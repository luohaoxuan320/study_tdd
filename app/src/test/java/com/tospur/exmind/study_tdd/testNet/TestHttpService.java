package com.tospur.exmind.study_tdd.testNet;

import android.support.annotation.NonNull;
import android.util.Log;

import com.tospur.exmind.study_tdd.net.HttpSerivce;
import com.tospur.exmind.study_tdd.net.NetResult;
import com.tospur.exmind.study_tdd.net.RetrofitBuilder;
import com.tospur.exmind.study_tdd.net.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lehow on 2016/9/8.
 * 内容摘要：
 * 版权所有：极策科技
 */
@RunWith(RobolectricTestRunner.class) @Config(constants = com.tospur.exmind.study_tdd.testNet.BuildConfig.class,sdk =23)
public class TestHttpService {

    private HttpSerivce httpSerivce;
    @Before
    public void setUp(){
//        httpSerivce= new Retrofit.Builder().baseUrl("http://172.18.84.243:8080/agent_cloud/").addConverterFactory(GsonConverterFactory.create()).build().create(HttpSerivce.class);

        httpSerivce = RetrofitBuilder.getHttpService();
    }

    /**
     * 同步测试API
     */
    @Test
    @Config(constants = com.tospur.exmind.study_tdd.testNet.BuildConfig.class,sdk =23)
    public void testLoginSync(){
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");
        Call<ResponseBody> login = httpSerivce.login(hashMap);
        Log.i("TestHttpService", "===begin===");
        System.out.println("===begin===");
        try {
            Response<ResponseBody> responseBody=login.execute();
            System.out.println(responseBody.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("TestHttpService", "===end===");
        System.out.println("===end===");
    }


    /**
     * 测试拦截器，添加Header和网络返回日志
     */
    @Test
    @Config(constants = com.tospur.exmind.study_tdd.testNet.BuildConfig.class,sdk =23)
    public void testLoggingInterceptor(){
        HttpSerivce httpSerivce = new Retrofit.Builder().baseUrl("http://172.18.84.243:8080/agent_cloud/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(headInterceptor).addInterceptor(loggingInterceptor).build())
                .build().create(HttpSerivce.class);
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");
        Call<ResponseBody> login = httpSerivce.login(hashMap);
        try {
            Response<ResponseBody> execute = login.execute();
            System.out.println(execute.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






    @Test
    @Config(constants = com.tospur.exmind.study_tdd.testNet.BuildConfig.class,sdk =23)
    public void testLoginParseData(){
        HttpSerivce httpSerivce = new Retrofit.Builder().baseUrl("http://172.18.84.243:8080/agent_cloud/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(headInterceptor).addInterceptor(loggingInterceptor).build())
                .build().create(HttpSerivce.class);
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");
        Call<NetResult<User>> login = httpSerivce.loginUser(hashMap);
        try {
            Response<NetResult<User>> execute = login.execute();
            System.out.println(execute.body().getMsg());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Config(constants = com.tospur.exmind.study_tdd.testNet.BuildConfig.class,sdk =23)
    public void testLoginAsyncToSync (){
        HttpSerivce httpSerivce = new Retrofit.Builder().baseUrl("http://172.18.84.243:8080/agent_cloud/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder()
                        .addInterceptor(headInterceptor)
                        .addInterceptor(loggingInterceptor)
                        .dispatcher(dispatcher)
                        .build())
                .build().create(HttpSerivce.class);
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");
        Call<NetResult<User>> login = httpSerivce.loginUser(hashMap);
        login.enqueue(new Callback<NetResult<User>>() {
            @Override
            public void onResponse(Call<NetResult<User>> call, Response<NetResult<User>> response) {
                System.out.println("==onResponse=="+response.body());
            }

            @Override
            public void onFailure(Call<NetResult<User>> call, Throwable t) {

            }
        });
        System.out.println("==end==");
    }

    Dispatcher dispatcher = new Dispatcher(new AbstractExecutorService() {
        private boolean shutingDown = false;
        private boolean terminated = false;

        @Override
        public void shutdown() {
            this.shutingDown = true;
            this.terminated = true;
        }

        @NonNull
        @Override
        public List<Runnable> shutdownNow() {
            return new ArrayList<>();
        }

        @Override
        public boolean isShutdown() {
            return this.shutingDown;
        }

        @Override
        public boolean isTerminated() {
            return this.terminated;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public void execute(Runnable command) {
            command.run();
        }
    });



    @Test
    @Config(constants = com.tospur.exmind.study_tdd.testNet.BuildConfig.class,sdk =23)
    public void testLoginAsync() throws InterruptedException {
        HttpSerivce httpSerivce = new Retrofit.Builder().baseUrl("http://172.18.84.243:8080/agent_cloud/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder()
                        .addInterceptor(headInterceptor)
                        .addInterceptor(loggingInterceptor)
                        .build())
                .build().create(HttpSerivce.class);
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");

        final AtomicBoolean waitLock = new AtomicBoolean(false);
        Call<NetResult<User>> login = httpSerivce.loginUser(hashMap);
        login.enqueue(new Callback<NetResult<User>>() {
            @Override
            public void onResponse(Call<NetResult<User>> call, Response<NetResult<User>> response) {
                System.out.println("==onResponse=="+response.body());
                waitLock.set(true);
            }

            @Override
            public void onFailure(Call<NetResult<User>> call, Throwable t) {

            }
        });
        System.out.println("==end=11==");
        while (!waitLock.get()) {
            Thread.sleep(1000);
            ShadowLooper.runUiThreadTasks();
        }
        System.out.println("==end=22==");
    }




    Interceptor loggingInterceptor = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            final long t1 = System.nanoTime();
            System.out.println(String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));

            okhttp3.Response response = chain.proceed(request);

            final long t2 = System.nanoTime();
            final String responseBody = response.body().string();
            System.out.println(String.format("Received response for %s in %.1fms%n%s%s", response.request().url(), (t2 - t1) / 1e6d, response.headers(), responseBody));
            return response.newBuilder()
                    .body(ResponseBody.create(response.body().contentType(), responseBody))
                    .build();
        }
    };




    Interceptor headInterceptor = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request original = chain.request();

            Request request = original.newBuilder()
                    .header("X-Token", "")
                    .header("X-appOS", "android")
                    .header("X-version", BuildConfig.VERSION_NAME)
                    .header("X-CaseId", "")
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        }
    };



}
