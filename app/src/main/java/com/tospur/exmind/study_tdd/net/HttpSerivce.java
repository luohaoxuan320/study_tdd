package com.tospur.exmind.study_tdd.net;

import java.util.HashMap;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by lehow on 2016/9/5.
 * 内容摘要：
 * 版权所有：极策科技
 */
public interface HttpSerivce {
    @POST("v1/sessions/appLogin")
    Call<ResponseBody> login(@Body HashMap<String,String> values);
    @POST("v1/sessions/appLogin")
    Call<NetResult<User>> loginUser(@Body HashMap<String,String> values);
    @POST("v1/sessions/appLogin")
    Observable<NetResult<User>> loginObservable(@Body HashMap<String, String> values);

    @POST("v1/sessions/appLogin")
    Flowable<NetResult<User>> loginFlowable(@Body HashMap<String, String> values);
}
