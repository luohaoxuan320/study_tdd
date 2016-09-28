package com.tospur.exmind.study_tdd.login;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by lehow on 2016/9/26.
 * 内容摘要：
 * 版权所有：极策科技
 */
public class TestLoginBizOld {


    @Test
    public void testLogin2(){

        final Call mock = mock(Call.class);
        //创建对象是，重写方法直接返回mock对象验证
        LoginBizOld LoginBizOld = new LoginBizOld(){
            @Override
            protected Call<ResponseBody> getLoginCall(String name, String pw) {
                return mock;
            }
        };
        String name = "adimin";
        String pw = "admin";
        OnLoginCallback onLoginCallback = mock(OnLoginCallback.class);
        //调用login方法
        LoginBizOld.login(name, pw, onLoginCallback);
        ArgumentCaptor<Callback> argumentCaptor = ArgumentCaptor.forClass(Callback.class);
        //验证enqueue方法被调用，并捕获其参数
        verify(mock).enqueue(argumentCaptor.capture());

        Call<ResponseBody> call=null;
        Response<ResponseBody> response = null;
        //回调enqueue方法参数的onResponse方法（跳过真实的异步网络请求）
        argumentCaptor.getValue().onResponse(call,response);
        //验证onLoginCallback的方法有被调用
        verify(onLoginCallback).onSuccess(any(UserData.class));

    }

    @Test
    public void testLogin3(){


        LoginBizOld spy = spy(LoginBizOld.class);

        String name = "adimin";
        String pw = "admin";
        OnLoginCallback onLoginCallback = mock(OnLoginCallback.class);
        //主要是mock Call对象
        final Call mock = mock(Call.class);
        doReturn(mock).when(spy).getLoginCall(anyString(), anyString());
        //spy会执行真实的login方法，而login中getLoginCall时，会返回上面预设的Call的mock对象
        //如果此处是LoginBizOld的mock对象，那么login的真实方法是不会被执行的
        spy.login(name, pw, onLoginCallback);
        ArgumentCaptor<Callback> argumentCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(mock).enqueue(argumentCaptor.capture());

        Call<ResponseBody> call=null;
        Response<ResponseBody> response = null;

        argumentCaptor.getValue().onResponse(call,response);
        verify(onLoginCallback).onSuccess(any(UserData.class));

    }

    @Test
    public void testLogin4(){

        LoginBizOld LoginBizOld = new LoginBizOld();

        LoginBizOld spy = spy(LoginBizOld);

        final Call mock = mock(Call.class);
        doReturn(mock).when(spy).getLoginCall(anyString(), anyString());

        String name = "adimin";
        String pw = "admin";
        OnLoginCallback onLoginCallback = mock(OnLoginCallback.class);
        //通过doAnswer 来默认回调
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                Callback callback = (Callback) arguments[0];
                callback.onResponse(null,null);
                return null;
            }
        }).when(mock).enqueue(any(Callback.class));


        spy.login(name, pw, onLoginCallback);
        
        
        verify(onLoginCallback).onSuccess(any(UserData.class));

    }
}
