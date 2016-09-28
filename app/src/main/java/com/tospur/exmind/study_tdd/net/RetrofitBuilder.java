package com.tospur.exmind.study_tdd.net;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lehow on 2016/9/5.
 * 内容摘要：
 * 版权所有：极策科技
 */
public class RetrofitBuilder {

    private static final Retrofit RETROFIT=new Retrofit.Builder().baseUrl("http://172.18.84.243:8080/agent_cloud/").addConverterFactory(GsonConverterFactory.create()).build();

    public static HttpSerivce getHttpService(){
        return RETROFIT.create(HttpSerivce.class);
    }
}
