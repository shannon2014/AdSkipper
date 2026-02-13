package com.adskipper.detector;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SkipPatternDetector {
    
    // Strict text patterns for skip buttons (Chinese and English)
    private static final String[] SKIP_TEXT_PATTERNS = {
        "跳过", "skip", "跳過廣告", "跳过广告", "skip ad", "skip ads",
        "×", "x", "✕", "✖",
        "点击跳过", "點擊跳過"
    };

    // Regex patterns from the provided strategy image
    private static final List<Pattern> COMPLEX_SKIP_PATTERNS = new ArrayList<>();

    static {
        // [s秒c] matches 's', '秒', or 'c' (for close/count maybe?)
        // \s* matches optional whitespace
        // \d+ matches one or more digits
        String[] regexStrings = {
            "\\s*\\d+\\s*[s秒c]\\s*后关闭\\s*",      // e.g. "5s后关闭"
            "\\s*\\d+\\s*[s秒c]\\s*后关闭广告\\s*",  // e.g. "5秒后关闭广告"
            "\\s*\\d+\\s*[s秒c]\\s*后跳过\\s*",      // e.g. "5s后跳过"
            "\\s*\\d+\\s*[s秒c]\\s*后跳过广告\\s*",  // e.g. "5秒后跳过广告"
            
            "\\s*\\d*[s秒c]*\\s*关闭\\s*\\d*[s秒c]*\\s*",     // e.g. "5s关闭", "关闭5s"
            "\\s*\\d*[s秒c]*\\s*跳过\\s*\\d*[s秒c]*\\s*",     // e.g. "5s跳过", "跳过5s"
            "\\s*\\d*[s秒c]*\\s*跳过广告\\s*\\d*[s秒c]*\\s*", // e.g. "5s跳过广告", "跳过广告5s"
            "\\s*\\d*[s秒c]*\\s*关闭广告\\s*\\d*[s秒c]*\\s*", // e.g. "5s关闭广告", "关闭广告5s"
            
            ".*\\d+[s秒c]*\\s*关闭",       // e.g. "something 5s 关闭"
            ".*\\d+[s秒c]*\\s*跳过",       // e.g. "something 5s 跳过"
            ".*\\d+[s秒c]*\\s*跳过广告",   // e.g. "something 5s 跳过广告"
            ".*\\d+[s秒c]*\\s*关闭广告",   // e.g. "something 5s 关闭广告"
            
            "\\d+[s秒c]*\\s*关闭[\\s|].*",      // e.g. "5s 关闭 | something"
            "\\d+[s秒c]*\\s*跳过[\\s|].*",      // e.g. "5s 跳过 | something"
            "\\d+[s秒c]*\\s*跳过广告[\\s|].*",  // e.g. "5s 跳过广告 | something"
            "\\d+[s秒c]*\\s*关闭广告[\\s|].*"   // e.g. "5s 关闭广告 | something"
        };

        for (String regex : regexStrings) {
            COMPLEX_SKIP_PATTERNS.add(Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
        }
    }
    
    /**
     * Check if node is likely a skip button based on text content
     */
    public static boolean matchesTextPattern(AccessibilityNodeInfo node) {
        if (node == null) {
            return false;
        }
        
        CharSequence text = node.getText();
        CharSequence contentDesc = node.getContentDescription();
        
        boolean textMatch = text != null && !text.toString().isEmpty() && checkText(text.toString());
        boolean descMatch = contentDesc != null && !contentDesc.toString().isEmpty() && checkText(contentDesc.toString());

        return textMatch || descMatch;
    }

    private static boolean checkText(String text) {
        if (text == null) return false;
        
        // Optimization: Skip buttons usually have short text.
        // Avoid running regex on long text which can cause ANRs (APP_SCOUT_HANG).
        if (text.length() > 30) return false;

        String lowerText = text.toLowerCase().trim();

        // 1. Explicit exclusion
        if (lowerText.equals("关闭") || lowerText.equals("close") || lowerText.equals("cancel") || lowerText.equals("取消")) {
            return false;
        }

        // 2. Exact match for simple patterns
        for (String pattern : SKIP_TEXT_PATTERNS) {
            // For single characters like 'x', exact match is preferred
            if (pattern.length() <= 1) {
                if (lowerText.equals(pattern.toLowerCase())) return true;
            } else {
                // For longer patterns, if the text IS the pattern, match it.
                if (lowerText.equals(pattern.toLowerCase())) return true;
            }
        }
        
        // 3. Regex match
        for (Pattern pattern : COMPLEX_SKIP_PATTERNS) {
            if (pattern.matcher(lowerText).matches()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Main detection method
     * Simplified matching strategy: matches only text/contentDescription patterns.
     * ID matching and confidence scoring have been removed.
     *
     * @param node The node to check
     * @param minConfidence parameter is ignored in this simplified strategy
     */
    public static boolean isSkipButton(AccessibilityNodeInfo node) {
        return matchesTextPattern(node);
    }
}