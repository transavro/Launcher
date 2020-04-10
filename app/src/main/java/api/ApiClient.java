package api;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tv.cloudwalker.launcher.BuildConfig;

public class ApiClient {
    public static String BASE_URL = BuildConfig.SERVER_URL;
//    public static String BASE_URL = "http://192.168.0.106:9876/";
    private static Retrofit retrofit = null;


    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(CustomHttpClient.getHttpClient(context, BASE_URL))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
