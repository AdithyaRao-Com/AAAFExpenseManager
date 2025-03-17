package com.adithya.aaafexpensemanager.util;

import java.text.DecimalFormat;

public class CurrencyFormatter {
    public static String formatIndianStyle(Double value,String currency) {
        // Round to two decimal places
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String positiveNegativeInd = value < 0 ? "-" : "";
        String roundedNumberStr = decimalFormat.format(Math.abs(value));

        String integerPart;
        String decimalPart;

        // Split into integer and decimal parts
        if (roundedNumberStr.contains(".")) {
            int decimalIndex = roundedNumberStr.indexOf(".");
            integerPart = roundedNumberStr.substring(0, decimalIndex);
            decimalPart = roundedNumberStr.substring(decimalIndex); // Includes the dot
        } else {
            integerPart = roundedNumberStr;
            decimalPart = ".00"; // Default decimal part if no decimal point found
        }
        // Format the integer part
        String formattedInteger = formatIntegerPartIndian(integerPart);
        // Combine integer and decimal parts
        return positiveNegativeInd +formattedInteger + decimalPart + " "+currency;
    }
    public static String formatStandardStyle(double number,String currency) {
        // Round to two decimal places
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String positiveNegativeInd = number < 0 ? "-" : "";
        String roundedNumberStr = decimalFormat.format(Math.abs(number));
        String integerPart;
        String decimalPart;
        // Split into integer and decimal parts
        if (roundedNumberStr.contains(".")) {
            int decimalIndex = roundedNumberStr.indexOf(".");
            integerPart = roundedNumberStr.substring(0, decimalIndex);
            decimalPart = roundedNumberStr.substring(decimalIndex);
        } else {
            integerPart = roundedNumberStr;
            decimalPart = ".00"; // Default decimal part if no decimal point found
        }
        String formattedInteger = formatIntegerPartStandard(integerPart);
        return positiveNegativeInd+formattedInteger + decimalPart + " "+currency;
    }
    private static String formatIntegerPartIndian(String integerPart) {
        int len = integerPart.length();
        if (len <= 3) {
            return integerPart;
        }
        StringBuilder formatted = new StringBuilder();
        formatted.append(integerPart.substring(len - 3)); // Last three digits
        len -= 3;
        while (len > 0) {
            formatted.insert(0, ",");
            if (len >= 2) {
                formatted.insert(0, integerPart.substring(len - 2, len));
                len -= 2;
            } else {
                formatted.insert(0, integerPart.charAt(0));
                len = 0;
            }
        }
        return formatted.toString();
    }
    private static String formatIntegerPartStandard(String integerPart) {
        int len = integerPart.length();
        if (len <= 3) {
            return integerPart;
        }
        StringBuilder formatted = new StringBuilder();
        int commaCount = 0;
        for (int i = len - 1; i >= 0; i--) {
            formatted.insert(0, integerPart.charAt(i));
            commaCount++;
            if (commaCount % 3 == 0 && i != 0) {
                formatted.insert(0, ",");
            }
        }
        return formatted.toString();
    }
}
