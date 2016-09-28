package com.tospur.exmind.study_tdd.testNet;

/**
 * Created by lehow on 2016/9/19.
 * 内容摘要：
 * 版权所有：极策科技
 */
public class ApiException extends Exception {
    protected final int code;
    protected final String msg;

    public ApiException( int code,String msg) {
        this.msg = msg;
        this.code = code;
    }
}
