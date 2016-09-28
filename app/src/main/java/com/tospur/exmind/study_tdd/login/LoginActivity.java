package com.tospur.exmind.study_tdd.login;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tospur.exmind.study_tdd.R;
import com.tospur.exmind.study_tdd.net.DaggerNetComponent;
import com.tospur.exmind.study_tdd.net.NetModule;

import javax.inject.Inject;

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
        DaggerLoginCompent.builder().loginPresenterModule(new LoginPresenterModule(this)).netComponent(DaggerNetComponent.builder().netModule(new NetModule()).build()).build().inject(this);
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
        toast("恭喜，成功了");
    }

    @Override
    public void showLoginFailed(String errMsg) {

    }
}
