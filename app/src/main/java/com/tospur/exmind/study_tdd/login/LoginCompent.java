package com.tospur.exmind.study_tdd.login;

import com.tospur.exmind.study_tdd.net.ActivityScope;
import com.tospur.exmind.study_tdd.net.ActivityScope2;
import com.tospur.exmind.study_tdd.net.NetComponent;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by lehow on 2016/9/22.
 * 内容摘要：
 * 版权所有：极策科技
 */
//从 NetComponent处获取HttpService依赖
@ActivityScope
@Component(modules = LoginPresenterModule.class,dependencies = NetComponent.class)
public interface LoginCompent {
    void inject(LoginActivity loginActivity);
}
