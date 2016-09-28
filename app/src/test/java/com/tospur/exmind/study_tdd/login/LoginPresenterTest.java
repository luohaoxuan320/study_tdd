package com.tospur.exmind.study_tdd.login;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Created by lehow on 2016/8/22.
 * 内容摘要：登录测试
 * 版权所有：极策科技
 */
public class LoginPresenterTest {
    @Mock
    LoginContract.IView loginView;
    @Mock
    LoginBiz loginBiz;

    private LoginPresenter loginPresenter;

    ArgumentCaptor<OnLoginCallback> loginCallbackArgumentCaptor;

    /**
     * 初始化对象
     */
    @Before
    public void setupLoginPresenter() {
        MockitoAnnotations.initMocks(this);
        loginCallbackArgumentCaptor = ArgumentCaptor.forClass(OnLoginCallback.class);
        loginPresenter = new LoginPresenter(loginView, loginBiz);
    }

    /**
     * 登录操作
     * 弹框等待--登录--取消弹框--跳转页面
     */
    @Test
    public void testLoginPresenterSuccess() {

        String name = "admin";
        String pw = "12345678";
        loginPresenter.login(name, pw);
        //显示进度框
        verify(loginView).setLoginIndicator(true);
        //调用了登录业务
        verify(loginBiz).login(eq(name), eq(pw), loginCallbackArgumentCaptor.capture());
        //调用onSuccess 回调
        loginCallbackArgumentCaptor.getValue().onSuccess(new UserData());
        //弹框取消了
        verify(loginView).setLoginIndicator(false);
        verify(loginView).jumpToMainActivity();

    }


    /**
     * 弹框等待-登录-取消弹框--提示错误
     */
    @Test
    public void testLoginPresenterFailed() {

        String name = "admin";
        String pw = "12345678";
        loginPresenter.login(name, pw);
        //显示进度框
        verify(loginView).setLoginIndicator(true);
        //调用了登录业务
        verify(loginBiz).login(eq(name), eq(pw), loginCallbackArgumentCaptor.capture());
        loginCallbackArgumentCaptor.getValue().onFailed("用户名或者密码不对");
        //弹框取消了
        verify(loginView).setLoginIndicator(false);
        verify(loginView).showLoginFailed("用户名或者密码不对");

    }


    /**
     * 检验登录输入参数
     */
    @Test
    public void testLoginParamsInvalid() {

        String errEmptyName = "输入的用户名不能为空";
        String errPWName = "输入的密码不能为空";
        String errPwLength = "请输入8位密码";
        loginPresenter.login("", "");

        verify(loginView).showErrorParams(errEmptyName);
        //没有其他的交互被调用了，防止条件校验失败未return
        verifyNoMoreInteractions(loginView);
        verify(loginView, never()).setLoginIndicator(anyBoolean());
        //密码不能为空
        loginPresenter.login("admin", "");
        verify(loginView).showErrorParams(errPWName);
        verify(loginView, never()).setLoginIndicator(anyBoolean());
        //密码长度不对
        loginPresenter.login("admin", "1234567");
        verify(loginView).showErrorParams(errPwLength);
        verify(loginView, never()).setLoginIndicator(anyBoolean());
    }


}
