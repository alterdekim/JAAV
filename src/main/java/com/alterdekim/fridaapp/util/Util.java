package com.alterdekim.fridaapp.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class Util {
    public static String readTextFromUri(Context context, Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream =
                     context.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }

    public static String getFilenameFromUri(Context context, Uri uri) {
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst() && cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME) >= 0) {
                return cursor.getString(Math.abs(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
            }
        }
        return "default";
    }
}
