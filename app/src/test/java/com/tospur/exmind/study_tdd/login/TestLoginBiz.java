package com.tospur.exmind.study_tdd.login;

import com.tospur.exmind.study_tdd.net.HttpSerivce;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by lehow on 2016/9/26.
 * 内容摘要：
 * 版权所有：极策科技
 */
public class TestLoginBiz {

    @Test
    public void testLogin(){

        HttpSerivce httpSerivce = mock(HttpSerivce.class);
        LoginBiz loginBiz = new LoginBiz(httpSerivce);

        String name = "adimin";
        String pw = "admin";
        OnLoginCallback onLoginCallback = mock(OnLoginCallback.class);
        //主要是mock Call对象
        final Call mockCall = mock(Call.class);
        //当mock对象的login方法执行时，将返回替换为mock对象
        doReturn(mockCall).when(httpSerivce).login(any(HashMap.class));

        loginBiz.login(name, pw, onLoginCallback);
        ArgumentCaptor<Callback> argumentCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(mockCall).enqueue(argumentCaptor.capture());

        Call<ResponseBody> call=null;
        Response<ResponseBody> response = null;

        argumentCaptor.getValue().onResponse(call,response);
        verify(onLoginCallback).onSuccess(any(UserData.class));

    }

}
