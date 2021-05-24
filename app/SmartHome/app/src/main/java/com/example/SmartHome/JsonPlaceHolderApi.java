package com.example.SmartHome;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface JsonPlaceHolderApi {
    @GET("people")  // 주소
    Call<List<People>> getPeoples();  // HTTP 요청을 웹서버로 보낸다

    @POST("people")
    Call<People> createPeople(@Body People people);

    @DELETE("people/{Name}/delete")
    Call<People> deletePeople(@Path("Name") String Name);
}
