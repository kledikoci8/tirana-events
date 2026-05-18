package com.tirana.events.util;

/**
 * FIX A4: Utility class for SQL-related operations
 * Prevents SQL injection and wildcard abuse in LIKE queries
 */
public class SqlUtil {
    
    /**
     * Escapes special characters in LIKE patterns to prevent wildcard injection
     * Escapes: % (matches any string), _ (matches any character), \ (escape character)
     * 
     * @param input The user input to escape
     * @return Escaped string safe for use in LIKE queries
     */
    public static String escapeLikePattern(String input) {
        if (input == null) {
            return null;
        }
        
        return input
            .replace("\\", "\\\\")  // Escape backslash first
            .replace("%", "\\%")    // Escape percent wildcard
            .replace("_", "\\_");   // Escape underscore wildcard
    }
    
    /**
     * Escapes and wraps input for LIKE query with wildcards
     * Example: "test" becomes "%test%"
     * 
     * @param input The user input to escape and wrap
     * @return Escaped and wrapped string for LIKE queries
     */
    public static String escapeLikePatternWithWildcards(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "%";
        }
        
        String escaped = escapeLikePattern(input.trim());
        return "%" + escaped + "%";
    }
}
