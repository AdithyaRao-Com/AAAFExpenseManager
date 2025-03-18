package com.adithya.aaafexpensemanager.util;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

public class MimeTypeUtils {

    public static String getMimeType(Context context, Uri uri) {
        ContentResolver cR = context.getContentResolver();
        String mime = cR.getType(uri);
        return mime;
    }
}