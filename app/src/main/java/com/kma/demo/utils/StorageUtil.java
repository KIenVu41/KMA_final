package com.kma.demo.utils;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;

public class StorageUtil {

    public static Cursor getMp3FileCursor(Context context) {
        ContentResolver cr = context.getContentResolver();
        //Uri uri = MediaStore.Files.getContentUri("external");
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=? AND " + MediaStore.Audio.Media.SIZE +  " > 0";
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");
        String[] selectionArgs = new String[]{mimeType};

        return cr.query(uri, null, selection, selectionArgs, null);
    }

    public static File[] getListFiles(String dirStr) {
        File dir = new File(dirStr);
        File[] files = dir.listFiles();
        if (files != null) {
            return files;
        }
        return null;
    }

    public static String getFileExtension(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("fileName must not be null!");
        }

        String extension = "";

        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            extension = fileName.substring(index + 1);
        }

        return extension;

    }
}
