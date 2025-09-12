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
    public static String bitwiseAddition(String ieee754_a, String ieee754_b) {
        // Parse numbers
        int signA = ieee754_a.charAt(0) - '0';
        int exponentA = Integer.parseInt(ieee754_a.substring(1, 12), 2);
        String mantissaA = ieee754_a.substring(12);
        
        int signB = ieee754_b.charAt(0) - '0';
        int exponentB = Integer.parseInt(ieee754_b.substring(1, 12), 2);
        String mantissaB = ieee754_b.substring(12);
        
        // Add implicit leading 1 for normalized numbers
        long mantissaValueA = addImplicitOne(mantissaA, exponentA);
        long mantissaValueB = addImplicitOne(mantissaB, exponentB);
        
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
        } else {
            // Different signs so we subtract mantissas
            if (mantissaValueA >= mantissaValueB) {
                resultMantissa = mantissaValueA - mantissaValueB;
                resultSign = signA;
            } else {
                resultMantissa = mantissaValueB - mantissaValueA;
                resultSign = signB;
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
}
