package com.haven.hckp.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haven.hckp.AppContext;
import com.haven.hckp.R;
import com.haven.hckp.api.ApiClient;
import com.haven.hckp.bean.URLs;
import com.haven.hckp.common.StringUtils;
import com.haven.hckp.common.UIHelper;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import java.util.HashMap;
import java.util.Map;

public class TeamFindActivity extends BaseActivity {

    @ViewInject(R.id.title_tv)
    private TextView mTitleTv;

    @ViewInject(R.id.btn_email)
    private TextView textEmail;

    @ViewInject(R.id.btn_pwd)
    private TextView textPwd;

    @ViewInject(R.id.btn_login)
    private Button btnLogin;

    @ViewInject(R.id.btn_register)
    private Button btnRegister;

    @ViewInject(R.id.progress_bar)
    private CircleProgressBar progressBar;

    @ViewInject(R.id.back_img)
    private ImageView backBtn;

    private AppContext appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);
        mTitleTv.setText(R.string.login_page);
        //显示返回按钮
        backBtn.setVisibility(View.VISIBLE);
        appContext = (AppContext) getApplicationContext();

    }

    @OnClick({R.id.btn_login, R.id.btn_register, R.id.back_img})
    public void buttonClick(View v) {

        switch (v.getId()) {
            case R.id.back_img:
                this.finish();
                break;
            case R.id.btn_login:
                loginAction();
                break;
            case R.id.btn_register:
                UIHelper.showRegisterRedirect(appContext);
                break;
        }
    }

    private void loginAction() {

        String emailStr = textEmail.getText().toString();
        String pwdStr = textPwd.getText().toString();
        if (StringUtils.isEmpty(emailStr) || StringUtils.isEmpty(pwdStr)) {
            UIHelper.ToastMessage(appContext, R.string.login_param_is_null);
            return;
        }
        String newUrl = ApiClient._MakeURL(URLs.LOGIN_POST, new HashMap<String, Object>());
        RequestParams params = new RequestParams();
        params.addBodyParameter("email", emailStr);
        params.addBodyParameter("pwd", pwdStr);
        HttpUtils http = new HttpUtils();
        progressBar.setVisibility(View.VISIBLE);
        http.send(HttpRequest.HttpMethod.POST, newUrl, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                progressBar.setVisibility(View.GONE);
                JSONObject obj = JSON.parseObject(objectResponseInfo.result);
                String code = obj.get("code").toString();
                if (code.equals("1")) {
                    Map<String,Object> userObj = (Map<String, Object>) obj.get("data");
                    appContext.setProperty("userId", StringUtils.toString(userObj.get("user_id")));
                    appContext.setProperty("userName", StringUtils.toString(userObj.get("user_username")));
                    appContext.setProperty("userPhone", StringUtils.toString(userObj.get("user_phone")));
                    appContext.setProperty("sessionId",  StringUtils.toString(userObj.get("session_id")));
                    UIHelper.ToastMessage(appContext, obj.get("msg").toString());
                    finish();
                } else {
                    UIHelper.ToastMessage(appContext, obj.get("msg").toString());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}