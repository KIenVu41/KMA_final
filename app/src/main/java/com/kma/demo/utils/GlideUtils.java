package com.kma.demo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kma.demo.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GlideUtils {

    public static void loadUrlBanner(String url, ImageView imageView) {
        if (StringUtil.isEmpty(url)) {
            imageView.setImageResource(R.drawable.img_no_image);
            return;
        }
//        Glide.with(imageView.getContext())
//                .load(url)
//                .error(R.drawable.img_no_image)
//                .dontAnimate()
//                .into(imageView);
        new DownloadImageTask(imageView).execute(url);
    }

    public static void loadUrl(String url, ImageView imageView) {
        if (StringUtil.isEmpty(url)) {
            imageView.setImageResource(R.drawable.image_no_available);
            return;
        }
//        Glide.with(imageView.getContext())
//                .load(url)
//                .error(R.drawable.image_no_available)
//                .dontAnimate()
//                .into(imageView);
        new DownloadImageTask(imageView).execute(url);
    }
}