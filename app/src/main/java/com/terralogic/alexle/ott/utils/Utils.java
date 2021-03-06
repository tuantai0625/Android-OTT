package com.terralogic.alexle.ott.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alex.le on 01-Aug-17.
 */

public final class Utils {
    public static final String EXTRA_USER = "EXTRA_USER";
    public static final String ARG_USER = "ARG_USER";
    public static final String ARG_EMAIL = "ARG_EMAIL";
    public static final String ARG_CHIPID = "ARG_CHIPID";

    public static boolean isValidEmail(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isRequiredFieldsFilled(EditText... fields) {
        for (EditText field : fields) {
            if (TextUtils.isEmpty(field.getText())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidPassword(EditText fieldPassword, EditText fieldConfirmPassword) {
        return fieldPassword.getText().toString().equals(fieldConfirmPassword.getText().toString());
    }

    public static boolean isValidJSON(String text) {
        try {
            new JSONObject(text);
        } catch (JSONException ex) {
            try {
                new JSONArray(text);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static void hideKeyboard(Context context) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = ((Activity) context).getCurrentFocus();
        if (view == null) return;
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
