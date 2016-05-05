/*
 * Created by zhangchong on 4/19/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.infohold.trade.config.Constant;
import com.infohold.trade.model.UserInfo;
import com.infohold.trade.util.Security;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {

    private static String TAG = LoginActivity.class.getName();

    private SweetAlertDialog pDialogLoading;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    pDialogLoading = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialogLoading.setTitleText("请等待");
                    pDialogLoading.setCancelable(false);
                    pDialogLoading.show();
                    break;
                case 1:
                    pDialogLoading.dismiss();
                    break;
                case 2:
                    String ret = (String) msg.obj;
                    Toast.makeText(LoginActivity.this,ret,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

        }
    };

    @Bind(R.id.login_page_user_name)
    EditText userNameEditText;

    @Bind(R.id.login_page_password)
    EditText passwordTextEdit;

    @Bind(R.id.login_page_login_button)
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        String defaultUserName = userNameEditText.getText().toString();
        if (!TextUtils.isEmpty(defaultUserName)) {
            userNameEditText.setSelection(defaultUserName.length());
        }

        String defaultPassword = passwordTextEdit.getText().toString();
        if (!TextUtils.isEmpty(defaultPassword)) {
            passwordTextEdit.setSelection(defaultPassword.length());
        }
    }


    @OnClick(R.id.login_page_login_button)
    void login() {
        if (attemptLogin()) {

//            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//            startActivity(intent);
                String userName = userNameEditText.getText().toString();
                String password = passwordTextEdit.getText().toString();

                password = Security.getInstance().MD5Hash(password);
                password = Security.getInstance().SHAHash(password);

                Log.d(TAG, password);

                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder().add("userName", userName).add("password", password).build();

                handler.sendEmptyMessage(0);

                Request request = new Request.Builder().url(Constant.ServiceEndpoint + "login").post(requestBody).build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "Login failed result:" + e.getLocalizedMessage());
                        handler.sendEmptyMessage(1);
                        Message message = new Message();
                        message.what = 2;
                        message.obj = getString(R.string.lost_connection);
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String retJsonStr = response.body().string();
                        Log.e(TAG, "Login success result:" + retJsonStr);

                        try {
                            HashMap<String, Object> resMap = JSON.parseObject(retJsonStr, HashMap.class);
                            String repCode = (String) resMap.get(Constant.RepCode);
                            String repMSG = (String) resMap.get(Constant.RepMSG);

                            if (Constant.RepCodeSuccess.equals(repCode)) {

                                JSONObject data = (JSONObject) resMap.get(Constant.RepData);
                                UserInfo userInfo = JSON.parseObject(JSON.toJSONString(data), UserInfo.class);
                                Log.d(TAG, userInfo.getStoreId());
                                Log.d(TAG, userInfo.getStorename());
                                Log.d(TAG, userInfo.getMemberGrade());

                                SharedPreferences.Editor editor = getSharedPreferences(Constant.USER_INFO, MODE_PRIVATE).edit();
                                editor.putString(Constant.USER_INFO_ID, userInfo.getStoreId());
                                editor.putString(Constant.USER_INFO_NAME, userInfo.getStorename());
                                editor.putString(Constant.USER_INFO_GRADE, userInfo.getMemberGrade());
                                editor.commit();

                                handler.sendEmptyMessage(1);

                                Message message = new Message();
                                message.what = 2;
                                message.obj = repMSG;
                                handler.sendMessage(message);

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();

                            } else {
                                handler.sendEmptyMessage(1);
                                Message message = new Message();
                                message.what = 2;
                                message.obj = repMSG;
                                handler.sendMessage(message);
                                Log.d(TAG, repMSG);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(1);
                            Message message = new Message();
                            message.what = 2;
                            message.obj = e.getLocalizedMessage();
                            handler.sendMessage(message);
                        }
                    }
                });
        }
    }


    boolean attemptLogin() {

        String userName = userNameEditText.getText().toString();
        String password = passwordTextEdit.getText().toString();

        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(LoginActivity.this, getString(R.string.login_page_user_name_empty), Toast.LENGTH_SHORT).show();
            return false;
        }


        if (!isUserNameValid(userName)) {
            Toast.makeText(LoginActivity.this, getString(R.string.login_page_user_name_invalid), Toast.LENGTH_SHORT).show();
            return false;
        }


        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this,getString(R.string.login_page_password_empty),Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isPasswordValid(password)) {
            Toast.makeText(LoginActivity.this,getString(R.string.login_page_password_invalid),Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isUserNameValid(String userName) {
        return userName.length() > 0;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}

