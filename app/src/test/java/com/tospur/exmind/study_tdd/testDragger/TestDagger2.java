package com.tospur.exmind.study_tdd.testDragger;

import com.tospur.exmind.study_tdd.net.DaggerNetComponent;
import com.tospur.exmind.study_tdd.net.HttpSerivce;
import com.tospur.exmind.study_tdd.net.NetComponent;
import com.tospur.exmind.study_tdd.net.NetModule;
import com.tospur.exmind.study_tdd.net.NetResult;
import com.tospur.exmind.study_tdd.net.User;
import com.tospur.exmind.study_tdd.testNet.ApiException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

/**
 * Created by lehow on 2016/9/18.
 * 内容摘要：
 * 版权所有：极策科技
 */
@RunWith(RobolectricTestRunner.class) @Config(constants = com.tospur.exmind.study_tdd.testNet.BuildConfig.class,sdk =23)
public class TestDagger2 {

    @Test
    public void testHttpReq(){
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");
        HttpSerivce httpSerivce= DaggerNetComponent.builder().netModule(new NetModule()).build().getHttpSerivce();
        Flowable<NetResult<User>> netResultFlowable = httpSerivce.loginFlowable(hashMap);

        netResultFlowable.map(new Function<NetResult<User>, User>() {
            @Override
            public User apply(NetResult<User> userNetResult) throws Exception {
                if (userNetResult.getCode() == 0) {
                    return userNetResult.getData();
                }else{
                throw new ApiException(userNetResult.getCode(), userNetResult.getMsg());
                }

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
                    ApiException apiException = (ApiException) t;
                    System.out.println("TestDagger2.onError apiException="+apiException.getMessage());
                }
            }

            @Override
            public void onComplete() {
                System.out.println("TestRetrofitWithRx.onComplete");
            }
        });
    }

    @Test
    public void testScope(){
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");
        NetComponent netComponent = DaggerNetComponent.builder().netModule(new NetModule()).build();
        HttpSerivce httpSerivce = netComponent.getHttpSerivce();
        HttpSerivce httpSerivce2= netComponent.getHttpSerivce();

        System.out.println("httpSerivce=" + httpSerivce);
        System.out.println("httpSerivce2=" + httpSerivce2);
    }
}
