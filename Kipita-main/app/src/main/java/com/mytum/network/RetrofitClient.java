package com.mytum.network;

import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
//    public static String PRODUCTION_URL = "http://143.244.149.211/applications/public_html/";
    public static String PRODUCTION_URL = "https://api.dwaat.com/";
    private static Retrofit retrofit;
    private static OkHttpClient client;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit =
                    new Retrofit.Builder()
                            .baseUrl(PRODUCTION_URL)
                            .addConverterFactory(getGsonConverterFactory())
                            .client(getOkHttpClient())
                            .build();
        }
        return retrofit;
    }
    private static OkHttpClient getOkHttpClient() {
        if (client == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
//                    .addInterceptor(new BasicAuthInterceptor(username, password))
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS);
            client = builder.build();
        }
        return client;
    }
    public static Converter.Factory getGsonConverterFactory() {
        return GsonConverterFactory.create(new GsonBuilder().disableHtmlEscaping().create());
    }

}
