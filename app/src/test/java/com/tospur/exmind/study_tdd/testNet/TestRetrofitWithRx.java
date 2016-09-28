package com.tospur.exmind.study_tdd.testNet;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.tospur.exmind.study_tdd.net.HttpSerivce;
import com.tospur.exmind.study_tdd.net.NetResult;
import com.tospur.exmind.study_tdd.net.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.HashMap;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lehow on 2016/9/13.
 * 内容摘要：
 * 版权所有：极策科技
 */
@RunWith(RobolectricTestRunner.class) @Config(constants = com.tospur.exmind.study_tdd.testNet.BuildConfig.class,sdk =23)
public class TestRetrofitWithRx {

    @Test
    public void testLogin(){
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
    public void testLoginWithRx(){
        HttpSerivce httpSerivce = new Retrofit.Builder().baseUrl("http://172.18.84.243:8080/agent_cloud/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(headInterceptor).addInterceptor(loggingInterceptor).build())
                .build().create(HttpSerivce.class);
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");
        Observable<NetResult<User>> observable = httpSerivce.loginObservable(hashMap);

            observable.subscribe( new Observer<NetResult<User>>(){

                @Override
                public void onSubscribe(Disposable d) {
                    System.out.println("TestRetrofitWithRx.onSubscribe");
                }

                @Override
                public void onNext(NetResult<User> value) {
                    System.out.println("TestRetrofitWithRx.onNext");
                }

                @Override
                public void onError(Throwable e) {
                    System.out.println("TestRetrofitWithRx.onError");
                }

                @Override
                public void onComplete() {
                    System.out.println("TestRetrofitWithRx.onComplete");
                }
            });
    }


    @Test
    public void testLoginWithRxFlowable(){
        HttpSerivce httpSerivce = new Retrofit.Builder().baseUrl("http://172.18.84.243:8080/agent_cloud/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(headInterceptor).addInterceptor(loggingInterceptor).build())
                .build().create(HttpSerivce.class);
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");
        Flowable<NetResult<User>> netResultFlowable = httpSerivce.loginFlowable(hashMap);

        netResultFlowable.subscribe(new Subscriber<NetResult<User>>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("TestRetrofitWithRx.onSubscribe");
                s.request(1);
            }

            @Override
            public void onNext(NetResult<User> userNetResult) {
                System.out.println("TestRetrofitWithRx.onNext "+userNetResult.getData().toString());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("TestRetrofitWithRx.onError");
            }

            @Override
            public void onComplete() {
                System.out.println("TestRetrofitWithRx.onComplete");
            }
        });
    }


    @Test
    public void testLoginWithRxFlowableAndMap(){
        HttpSerivce httpSerivce = new Retrofit.Builder().baseUrl("http://172.18.84.243:8080/agent_cloud/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(headInterceptor).addInterceptor(loggingInterceptor).build())
                .build().create(HttpSerivce.class);
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");
        Flowable<NetResult<User>> netResultFlowable = httpSerivce.loginFlowable(hashMap);
        netResultFlowable.map(new Function<NetResult<User>, User>() {
            @Override
            public User apply(NetResult<User> userNetResult) throws Exception {
                System.out.println("TestRetrofitWithRx.apply");
                return userNetResult.getData();
            }
        }).subscribe(new Subscriber<User>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("TestRetrofitWithRx.onSubscribe");
                s.request(1);
            }

            @Override
            public void onNext(User user) {
                System.out.println(user.toString());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("TestRetrofitWithRx.onError");
            }

            @Override
            public void onComplete() {
                System.out.println("TestRetrofitWithRx.onComplete");
            }
        });
    }


    @Test
    public void testLoginWithRxFlowableAndMapProgressBar(){
        HttpSerivce httpSerivce = new Retrofit.Builder().baseUrl("http://172.18.84.243:8080/agent_cloud/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(headInterceptor).addInterceptor(loggingInterceptor).build())
                .build().create(HttpSerivce.class);
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");
        Flowable<NetResult<User>> netResultFlowable = httpSerivce.loginFlowable(hashMap);
        netResultFlowable.map(new Function<NetResult<User>, User>() {
            @Override
            public User apply(NetResult<User> userNetResult) throws Exception {
                return userNetResult.getData();
            }
        }).subscribe(new Subscriber<User>() {

            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("TestRetrofitWithRx.onSubscribe");
                s.request(1);
            }

            @Override
            public void onNext(User user) {
                System.out.println(user.toString());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("TestRetrofitWithRx.onError");
            }

            @Override
            public void onComplete() {
                System.out.println("TestRetrofitWithRx.onComplete");
            }
        });
    }

    @Test
    public void testLoginWithRxFlowableAndMapException(){
        HttpSerivce httpSerivce = new Retrofit.Builder().baseUrl("http://172.18.84.243:8080/agent_cloud/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(headInterceptor).addInterceptor(loggingInterceptor).build())
                .build().create(HttpSerivce.class);
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");
        Flowable<NetResult<User>> netResultFlowable = httpSerivce.loginFlowable(hashMap);
        netResultFlowable.map(new Function<NetResult<User>, User>() {
            @Override
            public User apply(NetResult<User> userNetResult) throws Exception {
//                if (userNetResult.getCode() == 0) {
//                    return userNetResult.getData();
//                }else{
                    throw new ApiException(userNetResult.getCode(), userNetResult.getMsg());
//                }

            }
        }).subscribe(new Subscriber<User>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("TestRetrofitWithRx.onSubscribe");
                s.request(1);
            }

            @Override
            public void onNext(User user) {
                System.out.println(user.toString());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("TestRetrofitWithRx.onError");
                if (t instanceof ApiException) {

                }
            }

            @Override
            public void onComplete() {
                System.out.println("TestRetrofitWithRx.onComplete");
            }
        });
    }



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


}
