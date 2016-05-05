/*
 * Created by zhangchong on 4/19/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.infohold.trade.config.Constant;
import com.infohold.trade.model.UserInfo;
import com.infohold.trade.util.ResourceManager;
import com.infohold.trade.util.Security;
import com.infohold.trade.view.InputEditText;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangePasswordActivity extends BaseActivity {


    private static final String TAG = ChangePasswordActivity.class.getName();

    @Bind(R.id.password_page_user_name_text_view)
    TextView userNameTextView;
    
    @Bind(R.id.password_page_user_old_password_edit_text)
    InputEditText oldPasswordEditText;

    @Bind(R.id.password_page_user_new_password_edit_text)
    InputEditText newPasswordEditText;
    
    @Bind(R.id.password_page_user_new_password_confirm_edit_view)
    InputEditText confirmEditText;

    @Bind(R.id.passwd_page_user_button)
    Button saveButton;

    @Bind(R.id.toolbar_common)
    Toolbar toolbar;

    private SweetAlertDialog pDialogLoading;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    pDialogLoading = new SweetAlertDialog(ChangePasswordActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialogLoading.setTitleText("请等待");
                    pDialogLoading.setCancelable(false);
                    pDialogLoading.show();
                    break;
                case 1:
                    pDialogLoading.dismiss();
                    break;
                case 2:
                    Toast.makeText(ChangePasswordActivity.this, (CharSequence) msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passwd);

        ButterKnife.bind(this);
        toolbar.setTitle(getString(R.string.action_change_password));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordActivity.this.finish();
            }
        });

        UserInfo userInfo = ResourceManager.getInstance().getLoginUser(ChangePasswordActivity.this);
        if (userInfo != null && userInfo.getStorename() != null) {
            userNameTextView.setText(userInfo.getStorename());
        }
    }


    @OnClick(R.id.passwd_page_user_button) void saveBtnAction() {

        String userOldPasswordText = oldPasswordEditText.getText().toString();
        String newPasswordText = newPasswordEditText.getText().toString();
        String confirmPasswordText = confirmEditText.getText().toString();

        if (!newPasswordText.equals(confirmPasswordText)) {
            Toast.makeText(ChangePasswordActivity.this,"新密码与确认密码不相同",Toast.LENGTH_SHORT).show();
            return;
        }


        if (userOldPasswordText.equals(newPasswordText)) {
            Toast.makeText(ChangePasswordActivity.this,"新密码与旧密码相同",Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences preferences = getSharedPreferences(LoginActivity.class.getName(), MODE_PRIVATE);
        String storeId = preferences.getString("storeId", "");


        userOldPasswordText = Security.getInstance().MD5Hash(userOldPasswordText);
        userOldPasswordText = Security.getInstance().SHAHash(userOldPasswordText);

        newPasswordText = Security.getInstance().MD5Hash(newPasswordText);
        newPasswordText = Security.getInstance().SHAHash(newPasswordText);
        Log.d(TAG, userOldPasswordText);
        Log.d(TAG, newPasswordText);
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder().add("storeId", storeId)
                .add("oldPassword", userOldPasswordText).add("newPassword", newPasswordText).build();
        Request request = new Request.Builder().url(Constant.ServiceEndpoint + "changePassword").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, e.getLocalizedMessage());
                handler.sendEmptyMessage(1);
                Message message = new Message();
                message.what = 2;
                message.obj = e.getLocalizedMessage();
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Log.d(TAG, responseText);

                try {
                    HashMap<String, Object> resMap = JSON.parseObject(responseText, HashMap.class);
                    String repMSG = (String) resMap.get(Constant.RepMSG);

                    handler.sendEmptyMessage(1);

                    Message message = new Message();
                    message.what = 2;
                    message.obj = repMSG;
                    handler.sendMessage(message);
                }catch (Exception e) {
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


    @OnTextChanged(R.id.password_page_user_old_password_edit_text)
    void onOldPasswordChanged() {

    }

    @OnTextChanged(R.id.password_page_user_new_password_edit_text)
    void onNewPasswordChanged() {

    }

    @OnTextChanged(R.id.password_page_user_new_password_confirm_edit_view)
    void onConfirmPasswordChanged() {

    }

    boolean validOldPassword() {
        return true;
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
