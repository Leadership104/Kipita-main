package com.mytum.client.connection;


import com.mytum.client.connection.interceptors.ApiKeyInterceptor;
import com.mytum.client.exception.ErrorHandlingInterceptor;
import com.mytum.client.models.ApiKey;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
//import com.dawata.client.connection.interceptors.ApiKeyInterceptor;
//import com.dawata.client.exception.ErrorHandlingInterceptor;
//import com.dawata.client.models.ApiKey;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Ranga on 2/22/2017.
 */

public class YelpFusionApiFactory {
    private static final String YELP_API_BASE_URL = "https://api.yelp.com";
    private static final String advisryUrl = "http://216.250.127.172/dawat/";
    private static final String applicationurl = "http://143.244.149.211/applications/";
    private static OkHttpClient httpClient;
    private static ApiKey apiKey;
    private static Retrofit retrofit;
    public YelpFusionApiFactory() {}
    public static YelpFusionApi createAPI(String apiKeyData) throws IOException {
         apiKey = new ApiKey(apiKeyData, "Bearer");
        return getYelpFusionApi();
    }
    private static YelpFusionApi getYelpFusionApi() {
        httpClient = new OkHttpClient.Builder()
                .addInterceptor(new ApiKeyInterceptor(apiKey))
                .addInterceptor(new ErrorHandlingInterceptor())
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getAPIBaseUrl())
                .addConverterFactory(getJacksonFactory())
                .client(getOkHttpClient())
                .build();
        return retrofit.create(YelpFusionApi.class);
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit =
                    new Retrofit.Builder()
                            .baseUrl(advisryUrl)
                            .addConverterFactory(getGsonConverterFactory())
                            .client(getOkHttpClient())
                            .build();
        }
        return retrofit;
    }

    public static Retrofit getApplication() {
        if (retrofit == null) {
            retrofit =
                    new Retrofit.Builder()
                            .baseUrl(applicationurl)
                            .addConverterFactory(getGsonConverterFactory())
                            .client(getOkHttpClient())
                            .build();
        }
        return retrofit;
    }
    private static OkHttpClient getOkHttpClient() {
        if (httpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(90, TimeUnit.SECONDS)
                    .readTimeout(90, TimeUnit.SECONDS)
                    .writeTimeout(90, TimeUnit.SECONDS);
            httpClient = builder.build();
        }
        return httpClient;
    }
    private static JacksonConverterFactory getJacksonFactory(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return JacksonConverterFactory.create(mapper);
    }
    public static String getAPIBaseUrl() {
        return YELP_API_BASE_URL;
    }
    public static Converter.Factory getGsonConverterFactory() {
        return GsonConverterFactory.create(new GsonBuilder().disableHtmlEscaping().create());
    }
}

