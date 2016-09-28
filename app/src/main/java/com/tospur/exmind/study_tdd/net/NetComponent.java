package com.tospur.exmind.study_tdd.net;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by lehow on 2016/9/19.
 * 内容摘要：
 * 版权所有：极策科技
 */
@Singleton
@Component(modules = NetModule.class)
public interface NetComponent {
    HttpSerivce getHttpSerivce();

}
