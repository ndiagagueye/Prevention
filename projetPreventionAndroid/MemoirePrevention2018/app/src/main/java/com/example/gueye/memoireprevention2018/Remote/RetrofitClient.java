package com.example.gueye.memoireprevention2018.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gueye on 15/09/18.
 */

public class RetrofitClient {

    public static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl){

        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit ;
    }
}
