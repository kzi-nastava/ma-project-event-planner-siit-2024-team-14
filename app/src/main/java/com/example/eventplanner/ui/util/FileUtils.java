package com.example.eventplanner.ui.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FileUtils {

    public static MultipartBody.Part toJsonPart(String partName, String jsonString) {
        RequestBody body = RequestBody.create(
                jsonString,
                MediaType.parse("application/json")
        );
        return MultipartBody.Part.createFormData(partName, "dto.json", body);
    }

    public static @Nullable MultipartBody.Part toImagePart(Context ctx, String partName, Uri uri, String desiredFileName) {
        if (uri == null) return null;
        try {
            byte[] bytes = readAll(ctx.getContentResolver(), uri);
            String mime = ctx.getContentResolver().getType(uri);
            if (mime == null) mime = "image/*";
            RequestBody req = RequestBody.create(bytes, MediaType.parse(mime));
            String fileName = desiredFileName != null ? desiredFileName : getDisplayName(ctx.getContentResolver(), uri);
            if (fileName == null) fileName = "photo.png";
            return MultipartBody.Part.createFormData(partName, fileName, req);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] readAll(ContentResolver cr, Uri uri) throws IOException {
        try (InputStream in = cr.openInputStream(uri);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            if (in == null) return new byte[0];
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) >= 0) bos.write(buf, 0, n);
            return bos.toByteArray();
        }
    }

    private static @Nullable String getDisplayName(ContentResolver cr, Uri uri) {
        String result = null;
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor != null) {
            try {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (cursor.moveToFirst() && nameIndex >= 0) {
                    result = cursor.getString(nameIndex);
                }
            } finally {
                cursor.close();
            }
        }
        return result;
    }
}
