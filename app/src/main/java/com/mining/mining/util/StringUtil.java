package com.mining.mining.util;

import android.widget.EditText;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringUtil {
    public static String getText(EditText textView) {
        return textView.getText().toString();
    }

    public static int isText(EditText editText) {
        String text = getText(editText);
        return text.length();
    }

    public static boolean isTimeExceeded(String targetTime) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(targetTime, formatter);
            LocalDateTime currentDateTime = LocalDateTime.now();
            if (dateTime.isBefore(currentDateTime)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
