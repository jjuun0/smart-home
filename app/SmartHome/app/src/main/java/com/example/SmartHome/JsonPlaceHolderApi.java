package com.example.SmartHome;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface JsonPlaceHolderApi {
    @GET("people")  // 주소
    Call<List<People>> getPeople();  // HTTP 요청을 웹서버로 보낸다

//    @GET("people/JunHyoung")
//    Call<People> getName(@Query("Name") String Name);

    @POST("people")
    Call<People> createPeople(@Body People people);
}
