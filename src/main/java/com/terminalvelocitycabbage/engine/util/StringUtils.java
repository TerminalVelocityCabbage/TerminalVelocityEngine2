package com.terminalvelocitycabbage.engine.util;

public class StringUtils {

    /**
     * @param source The source String to test a value from
     * @param startDelimiter The String that starts the query bound
     * @param endDelimiter The String that ends the Query bound
     * @return The value between start and end delimiters
     */
    public static String getStringBetween(String source, String startDelimiter, String endDelimiter) {
        int startIndex = source.indexOf(startDelimiter);
        int endIndex = source.indexOf(endDelimiter, startIndex);
        if (startIndex < 0 || endIndex < startIndex) return null;
        return source.substring(startIndex + startDelimiter.length(), endIndex);
    }

}
