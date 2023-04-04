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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

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

    public static void decompressAndSave(InputStream compressedData, String fileName) {
        InputStream inputStream = compressedData;
        GZIPInputStream gzipInputStream = null;
        FileOutputStream fileOutputStream = null;
        File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File myFile = new File(downloadFolder, fileName);
        try {
            fileOutputStream = new FileOutputStream(myFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }
            // Giải nén byte[] thành file mp3
//            gzipInputStream = new GZIPInputStream(inputStream);
//            fileOutputStream = new FileOutputStream(myFile);
//
//            byte[] buffer = new byte[4096];
//            int len;
//            while ((len = gzipInputStream.read(buffer)) > 0) {
//                fileOutputStream.write(buffer, 0, len);
//            }
//
//            // Đóng các stream
//            fileOutputStream.close();
//            gzipInputStream.close();
//            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(gzipInputStream != null) {
                try {
                    gzipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //long cacheSize = getCacheSize(getApplicationContext());

    public long getCacheSize(Context context) {
        long size = 0;
        try {
            File cacheDir = context.getCacheDir();
            File[] files = cacheDir.listFiles();
            for (File file : files) {
                size += file.length();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }
}
