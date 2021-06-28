package com.example.SmartHome;

import com.example.SmartHome.Face.FaceDB;
import com.example.SmartHome.Face.FaceLog;
import com.example.SmartHome.FingerPrint.FingerPrintDB;
import com.example.SmartHome.FingerPrint.FingerPrintLog;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface JsonPlaceHolderApi {
    @GET("people")  // allow data 조회
    Call<List<FaceDB>> getFaceDB();  // HTTP 요청을 웹서버로 보낸다

    @POST("people")  // 항목 추가
    Call<FaceDB> createFaceDB(@Body FaceDB faceDB);

    @DELETE("people/{Name}/delete")  // 항목 삭제
    Call<FaceDB> deleteFaceDB(@Path("Name") String Name);

    @GET("log") // 로그 조회
    Call<List<FaceLog>> getFaceLog();

    @GET("log/{correct}") // 로그 조회
    Call<List<FaceLog>> getFaceCorrectLog(@Path("correct") String Correct);

    @GET("fingerprint") // 로그 조회
    Call<List<FingerPrintDB>> getFingerPrintDB();

    @GET("fingerprint/log/{correct}") // 로그 조회
    Call<List<FingerPrintLog>> getFingerPrintLog(@Path("correct") String Correct);


}
