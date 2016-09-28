package com.tospur.exmind.study_tdd.login;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lehow on 2016/9/22.
 * 内容摘要：
 * 版权所有：极策科技
 */
@Module
public class LoginPresenterModule {

    private LoginContract.IView loginView;

    public LoginPresenterModule(LoginContract.IView loginView) {
        this.loginView = loginView;
    }

    @Provides
    LoginContract.IView provideLoginView() {
        return loginView;
    }
}
