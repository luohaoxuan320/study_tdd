package com.tospur.exmind.study_tdd.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tospur.exmind.study_tdd.BuildConfig;
import com.tospur.exmind.study_tdd.R;

import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowProgressDialog;
import org.robolectric.shadows.ShadowToast;

import static android.R.string.no;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by lehow on 2016/9/21.
 * 内容摘要：
 * 版权所有：极策科技
 */
@RunWith(RobolectricTestRunner.class) @Config(constants =BuildConfig.class,sdk =23)

public class LoginActivityTest {

    LoginActivity mainActivity;

    @Before
    public void stetUp(){
        mainActivity = Robolectric.setupActivity(LoginActivity.class);
        //验证loginPresenter对象不能为null
        assertThat("loginPresenter can't be null",mainActivity.loginPresenter,notNullValue());
    }


    @Test
    public void testLoginBtn(){
        LoginPresenter loginPresenter = mock(LoginPresenter.class);
        //替换mock对象，方面后面验证交互
        mainActivity.loginPresenter = loginPresenter;
        Button btn_login = findView(mainActivity, R.id.btn_login);
        btn_login.performClick();
        //验证loginPresenter的方法被调用了
        verify(loginPresenter).login(anyString(), anyString());

    }

    @Test
    public void testShowDialog(){
        mainActivity.setLoginIndicator(true);
        AlertDialog latestAlertDialog = ShadowProgressDialog.getLatestAlertDialog();
        assertThat(latestAlertDialog.isShowing(), is(true));
        mainActivity.setLoginIndicator(false);
        assertFalse(latestAlertDialog.isShowing());
    }

    @Test
    public void testShowErrorMsg() {
        String errMsg = "用户名或者密码不能为空";
        mainActivity.showErrorParams(errMsg);
        Assert.assertThat(ShadowToast.getTextOfLatestToast(),equalTo(errMsg));
    }




    private <T extends View> T findView(Activity parentView, int idRes) {
        return (T)parentView.findViewById(idRes);
    }

}
