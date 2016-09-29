本实践案例除了学习单元测试外，还在学习实践TDD，Retrofit，RxJava，MVP。最近也在学习摸索，觉得有必要做一个总结记录和分享，因为参考了很多大神分享的文章，获益良多。
[项目github地址](https://github.com/luohaoxuan320/study_tdd)

单元测试的时候，我们只验证平级的交互流程，即某个方法的功能，他调用的其他方法的细节不做验证，只需要验证，他调用了某个方法，某个方法给予结果，他收到了，就行

# 首先考虑登录的业务交互流程逻辑，测试LoginPresenter 

校验数据的有效性-->等待弹框-->网络验证 -->关闭弹框-->结果提示


```
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
```


上面是LoginPresenter的交互逻辑，需要验证它与上下游的交互流程是否正确，已经覆盖到。验证了对LoginBiz的调用已经对LoginView的相关调用。

```
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
```

可以看出来，在验证LoginPresenter的login方法时，我只验证了他对loginView的调用，以及对loginBiz的调用，至于loginView.setLoginIndicator 和loginBiz.login方法具体有没有实现执行的是什么，在这个交互里面我是不关心的。

PS：在testLoginParamsInvalid中 有一行代码verifyNoMoreInteractions(loginView)，因为我在LoginPresenter 的login中verfiryParams无效后，忘了return，导致后面的交互也在走，是测试没有覆盖到的，后面运行的时候，才发现这个问题，补加的一句。
所以可见测试的重要性，已经自己要充分的理解交互的流程与关键点。

# 测试LoginBiz

```
public class LoginBiz {
    public void login(String name, String pw, final OnLoginCallback loginCallback) {
        //刚开始是这样写的，但是这样写 1.不方便测试，2.也破环方法的平行层级结构即步骤，他的步骤，一获取call对象，二执行。
        // HashMap<String, String> hashMap = new HashMap();
        // hashMap.put("loginId", name);
        // hashMap.put("password", pw);
        // Call<ResponseBody> login= RetrofitBuilder.getHttpService().login(hashMap);

        Call<ResponseBody> login = getLoginCall(name,pw);
        login.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loginCallback.onSuccess(new UserData());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


     Call<ResponseBody> getLoginCall(String name, String pw) {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("loginId", name);
        hashMap.put("password", pw);
        return RetrofitBuilder.getHttpService().login(hashMap);
    }

}
```
逻辑很简单，可以看下注释的地方，提炼一个getLoginCall方法后，方面的层级步骤清楚简单了，但是感觉有点破坏了封装性，但是这样有利于测试mock，不然很难下手测试。
因为我们要测试LoginBiz 的login方法，主要是验证其与OnLoginCallback 的交互是预期的正确的，也即onResponse和onFailure后，有对应调用OnLoginCallback 的方法，那么至于这个login.enqueue()方法的执行我们不关心也不会去等（这个可是网络请求，这一块的验证不在范围之内）
看下面测试的实现：一个是通过重写 来返回mock对象，一个是通过spy返回mock对象。同时也用两种方式实现了对结果的回调，一个是ArgumentCaptor，一个是doAnswer。

```

    @Test
    public void testLogin2(){

        final Call mock = mock(Call.class);
        //创建对象是，重写方法直接返回mock对象验证
        LoginBiz loginBiz = new LoginBiz(){
            @Override
            protected Call<ResponseBody> getLoginCall(String name, String pw) {
                return mock;
            }
        };
        String name = "adimin";
        String pw = "admin";
        OnLoginCallback onLoginCallback = mock(OnLoginCallback.class);
        //调用login方法
        loginBiz.login(name, pw, onLoginCallback);
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


        LoginBiz spy = spy(LoginBiz.class);

        String name = "adimin";
        String pw = "admin";
        OnLoginCallback onLoginCallback = mock(OnLoginCallback.class);
        //主要是mock Call对象
        final Call mock = mock(Call.class);
        doReturn(mock).when(spy).getLoginCall(anyString(), anyString());
        //spy会执行真实的login方法，而login中getLoginCall时，会返回上面预设的Call的mock对象
        //如果此处是LoginBiz的mock对象，那么login的真实方法是不会被执行的
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

        LoginBiz loginBiz = new LoginBiz();

        LoginBiz spy = spy(loginBiz);

        final Call mock = mock(Call.class);
        doReturn(mock).when(spy).getLoginCall(anyString(), anyString());

        String name = "adimin";
        String pw = "admin";
        OnLoginCallback onLoginCallback = mock(OnLoginCallback.class);
        //通过doAnswer 来默认回调onResponse方法
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
```
## 引入Dagger依赖注入，更好的改造LoginBiz与测试


```
public class LoginBiz {


    HttpSerivce httpSerivce;

    @Inject
    public LoginBiz(HttpSerivce httpSerivce) {
        this.httpSerivce = httpSerivce;
    }

    public void login(String name, String pw, final OnLoginCallback loginCallback) {
        Call<ResponseBody> login = getLoginCall(name,pw);
        login.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loginCallback.onSuccess(new UserData());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


     private Call<ResponseBody> getLoginCall(String name, String pw) {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("loginId", name);
        hashMap.put("password", pw);
        return httpSerivce.login(hashMap);
    }

}
```


```
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
```

这样可以有更好的封装性与更简洁的测试代码，不用为了mock Call对象，而暴露getLoginCall方法给外部mock，他本应该是私有的。而且测试的代码也很简单流畅，只要mock就行，不用spy，或者重写。

或者代码调整成这样的，更能看出优势和符合平时书写的习惯

```
public class LoginBiz {


    HttpSerivce httpSerivce;

    @Inject
    public LoginBiz(HttpSerivce httpSerivce) {
        this.httpSerivce = httpSerivce;
    }

    public void login(String name, String pw, final OnLoginCallback loginCallback) {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("loginId", name);
        hashMap.put("password", pw);
        Call<ResponseBody> login = httpSerivce.login(hashMap);
        login.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loginCallback.onSuccess(new UserData());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

```
但是还是推荐上面的方式，让login方法的步骤层级简单明晰些。


# LoginActivity验证


1.验证了Activity实例化后，loginPresenter 对象也被实例化了
2.验证了登录按钮与loginPresenter 的交互正确
3.验证了对Progerss的调用正确的显示和关闭了
4.验证toast的消息正确的显示了。

这里可能会有疑惑，为何这里是分开验证的。不是应该验证点击登录后，依次验证loginPresenter 的login方法被调用了，setLoginIndicator(true)被调用了，然后setLoginIndicator(false)被调用了么？
如果这样验证，你会发现很难下手，由于loginPresenter是被mock了的，其login的真实方法不会被调用，所以setLoginIndicator的调用无法被验证到。当然你会说可以用spy对象，但是，这样你有得考虑网络请求的等待，是不是想屎了。

之所以会陷入这样的困局，主要是对单元测试的理解有误，一是粒度的把握，二是职责的确定。刚才那样的测试应该属于继承测试，而不是单元测试。

单元测试应该主要验证当前类
- 调用其他方法，即需求其他的类提供服务时，他是否准确的触发了，如登录时对loginPresenter的login方法的调用，而其真正的执行我不关心，也无需知道，不在我的职责范畴之类
- 被其他类调用，即对外提供服务时，自己是否正确的响应并执行了。

这两点，也就是我们类的边界，这样单元测试就会轻松很多。回到前面说的对setLoginIndicator(true)和setLoginIndicator(false)的验证，这应该是在测试loginPresenter 时，其需要保证的，而不在Acitivty中，Acitivty只要知道，点击登录时，我的请求发出去了，就可以了，我告诉你了，你不执行，我就很无奈了；我给你提供了弹框方法，而且我自检可以弹出，可是如果你不告诉我，不请求我，我也只能很无辜了。我做好了我该做的。

回过头再来看mock的设计艺术，你会发现他就是让你关注当前类的功能与交互，与之相关的类，直接用mock来验证，而不真实的去执行，因为当前类，只关心到这个层面。

```
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
```

LoginActivity的代码

```
public class LoginActivity extends AppCompatActivity implements LoginContract.IView {


    private Button btn_login;
    private EditText et_name;
    private EditText et_password;


    private ProgressDialog progressDialog;

    @Inject LoginPresenter loginPresenter;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //注入Presenter对象
        DaggerLoginCompent.builder().loginPresenterModule(new LoginPresenterModule(this)).build().inject(this);
        progressDialog = new ProgressDialog(this);
        btn_login = findView(R.id.btn_login);
        et_name = findView(R.id.ed_name);
        et_password = findView(R.id.ed_password);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginPresenter.login(et_name.getText().toString(), et_password.getText().toString());
            }
        });
    }


    protected <T extends View> T findView(int idRes) {
        return (T) findViewById(idRes);
    }

    protected void toast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setLoginIndicator(boolean b) {
        if (b) {
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }

    }

    @Override
    public void showErrorParams(String errEmptyName) {
        toast(errEmptyName);
    }

    @Override
    public void jumpToMainActivity() {

    }

    @Override
    public void showLoginFailed(String errMsg) {

    }
}
```


# 用Retroft进行网络请求并验证

由于网络请求的异步性，这里测试异步回调有3中方式
1.将发起的异步请求用同步请求去代替，即将enqueue方法，替换为execute
2.用拦截器，将异步请求变为同步请求
3.测试线程沦陷等待异步线程结果返回


```
    /**
     * 同步测试API
     */
    @Test
    @Config(constants = com.tospur.exmind.study_tdd.testNet.BuildConfig.class,sdk =23)
    public void testLoginSync(){
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");
        Call<ResponseBody> login = httpSerivce.login(hashMap);
        Log.i("TestHttpService", "===begin===");
        System.out.println("===begin===");
        try {
            Response<ResponseBody> responseBody=login.execute();
            System.out.println(responseBody.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("TestHttpService", "===end===");
        System.out.println("===end===");
    }
```


```

    @Test
    @Config(constants = com.tospur.exmind.study_tdd.testNet.BuildConfig.class,sdk =23)
    public void testLoginAsyncToSync (){
        HttpSerivce httpSerivce = new Retrofit.Builder().baseUrl("http://172.18.84.243:8080/agent_cloud/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder()
                        .addInterceptor(headInterceptor)
                        .addInterceptor(loggingInterceptor)
                        .dispatcher(dispatcher)
                        .build())
                .build().create(HttpSerivce.class);
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");
        Call<NetResult<User>> login = httpSerivce.loginUser(hashMap);
        login.enqueue(new Callback<NetResult<User>>() {
            @Override
            public void onResponse(Call<NetResult<User>> call, Response<NetResult<User>> response) {
                System.out.println("==onResponse=="+response.body());
            }

            @Override
            public void onFailure(Call<NetResult<User>> call, Throwable t) {

            }
        });
        System.out.println("==end==");
    }

    Dispatcher dispatcher = new Dispatcher(new AbstractExecutorService() {
        private boolean shutingDown = false;
        private boolean terminated = false;

        @Override
        public void shutdown() {
            this.shutingDown = true;
            this.terminated = true;
        }

        @NonNull
        @Override
        public List<Runnable> shutdownNow() {
            return new ArrayList<>();
        }

        @Override
        public boolean isShutdown() {
            return this.shutingDown;
        }

        @Override
        public boolean isTerminated() {
            return this.terminated;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public void execute(Runnable command) {
            command.run();
        }
    });


```


```

    @Test
    @Config(constants = com.tospur.exmind.study_tdd.testNet.BuildConfig.class,sdk =23)
    public void testLoginAsync() throws InterruptedException {
        HttpSerivce httpSerivce = new Retrofit.Builder().baseUrl("http://172.18.84.243:8080/agent_cloud/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder()
                        .addInterceptor(headInterceptor)
                        .addInterceptor(loggingInterceptor)
                        .build())
                .build().create(HttpSerivce.class);
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");

        final AtomicBoolean waitLock = new AtomicBoolean(false);
        Call<NetResult<User>> login = httpSerivce.loginUser(hashMap);
        login.enqueue(new Callback<NetResult<User>>() {
            @Override
            public void onResponse(Call<NetResult<User>> call, Response<NetResult<User>> response) {
                System.out.println("==onResponse=="+response.body());
                waitLock.set(true);
            }

            @Override
            public void onFailure(Call<NetResult<User>> call, Throwable t) {

            }
        });
        System.out.println("==end=11==");
        while (!waitLock.get()) {
            Thread.sleep(1000);
            ShadowLooper.runUiThreadTasks();
        }
        System.out.println("==end=22==");
    }

```

# Retrofit+Rxjava

```
@RunWith(RobolectricTestRunner.class) @Config(constants = com.tospur.exmind.study_tdd.testNet.BuildConfig.class,sdk =23)
public class TestRetrofitWithRx {

    @Test
    public void testLogin(){
        HttpSerivce httpSerivce = new Retrofit.Builder().baseUrl("http://172.18.84.243:8080/agent_cloud/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(headInterceptor).addInterceptor(loggingInterceptor).build())
                .build().create(HttpSerivce.class);
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");
        Call<NetResult<User>> login = httpSerivce.loginUser(hashMap);
        try {
            Response<NetResult<User>> execute = login.execute();
            System.out.println(execute.body().getMsg());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLoginWithRx(){
        HttpSerivce httpSerivce = new Retrofit.Builder().baseUrl("http://172.18.84.243:8080/agent_cloud/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(headInterceptor).addInterceptor(loggingInterceptor).build())
                .build().create(HttpSerivce.class);
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");
        Observable<NetResult<User>> observable = httpSerivce.loginObservable(hashMap);

            observable.subscribe( new Observer<NetResult<User>>(){

                @Override
                public void onSubscribe(Disposable d) {
                    System.out.println("TestRetrofitWithRx.onSubscribe");
                }

                @Override
                public void onNext(NetResult<User> value) {
                    System.out.println("TestRetrofitWithRx.onNext");
                }

                @Override
                public void onError(Throwable e) {
                    System.out.println("TestRetrofitWithRx.onError");
                }

                @Override
                public void onComplete() {
                    System.out.println("TestRetrofitWithRx.onComplete");
                }
            });
    }


    @Test
    public void testLoginWithRxFlowable(){
        HttpSerivce httpSerivce = new Retrofit.Builder().baseUrl("http://172.18.84.243:8080/agent_cloud/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(headInterceptor).addInterceptor(loggingInterceptor).build())
                .build().create(HttpSerivce.class);
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");
        Flowable<NetResult<User>> netResultFlowable = httpSerivce.loginFlowable(hashMap);

        netResultFlowable.subscribe(new Subscriber<NetResult<User>>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("TestRetrofitWithRx.onSubscribe");
                s.request(1);
            }

            @Override
            public void onNext(NetResult<User> userNetResult) {
                System.out.println("TestRetrofitWithRx.onNext "+userNetResult.getData().toString());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("TestRetrofitWithRx.onError");
            }

            @Override
            public void onComplete() {
                System.out.println("TestRetrofitWithRx.onComplete");
            }
        });
    }


    @Test
    public void testLoginWithRxFlowableAndMap(){
        HttpSerivce httpSerivce = new Retrofit.Builder().baseUrl("http://172.18.84.243:8080/agent_cloud/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(headInterceptor).addInterceptor(loggingInterceptor).build())
                .build().create(HttpSerivce.class);
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");
        Flowable<NetResult<User>> netResultFlowable = httpSerivce.loginFlowable(hashMap);
        netResultFlowable.map(new Function<NetResult<User>, User>() {
            @Override
            public User apply(NetResult<User> userNetResult) throws Exception {
                System.out.println("TestRetrofitWithRx.apply");
                return userNetResult.getData();
            }
        }).subscribe(new Subscriber<User>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("TestRetrofitWithRx.onSubscribe");
                s.request(1);
            }

            @Override
            public void onNext(User user) {
                System.out.println(user.toString());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("TestRetrofitWithRx.onError");
            }

            @Override
            public void onComplete() {
                System.out.println("TestRetrofitWithRx.onComplete");
            }
        });
    }


    @Test
    public void testLoginWithRxFlowableAndMapProgressBar(){
        HttpSerivce httpSerivce = new Retrofit.Builder().baseUrl("http://172.18.84.243:8080/agent_cloud/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(headInterceptor).addInterceptor(loggingInterceptor).build())
                .build().create(HttpSerivce.class);
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");
        Flowable<NetResult<User>> netResultFlowable = httpSerivce.loginFlowable(hashMap);
        netResultFlowable.map(new Function<NetResult<User>, User>() {
            @Override
            public User apply(NetResult<User> userNetResult) throws Exception {
                return userNetResult.getData();
            }
        }).subscribe(new Subscriber<User>() {

            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("TestRetrofitWithRx.onSubscribe");
                s.request(1);
            }

            @Override
            public void onNext(User user) {
                System.out.println(user.toString());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("TestRetrofitWithRx.onError");
            }

            @Override
            public void onComplete() {
                System.out.println("TestRetrofitWithRx.onComplete");
            }
        });
    }

    @Test
    public void testLoginWithRxFlowableAndMapException(){
        HttpSerivce httpSerivce = new Retrofit.Builder().baseUrl("http://172.18.84.243:8080/agent_cloud/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(headInterceptor).addInterceptor(loggingInterceptor).build())
                .build().create(HttpSerivce.class);
        final HashMap<String,String> hashMap = new HashMap();
        hashMap.put("loginId", "21048");
        hashMap.put("password", "12050646");
        Flowable<NetResult<User>> netResultFlowable = httpSerivce.loginFlowable(hashMap);
        netResultFlowable.map(new Function<NetResult<User>, User>() {
            @Override
            public User apply(NetResult<User> userNetResult) throws Exception {
//                if (userNetResult.getCode() == 0) {
//                    return userNetResult.getData();
//                }else{
                    throw new ApiException(userNetResult.getCode(), userNetResult.getMsg());
//                }

            }
        }).subscribe(new Subscriber<User>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("TestRetrofitWithRx.onSubscribe");
                s.request(1);
            }

            @Override
            public void onNext(User user) {
                System.out.println(user.toString());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("TestRetrofitWithRx.onError");
                if (t instanceof ApiException) {

                }
            }

            @Override
            public void onComplete() {
                System.out.println("TestRetrofitWithRx.onComplete");
            }
        });
    }



    Interceptor headInterceptor = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request original = chain.request();

            Request request = original.newBuilder()
                    .header("X-Token", "")
                    .header("X-appOS", "android")
                    .header("X-version", BuildConfig.VERSION_NAME)
                    .header("X-CaseId", "")
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        }
    };

    Interceptor loggingInterceptor = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            final long t1 = System.nanoTime();
            System.out.println(String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));

            okhttp3.Response response = chain.proceed(request);

            final long t2 = System.nanoTime();
            final String responseBody = response.body().string();
            System.out.println(String.format("Received response for %s in %.1fms%n%s%s", response.request().url(), (t2 - t1) / 1e6d, response.headers(), responseBody));
            return response.newBuilder()
                    .body(ResponseBody.create(response.body().contentType(), responseBody))
                    .build();
        }
    };


}
```
# 小结
1. 善用get将不容易mock出的对象暴露出来，方便mock。如上面的getLoginCall()方法

2. ArgumentCaptor可以捕获参数，更优雅的实现doAnswer式的回调效果.

3. mock和spy的区别，mock不会执行方法体，而spy会对方法进行真实的调用。而spy的适用场景，就如上面测试loginBiz中的一个案例。我们需要真实的执行其方法，检查其交互流程，但又对依赖到的方法或者对象进行mock，以改变其行为，这个时候spy就能很好的施展了。
    另外一定，使用的对象不同，mock主要是针对当前对象使用到的类，验证与其的交互；而spy主要针对当前对象，要改变当前对象部分方法的预期。
如下面示例： spy就改变了当前测试对象LoginBiz 自己的方法getLoginCall的返回预期，返回一个要使用到的类的mock对象。

```
    @Test
    public void testLogin3(){


        LoginBiz spy = spy(LoginBiz.class);

        String name = "adimin";
        String pw = "admin";
        OnLoginCallback onLoginCallback = mock(OnLoginCallback.class);
        //主要是mock Call对象
        final Call mock = mock(Call.class);
        doReturn(mock).when(spy).getLoginCall(anyString(), anyString());
        //spy会执行真实的login方法，而login中getLoginCall时，会返回上面预设的Call的mock对象
        //如果此处是LoginBiz的mock对象，那么login的真实方法是不会被执行的
        spy.login(name, pw, onLoginCallback);
        ArgumentCaptor<Callback> argumentCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(mock).enqueue(argumentCaptor.capture());

        Call<ResponseBody> call=null;
        Response<ResponseBody> response = null;

        argumentCaptor.getValue().onResponse(call,response);
        verify(onLoginCallback).onSuccess(any(UserData.class));

    }
```



```
public class LoginBiz {


    HttpSerivce httpSerivce;

    @Inject
    public LoginBiz(HttpSerivce httpSerivce) {
        this.httpSerivce = httpSerivce;
    }

    public void login(String name, String pw, final OnLoginCallback loginCallback) {
    //刚开始是这样写的，但是这样写 1.不方便测试，2.也破环方法的平行层级结构即步骤，他的步骤，一获取call对象，二执行。
        // HashMap<String, String> hashMap = new HashMap();
        // hashMap.put("loginId", name);
        // hashMap.put("password", pw);
        // Call<ResponseBody> login= RetrofitBuilder.getHttpService().login(hashMap);

        Call<ResponseBody> login = getLoginCall(name,pw);
        login.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loginCallback.onSuccess(new UserData());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


      Call<ResponseBody> getLoginCall(String name, String pw) {
        HashMap<String, String> hashMap = new HashMap();
        hashMap.put("loginId", name);
        hashMap.put("password", pw);
        return httpSerivce.login(hashMap);
    }

}
```
