package com.tospur.exmind.study_tdd.login;

/**
 * Created by lehow on 2016/9/26.
 * 内容摘要：
 * 版权所有：极策科技
 */
public interface LoginContract {
    interface IView {
        /**
         *显示等待进度
         *  @param b
         */
        void setLoginIndicator(boolean b) ;

        /**
         * 登录 参数检验错误
         * @param errEmptyName
         */
        void showErrorParams(String errEmptyName) ;

        /**
         * 跳转到主界面
         */
        void jumpToMainActivity();

        /**
         * 登录网络校验失败
         * @param errMsg
         */
        void showLoginFailed(String errMsg) ;
    }

    interface IPresenter{
        public void login(String name, String pw);
    }
}
