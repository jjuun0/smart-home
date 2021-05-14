package com.example.dynamodb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {
    @GET("people")  // 주소
    Call<List<People>> getPeople();  // HTTP 요청을 웹서버로 보낸다

//    @GET("people/JunHyoung")
//    Call<People> getName(@Query("Name") String Name);

    @POST("people")
    Call<People> createPeople(@Body People people);
}
