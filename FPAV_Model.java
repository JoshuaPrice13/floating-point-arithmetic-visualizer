public class FPAV_Model {
    /**
     * Converts decimal number to IEEE 754 binary representation
     */
    public static String decimalToIEEE754(double decimal) {
        long bits = Double.doubleToLongBits(decimal);
        return String.format("%64s", Long.toBinaryString(bits)).replace(' ', '0');
    }

    /**
     * Performs bitwise addition on two IEEE 754 numbers
     *  Parses IEEE 754 components
     *  Adds implicit leading 1 for normalized numbers
     *  Aligns mantissas by shifting based on exponent difference
     *  Performs actual binary addition on mantissa values
     *  Handles sign logic for different sign combinations
     *  Normalizes the result
     */
    public static String binaryAddition(String ieee754_a, String ieee754_b) {
        // Parse numbers
        int signA = ieee754_a.charAt(0) - '0';
        int exponentA = Integer.parseInt(ieee754_a.substring(1, 12), 2);
        String mantissaA = ieee754_a.substring(12);
        
        int signB = ieee754_b.charAt(0) - '0';
        int exponentB = Integer.parseInt(ieee754_b.substring(1, 12), 2);
        String mantissaB = ieee754_b.substring(12);
        
        // Add implicit leading 1 for normalized numbers
        long mantissaValueA = Long.parseUnsignedLong(mantissaA, 2);
        if (exponentA != 0) {
            mantissaValueA |= (1L << 52); // Add implicit leading 1
        }
        long mantissaValueB = Long.parseUnsignedLong(mantissaB, 2);
        if (exponentB != 0) {
            mantissaValueB |= (1L << 52); // Add implicit leading 1
        }
        
        // Align mantissas by shifting the smaller exponent
        int expDiff = exponentA - exponentB;
        if (expDiff > 0) {
            mantissaValueB = mantissaValueB >> expDiff;
        } else if (expDiff < 0) {
            mantissaValueA = mantissaValueA >> (-expDiff);
            exponentA = exponentB;
        }
        
        long resultMantissa;
        int resultSign;
        int resultExponent = exponentA;
        
        if (signA == signB) {
            // Same sign so we add mantissas
            resultMantissa = mantissaValueA + mantissaValueB;
            resultSign = signA;
            
            if ((resultMantissa & (1L << 53)) != 0) {
                resultMantissa >>= 1;  // Right shift to normalize
                resultExponent++;       // Increment exponent
            }
        } else {
            // Different signs so we subtract mantissas
            if (mantissaValueA >= mantissaValueB) {
                resultMantissa = mantissaValueA - mantissaValueB;
                resultSign = signA;
            } else {
                resultMantissa = mantissaValueB - mantissaValueA;
                resultSign = signB;

                if (resultMantissa != 0) {
                    while ((resultMantissa & (1L << 52)) == 0 && resultExponent > 0) {
                        resultMantissa <<= 1;
                        resultExponent--;
                    }
                }
            }
        }
        
        // Handle zero result
        if (resultMantissa == 0) {
            return "0000000000000000000000000000000000000000000000000000000000000000";
        }
        
        // Normalize the result
        return normalizeResult(resultSign, resultExponent, resultMantissa);
    }

    private static String normalizeResult(int sign, int exponent, long mantissa) {
        if ((mantissa & (1L << 53)) != 0) {
            mantissa >>= 1;
            exponent++;
        }
        
        // Handle overflow/underflow
        if (exponent <= 0) {
            exponent = 0; 
        } else if (exponent >= 2047) {
            exponent = 2047; // Infinity
            mantissa = 0;
        }
        
        // Normalize mantissa (find leading 1 and adjust)
        if (mantissa != 0 && exponent > 0) {
            while ((mantissa & (1L << 52)) == 0 && exponent > 0) {
                mantissa <<= 1;
                exponent--;
            }
            mantissa &= ((1L << 52) - 1); // Remove implicit leading 1
        }
        
        // Construct IEEE 754 representation. Could've used contructing method but this look different from 
        // other calls of the function. I think this adds clarity 
        StringBuilder result = new StringBuilder();
        result.append(sign);
        result.append(String.format("%11s", Integer.toBinaryString(exponent)).replace(' ', '0'));
        result.append(String.format("%52s", Long.toBinaryString(mantissa)).replace(' ', '0'));
        
        return result.toString();
    }

    /**
     * Converts IEEE 754 binary representation back to decimal
     */
    public static double ieee754ToDecimal(String ieee754) {
        //Ensure we have exactly 64 bits for double precision
        if (ieee754.length() != 64) {
            ieee754 = String.format("%64s", ieee754).replace(' ', '0');
        }
        
        // Parse IEEE 754 components
        int sign = ieee754.charAt(0) - '0';
        int exponent = Integer.parseInt(ieee754.substring(1, 12), 2);
        String mantissaStr = ieee754.substring(12);
        
        // Handle all 1's in exponent 
        if (exponent == 2047) { // 
            if (mantissaStr.equals("0".repeat(52))) {
                return (sign == 0) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
            } else {
                return Double.NaN;
            }
        }
        
        // Handle zero
        if (exponent == 0 && mantissaStr.equals("0".repeat(52))) {
            return (sign == 0) ? 0.0 : -0.0;
        }
        
        // Calculate mantissa value
        double mantissaValue = 0.0;
        for (int i = 0; i < mantissaStr.length(); i++) {
            if (mantissaStr.charAt(i) == '1') {
                mantissaValue += Math.pow(2, -(i + 1));
            }
        }
        
        double result;
        if (exponent == 0) {
            result = mantissaValue * Math.pow(2, -1022);
        } else {
            // Normal number: add implicit leading 1
            mantissaValue += 1.0;
            result = mantissaValue * Math.pow(2, exponent - 1023);
        }
        
        // Apply sign
        return (sign == 0) ? result : -result;
    }
}
