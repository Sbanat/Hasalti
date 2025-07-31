package com.example.hesalaty.Api;

import com.example.hesalaty.models.LoginResponse;
import com.example.hesalaty.models.LoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @Headers("Accept: application/json")
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);

}
