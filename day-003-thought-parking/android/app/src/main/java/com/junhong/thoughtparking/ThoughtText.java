package com.junhong.thoughtparking;

public final class ThoughtText {
    public static final int MAX_LENGTH = 120;

    private ThoughtText() {}

    public static String normalize(String value) {
        if (value == null) return "";
        String clean = value.trim().replaceAll("\\s+", " ");
        return clean.length() > MAX_LENGTH ? clean.substring(0, MAX_LENGTH) : clean;
    }
}
