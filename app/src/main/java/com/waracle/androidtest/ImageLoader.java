package com.waracle.androidtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;

/**
 * Created by Riad on 20/05/2015.
 */
public class ImageLoader {

    private static final String TAG = ImageLoader.class.getSimpleName();
    private  ImageView imageView;

    /**
     * Simple function for loading a bitmap image from the web
     *
     * @param url       image url
     * @param imageView view to set image too.
     */
    public void load(String url, ImageView imageView) {
        if (TextUtils.isEmpty(url)) {
            throw new InvalidParameterException("URL is empty!");
        }

        // Can you think of a way to improve loading of bitmaps
        // that have already been loaded previously??

        // In the link below is described a way to reuse bitmaps.
        // https://developer.android.com/topic/performance/graphics/manage-memory.html
        // However, if it was allowed to use third party libraries,
        // I would definitely use Glide or Picasso for image loading.

        this.imageView = imageView;

        new LoadImageAsyncTask().execute(url);
    }


    private Bitmap convertToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    private  void setImageView(byte[] imageBytes) {
        Bitmap bitmap = convertToBitmap(imageBytes);
        imageView.setImageBitmap(bitmap);
}

    private class LoadImageAsyncTask extends AsyncTask<String, Void, byte[]> {

        @Override
        protected byte[] doInBackground(String... urls) {
            if (urls.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;

            String imageUrlString;

            try {
                imageUrlString = urls[0];

                URL url = new URL(imageUrlString);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a byte array
                InputStream inputStream = urlConnection.getInputStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                int nRead;
                byte[] data = new byte[16384];

                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

               return buffer.toByteArray();

            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

        }

        @Override
        protected void onPostExecute(byte[] imageBytes) {
            super.onPostExecute(imageBytes);

            setImageView(imageBytes);
        }
    }
}
