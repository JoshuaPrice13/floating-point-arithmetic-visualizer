//This model handles all operations and conversions with the data from the panel

public class FPAV_Model {
    /**
     * Converts decimal number to IEEE 754 binary representation
     */
    public String decimalToIEEE754(double decimal) {
        long bits = Double.doubleToLongBits(decimal);
        return String.format("%64s", Long.toBinaryString(bits)).replace(' ', '0');
    }

    /**
     * Converts IEEE 754 binary representation back to decimal
     */
    public double ieee754ToDecimal(String ieee754) {
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

    
//============================================================================
//  OPERATIONS {
//============================================================================

    /**
     * Performs bitwise addition on two IEEE 754 numbers
     *  Parses IEEE 754 components
     *  Adds implicit leading 1 for normalized numbers
     *  Aligns mantissas by shifting based on exponent difference
     *  Performs actual binary addition on mantissa values
     *  Handles sign logic for different sign combinations
     *  Normalizes the result
     */
    public String bitwiseAddition(String ieee754_a, String ieee754_b) {
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
    
    /**
     * Performs bitwise subtraction on two IEEE 754 numbers
     */
    public String bitwiseSubtraction(String a, String b) {
        // We must get the components of the IEEE 754 to change the sign 
        int signB = b.charAt(0) - '0';
        int exponentB = Integer.parseInt(b.substring(1, 12), 2);
        String mantissaB = b.substring(12);
        
        // Handle zero cases
        if (isZero(a)) return flipSign(b);
        if (isZero(b)) return a;
        
        // Convert a - b to a + (-b) by flipping sign of b
        signB = 1 - signB;
        
        return bitwiseAddition(a, createIEEE754(signB, exponentB, mantissaB));
    }

    /**
     * Performs bitwise multiplication on two IEEE 754 numbers
     */
    public String bitwiseMultiplication(String a, String b) {
        // Parse multiplicand a
        int signA = a.charAt(0) - '0';
        int exponentA = Integer.parseInt(a.substring(1, 12), 2);
        String mantissaA = a.substring(12);
        
        // Parse multiplier b
        int signB = b.charAt(0) - '0';
        int exponentB = Integer.parseInt(b.substring(1, 12), 2);
        String mantissaB = b.substring(12);
        
        // Handle zero multi for effiency 
        if (isZero(a) || isZero(b)) {
            return (signA == signB) ? 
                "0000000000000000000000000000000000000000000000000000000000000000" : // +0
                "1000000000000000000000000000000000000000000000000000000000000000";   // -0
        }
        
        
        int resultSign = signA ^ signB;
        int resultExponent = exponentA + exponentB - 1023;
        
        // Add implicit leading 1 for mantissa multiplication
        long mantissaValueA = addImplicitOne(mantissaA, exponentA);
        long mantissaValueB = addImplicitOne(mantissaB, exponentB);
        
        // Perform mantissa multiplication using double precision
        double doubleA = (double)mantissaValueA / (1L << 52);
        double doubleB = (double)mantissaValueB / (1L << 52);
        double product = doubleA * doubleB;
        
        // Convert back to long mantissa
        long resultMantissa = (long)(product * (1L << 52));
        
        // Check if result needs normalization
        if (resultMantissa >= (1L << 53)) {
            resultMantissa >>>= 1;
            resultExponent++;
        }
        
        return normalizeResult(resultSign, resultExponent, resultMantissa);
    }

    /**
     * Performs bitwise division on two IEEE 754 numbers
     *  Parses IEEE 754 components
     *  Calculates result sign using XOR of input signs
     *  Calculates result exponent by subtracting exponents and adjusting bias
     *  Performs actual long division on mantissa values using performMantissaDivision
     *  Normalizes and reconstructs IEEE 754 format
     * @param a
     * @param b
     * @return
     */
    public String bitwiseDivision(String a, String b) {
        // Parse dividend (a)
        int signA = a.charAt(0) - '0';
        int exponentA = Integer.parseInt(a.substring(1, 12), 2);
        String mantissaA = a.substring(12);
        
        // Parse divisor (b)
        int signB = b.charAt(0) - '0';
        int exponentB = Integer.parseInt(b.substring(1, 12), 2);
        String mantissaB = b.substring(12);
        
        // Handle special cases
        if (isZero(b)) {
            return null;
        }
        
        if (isZero(a)) {
            // Zero divided by anything is zero
            return (signA == signB) ? 
                "0000000000000000000000000000000000000000000000000000000000000000" : // +0
                "1000000000000000000000000000000000000000000000000000000000000000";   // -0
        }
        
        // Calculate result sign
        int resultSign = signA ^ signB; // XOR for sign
        
        // Calculate result exponent
        int resultExponent = exponentA - exponentB + 1023; 
        
        // Add implicit leading 1
        long mantissaValueA = addImplicitOne(mantissaA, exponentA);
        long mantissaValueB = addImplicitOne(mantissaB, exponentB);

        if ( mantissaValueA == 0 || mantissaValueB == 0){
            return null;
        }
        
        // Perform long division on mantissas
        long resultMantissa = performMantissaDivision(mantissaValueA, mantissaValueB);
        
        // Normalize and create IEEE 754 result
        return normalizeResult(resultSign, resultExponent, resultMantissa);
    }
    
//============================================================================
//  } OPERATIONS
//============================================================================

    // Adds leading 1
    private static long addImplicitOne(String mantissa, int exponent) {
        long value = Long.parseUnsignedLong(mantissa, 2);
        if (exponent != 0) {
            value |= (1L << 52); // Add implicit leading 1
        }
        return value;
    }
    
    private static boolean isZero(String ieee754) {
        return ieee754.substring(1).equals("0".repeat(63));
    }
    
    private static String flipSign(String ieee754) {
        char newSign = (ieee754.charAt(0) == '0') ? '1' : '0';
        return newSign + ieee754.substring(1);
    }
    
    /**
     * Builds IEEE 754 binary representation from the sign, expo, and mantissa parts.
     * @param sign
     * @param exponent
     * @param mantissa
     * @return
     */
    private static String createIEEE754(int sign, int exponent, String mantissa) {
        return String.valueOf(sign) + 
               String.format("%11s", Integer.toBinaryString(exponent)).replace(' ', '0') + 
               mantissa;
    }

    
    /**
     * Bitwise division of the two binaries inputed. 
     * @param dividend
     * @param divisor
     * @return
     */
    private static long performMantissaDivision(long dividend, long divisor) {

        // 0 divided by anything is 0
        if (divisor == 0) return 0;
        if (dividend == 0) return 0;
        
        long shiftedDividend = dividend;
        long quotient = 0;
        
        // We want 53 bits of precision (including implicit 1)
        for (int i = 52; i >= 0; i--) {
            if (shiftedDividend >= divisor) {
                quotient |= (1L << i);
                shiftedDividend -= divisor;
            }
            shiftedDividend <<= 1;
        }
        
        // Add rounding based on remainder
        if (shiftedDividend >= divisor) {
            quotient++;
        }
        
        return quotient;
    }
    
    private static String normalizeResult(int sign, int exponent, long mantissa) {
        if ((mantissa & (1L << 53)) != 0) {
            mantissa >>>= 1;
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
}
