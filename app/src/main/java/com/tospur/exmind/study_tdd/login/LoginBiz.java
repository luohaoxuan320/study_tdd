package com.tospur.exmind.study_tdd.login;

import com.tospur.exmind.study_tdd.net.HttpSerivce;

import java.util.HashMap;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lehow on 2016/8/22.
 * 内容摘要：
 * 版权所有：极策科技
 */
public class LoginBiz {


    HttpSerivce httpSerivce;

    @Inject
    public LoginBiz(HttpSerivce httpSerivce) {
        this.httpSerivce = httpSerivce;
    }

    public void login(String name, String pw, final OnLoginCallback loginCallback) {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("loginId", name);
        hashMap.put("password", pw);
        Call<ResponseBody> login = httpSerivce.login(hashMap);
        login.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loginCallback.onSuccess(new UserData());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loginCallback.onFailed("err");
            }
        });
    }


     private Call<ResponseBody> getLoginCall(String name, String pw) {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("loginId", name);
        hashMap.put("password", pw);
        return httpSerivce.login(hashMap);
    }

}
