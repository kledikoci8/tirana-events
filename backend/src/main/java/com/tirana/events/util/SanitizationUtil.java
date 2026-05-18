package com.tirana.events.util;

public class SanitizationUtil {
    
    public static String sanitizeText(String input) {
        if (input == null) {
            return null;
        }
        // Remove all HTML tags and escape special characters
        return input.replaceAll("<[^>]*>", "")
                   .replaceAll("&", "&amp;")
                   .replaceAll("<", "&lt;")
                   .replaceAll(">", "&gt;")
                   .replaceAll("\"", "&quot;")
                   .replaceAll("'", "&#x27;")
                   .replaceAll("/", "&#x2F;");
    }
    
    public static String sanitizeHtml(String input) {
        if (input == null) {
            return null;
        }
        // Allow only basic formatting tags
        String cleaned = input.replaceAll("<(?!/?(?:b|i|u|em|strong|p|br)\\b)[^>]*>", "");
        return cleaned;
    }
}
