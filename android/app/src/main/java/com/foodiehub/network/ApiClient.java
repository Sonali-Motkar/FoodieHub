package com.foodiehub.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {

    // 10.0.2.2 is the special IP that Android Emulator uses to connect to localhost on your PC
    // For a physical Android phone, change to your PC's Wi-Fi IP (e.g., http://192.168.1.X:5000/api/)
    public static String BASE_URL = "http://10.0.2.2:5000/api/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static void setCustomBaseUrl(String newUrl) {
        BASE_URL = newUrl.endsWith("/") ? newUrl : newUrl + "/";
        retrofit = null; // Reinitialize with new URL
    }
}
