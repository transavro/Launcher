package utils;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import model.MovieResponse;

public class AppUtils {

    public MovieResponse readJSONFromAsset(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName); //shoudl include ".json" in it
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            return gson.fromJson(json, MovieResponse.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
