package com.tospur.exmind.study_tdd.login;

import android.text.TextUtils;

import javax.inject.Inject;

import static dagger.internal.Preconditions.checkNotNull;

/**
 * Created by lehow on 2016/8/22.
 * 内容摘要：
 * 版权所有：极策科技
 */
public class LoginPresenter implements LoginContract.IPresenter{

    private LoginContract.IView loginView;
    private LoginBiz loginBiz;
    @Inject
    public LoginPresenter(LoginContract.IView loginView, LoginBiz loginBiz) {
        this.loginView = checkNotNull(loginView," loginView can't be null");
        this.loginBiz = checkNotNull(loginBiz,"loginBiz can't be null");
    }

    public void  login(String name, String pw) {
        if (!verfiryParams(name, pw))return;
        loginView.setLoginIndicator(true);
        loginBiz.login(name, pw, new OnLoginCallback(){

            @Override
            public void onSuccess(UserData userData) {
                loginView.setLoginIndicator(false);
                loginView.jumpToMainActivity();
            }

            @Override
            public void onFailed(String errMsg) {
                loginView.setLoginIndicator(false);
                loginView.showLoginFailed(errMsg);
            }
        });
    }

    private boolean verfiryParams(String name, String pw) {
        if (name==null||"".equals(name)) {
            loginView.showErrorParams("输入的用户名不能为空");
            return false;
        }
        if (pw == null || "".equals(pw)) {
            loginView.showErrorParams("输入的密码不能为空");
            return false;
        }
        /*if (pw.length() != 8) {
            loginView.showErrorParams("请输入8位密码");
            return false;
        }*/
        return true;
    }
}
