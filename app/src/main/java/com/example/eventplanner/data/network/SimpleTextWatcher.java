package com.example.eventplanner.data.network;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.function.Consumer;

public class SimpleTextWatcher implements TextWatcher {
    private final Consumer<String> onTextChanged;

    public SimpleTextWatcher(Consumer<String> onTextChanged) {
        this.onTextChanged = onTextChanged;
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override public void afterTextChanged(Editable s) {}
    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        onTextChanged.accept(s.toString());
    }
}

