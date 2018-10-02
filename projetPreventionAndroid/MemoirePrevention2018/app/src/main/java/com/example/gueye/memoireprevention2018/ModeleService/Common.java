package com.example.gueye.memoireprevention2018.ModeleService;

import com.example.gueye.memoireprevention2018.Remote.APIService;
import com.example.gueye.memoireprevention2018.Remote.RetrofitClient;

/**
 * Created by gueye on 15/09/18.
 */

public class Common {

    public static  String currentToken = "";

    private static String baseUrl = "https://fcm.googleapis.com/";

    public static APIService getFCMClient(){

        return RetrofitClient.getClient(baseUrl).create(APIService.class);
    }
}
