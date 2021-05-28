package com.example.SmartHome;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface JsonPlaceHolderApi {
    @GET("people")  // allow data 조회
    Call<List<People>> getPeoples();  // HTTP 요청을 웹서버로 보낸다

    @POST("people")  // 항목 추가
    Call<People> createPeople(@Body People people);

    @DELETE("people/{Name}/delete")  // 항목 삭제
    Call<People> deletePeople(@Path("Name") String Name);

    @GET("log") // 로그 조회
    Call<List<LogTable>> getLog();
}
