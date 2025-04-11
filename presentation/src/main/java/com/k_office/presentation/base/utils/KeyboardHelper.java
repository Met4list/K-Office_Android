package com.k_office.presentation.base.utils;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public final class KeyboardHelper {

    private KeyboardHelper() {
    }

    public static void hide(Context context, View view) {
        if (context == null) return;

        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (view != null) {
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void hide(Activity activity) {
        hide(activity, activity.getWindow().getDecorView());
    }

    public static void show(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.toggleSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), InputMethodManager.SHOW_IMPLICIT, 0);
    }
}