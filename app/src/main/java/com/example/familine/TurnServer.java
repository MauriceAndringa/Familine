package com.example.familine;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;

public interface TurnServer {
    @PUT("/_turn/<xyrsys_channel>")
    Call<TurnServerPojo> getIceCandidates(@Header("Authorization") String authkey);
}
