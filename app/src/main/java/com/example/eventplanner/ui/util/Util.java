package com.example.eventplanner.ui.util;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.Observer;


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

    /// Returns an {@link Observer} that shows a short {@link Toast} message when the observed value changes.
    /// Toast is showed in the given {@link Context} with message obtained by calling {@link Object#toString} from the value.
    public static <T> Observer<T> toastError(final Context context) {
        return err -> {
            if (err != null)
                Toast.makeText(context, err.toString(), Toast.LENGTH_SHORT).show();
        };
    }

}
