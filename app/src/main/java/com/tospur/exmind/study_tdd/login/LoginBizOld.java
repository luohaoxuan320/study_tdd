package com.tospur.exmind.study_tdd.login;

import com.tospur.exmind.study_tdd.net.HttpSerivce;
import com.tospur.exmind.study_tdd.net.RetrofitBuilder;

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
public class LoginBizOld {


    public void login(String name, String pw, final OnLoginCallback loginCallback) {
    //刚开始是这样写的，但是这样写 1.不方便测试，2.也破环方法的平行层级结构即步骤，他的步骤，一获取call对象，二执行。
        // HashMap<String, String> hashMap = new HashMap();
        // hashMap.put("loginId", name);
        // hashMap.put("password", pw);
        // Call<ResponseBody> login= RetrofitBuilder.getHttpService().login(hashMap);

        Call<ResponseBody> login = getLoginCall(name,pw);
        login.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loginCallback.onSuccess(new UserData());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


      Call<ResponseBody> getLoginCall(String name, String pw) {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("loginId", name);
        hashMap.put("password", pw);
        return RetrofitBuilder.getHttpService().login(hashMap);
    }

}
