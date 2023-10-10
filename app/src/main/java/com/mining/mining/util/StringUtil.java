package com.mining.mining.util;

import android.widget.EditText;

public class StringUtil {
    public static String getText(EditText textView) {
        return textView.getText().toString();
    }

    public static int isText(EditText editText) {
        String text = getText(editText);
        return text.length();
    }

}
