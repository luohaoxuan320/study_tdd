package com.tospur.exmind.study_tdd.testRx;

import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Flowable;

/**
 * Created by lehow on 2016/9/14.
 * 内容摘要：
 * 版权所有：极策科技
 */
public class TestRx {

    @Test
    public void testFlowable(){
        Flowable.just(1,2,3,4).subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("TestRx.onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("TestRx.onNext "+integer);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("TestRx.onError");
            }

            @Override
            public void onComplete() {
                System.out.println("TestRx.onComplete");
            }
        });
    }


    @Test
    public void testFlowable2(){
        Flowable.just(1,2,3,4).subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("TestRx.onSubscribe");
                s.request(1);
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("TestRx.onNext "+integer);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("TestRx.onError");
            }

            @Override
            public void onComplete() {
                System.out.println("TestRx.onComplete");
            }
        });
    }
}
