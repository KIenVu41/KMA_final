package com.kma.demo.data.local.cache;

import android.content.Context;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class CacheManager {
    private Context context;

    public CacheManager(Context context) {
        this.context = context;
    }

    public void writeJson(Object object, Type type, String fileName) {
        File file = new File(context.getCacheDir(), fileName);
        OutputStream outputStream = null;
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
        try {
            outputStream = new FileOutputStream(file);
            BufferedWriter bufferedWriter;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,
                        StandardCharsets.UTF_8));
            } else {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            }

            gson.toJson(object, type, bufferedWriter);
            bufferedWriter.close();

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                }
            }
        }

    }


    public Object readJson(Type type, String fileName) {
        Object jsonData = null;

        File file = new File(context.getCacheDir(), fileName);
        InputStream inputStream = null;
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
        try {
            inputStream = new FileInputStream(file);
            InputStreamReader streamReader;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                streamReader = new InputStreamReader(inputStream,
                        StandardCharsets.UTF_8);
            } else {
                streamReader = new InputStreamReader(inputStream, "UTF-8");
            }

            jsonData = gson.fromJson(streamReader, type);
            streamReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return jsonData;
    }
}
