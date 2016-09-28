package com.tospur.exmind.study_tdd.net;

/**
 * Created by lehow on 2016/9/12.
 * 内容摘要：
 * 版权所有：极策科技
 */
public class User {
    private String userId;
    private String userName;
    private int caseId;
    private String caseName;
    private String phone;
    private String token;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getCaseId() {
        return caseId;
    }

    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    @Override
    public String toString() {
        return "userId="+userId+"  userName="+userName+"  caseId="+caseId+"  caseName="+caseName+"  phone="+phone+"  token="+token;
    }
}
