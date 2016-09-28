package com.tospur.exmind.study_tdd.testNet;

import com.tospur.exmind.study_tdd.BuildConfig;
import com.tospur.exmind.study_tdd.net.HttpSerivce;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by lehow on 2016/9/12.
 * 内容摘要：
 * 版权所有：极策科技
 */
@RunWith(RobolectricTestRunner.class) @Config(constants = BuildConfig.class,sdk =23)
public class TestGithubApi {

    @Test
    public void testApi(){
        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl("https://api.github.com/").build();
        GithubApi githubApi = retrofit.create(GithubApi.class);
        Call<ResponseBody> devinshine = githubApi.listRepos("devinshine");
        try {
            Response<ResponseBody> execute = devinshine.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void testMyApi(){
        HttpSerivce httpSerivce = new Retrofit.Builder().baseUrl("http://172.18.84.243:8080/agent_cloud/").addConverterFactory(GsonConverterFactory.create()).build().create(HttpSerivce.class);
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");
        Call<ResponseBody> login = httpSerivce.login(hashMap);
        try {
            Response<ResponseBody> execute = login.execute();
            execute.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    interface GithubApi{
        @GET("users/{user}/repos")
        public Call<ResponseBody> listRepos(@Path("user") String user);

    }



}
