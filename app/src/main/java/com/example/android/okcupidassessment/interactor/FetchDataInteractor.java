package com.example.android.okcupidassessment.interactor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.util.Log;

import com.example.android.okcupidassessment.model.User;
import com.example.android.okcupidassessment.util.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sohail on 2/16/16.
 *
 * This class is concerned with networking calls: getting data, parsing data, downloading images.
 */
public class FetchDataInteractor {

    private final String TAG = this.getClass().getSimpleName();

    public static FetchDataInteractor fetchDataInteractor = null;

    private FetchDataInteractor() {

    }

    /**
     * Gets static instance of the class. This avoids creating multiple instances of the class
     */
    public static FetchDataInteractor getInstance() {
        if (fetchDataInteractor == null) fetchDataInteractor = new FetchDataInteractor();
        return fetchDataInteractor;
    }

    /**
     * using the path provided, downloads image via HTTPUrlConnection
     * @param path url to the image
     * @return bitmap
     */
    public Bitmap downloadImage(String path) {

        if (Utils.DEBUG)
            Log.i(TAG + " ON UI THREAD = ",
                    String.valueOf(Looper.myLooper() == Looper.getMainLooper()));

        InputStream inputStream = null;
        Bitmap bitmap = null;

        try {
            URL imageUrl = new URL(path);
            URLConnection urlConnection = imageUrl.openConnection();
            inputStream = urlConnection.getInputStream();

            bitmap = BitmapFactory.decodeStream(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();   // close input stream
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    /**
     * This method is used to request the json data using the {@link Utils.REQUEST_URL}
     * @return a list of {@link User} objects
     */
    public List<User> requestData() {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        List<User> userData = new ArrayList<>();

        try {
            // get the data
            URL url = new URL(Utils.REQUEST_URL);
            URLConnection urlConnection = url.openConnection();
            inputStream = new BufferedInputStream(urlConnection.getInputStream());

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder data = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                data.append(line);
            }

            // convert json data to JsonArray, iterate over it and
            // using Gson library parse data to java objects
            try {
                JSONObject json = new JSONObject(data.toString());
                JSONArray jsonArray = json.getJSONArray("data");

                Gson gson = new GsonBuilder().create();
                for (int i = 0; i < jsonArray.length(); i++) {
                    User user = gson.fromJson(jsonArray.get(i).toString(), User.class);

                    // convert 4 digit number to percent
                    user.setMatch(convertToPercent(user.getMatch()));

                    userData.add(user);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (bufferedReader != null) bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return userData;
    }

    /**
     * add decimal and round a four digit value
     */
    private int convertToPercent(int val) {
        return (int) Math.round(val / 100.00);
    }

}