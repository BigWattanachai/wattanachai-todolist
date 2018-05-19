package com.bot.wattanachaitodolist.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexUtil {
    public static Matcher patternMatch(String regex, String targetStr) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(targetStr);
    }
}
