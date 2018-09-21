package com.archermind.music;

import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by archermind on 2/8/18.
 */

public class UriUtils {
    public static Uri getUri(String path){
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/");
        String origin = path;
        String first = origin.replace("/mnt/media_rw/","");
        String second = first.replaceFirst("/",":");
        String third = "";
        try {
            third = URLEncoder.encode(second, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Uri last = uri.withAppendedPath(uri,third);

        return last;
    }
}
