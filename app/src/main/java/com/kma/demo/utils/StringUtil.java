package com.kma.demo.utils;

public class StringUtil {

    public static boolean isEmpty(String input) {
        return input == null || input.isEmpty() || ("").equals(input.trim());
    }
}
