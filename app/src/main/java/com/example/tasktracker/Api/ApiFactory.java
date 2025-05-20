package com.example.tasktracker.Api;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFactory {
    //private static final String BASE_URL = "http://147.45.255.215:8080/";
    private static final String BASE_URL = "http://192.168.0.3:8080/";

    private static ApiService apiService = null;

    public static ApiService getApiService()
    {
        if(apiService == null)
        {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}
