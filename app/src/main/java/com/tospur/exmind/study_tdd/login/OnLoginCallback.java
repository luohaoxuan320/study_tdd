package com.tospur.exmind.study_tdd.login;

/**
 * Created by lehow on 2016/8/29.
 * 内容摘要：
 * 版权所有：极策科技
 */
public interface OnLoginCallback {
    public void onSuccess(UserData userData);

    public void onFailed(String errMsg) ;
}
