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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

    public static void convertInputStreamToMp3File(InputStream inputStream, String filePath) {
        FileOutputStream outputStream = null;
        try {
            File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File outputFile = new File(downloadFolder, filePath);

            outputStream = new FileOutputStream(outputFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            // Close the input and output streams
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                   e.printStackTrace();
                }
            }
        }
    }
}
