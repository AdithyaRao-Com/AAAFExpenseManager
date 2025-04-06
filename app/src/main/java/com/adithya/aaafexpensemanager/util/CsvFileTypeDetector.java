package com.adithya.aaafexpensemanager.util;

import android.content.Context;
import android.net.Uri;

import java.util.List;

public class CsvFileTypeDetector {

    public static boolean isLikelyCsv(Context context, Uri fileUri) {
        try {
            List<String> headers = CsvHeaderUtils.getCsvHeaders(context, fileUri);
            if (headers.isEmpty()) return false;
            else if (headers.get(1).equals("Date")) return true;
            else if (headers.get(0).equals("Date")) return true;
            else return false;
        } catch (Exception e) {
            return false;
        }
    }
}