package com.example.eventplanner.ui.util;

public abstract class Util {

    public static int parseInt(String text) {
        return Integer.parseInt(text, 0);
    }

    public static int parseInt(String text, int defaultValue) {
        try {
            return parseInt(text);
        } catch (NumberFormatException | NullPointerException ignored) {
            return defaultValue;
        }
    }

    public static double parseDouble(String text) {
        return Double.parseDouble(text);
    }

    public static double parseDouble(String text, double defaultValue) {
        try {
            return parseDouble(text);
        } catch (NumberFormatException | NullPointerException ignored) {
            return defaultValue;
        }
    }

}
