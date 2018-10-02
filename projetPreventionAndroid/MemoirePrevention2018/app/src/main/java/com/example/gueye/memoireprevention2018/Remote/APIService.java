package com.example.gueye.memoireprevention2018.Remote;

import com.example.gueye.memoireprevention2018.ModeleService.MyResponse;
import com.example.gueye.memoireprevention2018.ModeleService.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by gueye on 15/09/18.
 */

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAfHt2iDE:APA91bEo10UV0RJSGCBeY3Rl4HptKPiqWr3RCeoNEP9gWmQhE3lniFRpc6Wnn8nH8p7wLnjtl5-P0nnzHMOs7d9A1jG3Lgyq0x6P8Kf9VavYfTJnQXZ-O9iyb6WZB1nVPq3ALeGUro2C"
    })

    @POST("fcm/send")

    Call<MyResponse> sendNotification(@Body Sender body);
}
