package com.tospur.exmind.study_tdd.net;

/**
 * Created by dell on 2016/5/30.
 * 内容摘要：
 * 版权所有：极策科技
 */
public class NetResult<T> {
    public static final int RESULT_OK = 0;
    public static final int RESULT_AUTHORIZATION = 401;//未授权

    private Status status;
    /**
     * 数据
     */
    private T data;


    public void setStatus(Status status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return status.getCode();
    }
    public String getMsg() {
        return status.getMsg();
    }
     class Status{
        /**
         * 状态码
         */
        private int code;
        /**
         * 状态消息
         */
        private String msg;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
