package com.example.eventplanner.ui.util;

import android.text.Editable;
import android.text.TextWatcher;


public interface SimpleTextWatcher extends TextWatcher {

    @Override
    default void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    default void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    default void afterTextChanged(Editable editable) {}



    static TextWatcher BeforeTextChanged(BeforeTextChangedListener listener) {
        return new SimpleTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listener.beforeTextChanged(charSequence, i, i1, i2);
            }
        };
    }

    static TextWatcher OnTextChanged(OnTextChangedListener listener) {
        return new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listener.onTextChanged(charSequence, i, i1, i2);
            }
        };
    }

    static TextWatcher AfterTextChanged(AfterTextChangedListener listener) {
        return new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                listener.afterTextChanged(editable);
            }
        };
    }


    @FunctionalInterface
    interface OnTextChangedListener {
        void onTextChanged(CharSequence text, int start, int before, int count);
    }

    @FunctionalInterface
    interface AfterTextChangedListener {
        void afterTextChanged(Editable editable);
    }

    @FunctionalInterface
    interface BeforeTextChangedListener {
        void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2);
    }

}
