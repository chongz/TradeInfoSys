/*
 * Created by zhangchong on 4/19/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.infohold.trade.config.Constant;
import com.infohold.trade.model.Member;
import com.infohold.trade.model.PersonInfoData;
import com.infohold.trade.model.ScanResult;
import com.infohold.trade.model.UserInfo;
import com.infohold.trade.util.ResourceManager;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

public class PersonInfoActivity extends BaseActivity {

    private static final String TAG = PersonInfoActivity.class.getName();

    private String picUrl;
    private SweetAlertDialog pDialogLoading;
    List<PersonInfoData> infoDatas = new ArrayList<>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    pDialogLoading = new SweetAlertDialog(PersonInfoActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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
                    Toast.makeText(PersonInfoActivity.this,ret,Toast.LENGTH_SHORT).show();
                    break;
                case 10:
                    Glide.with(PersonInfoActivity.this).load(Constant.ServicePicAddress + picUrl).placeholder(R.drawable.portrait_view).into(portraitView);

                    personInfoAdapter = new PersonInfoAdapter(PersonInfoActivity.this, R.layout.activity_person_info_list_view_item, infoDatas);
                    listView.setAdapter(personInfoAdapter);
                    break;
                case 11:
                    finish();
                    break;
                case 12:
                    okBtn.setVisibility(View.INVISIBLE);
                    cancelBtn.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }

        }
    };
    @Bind(R.id.toolbar_common)
    Toolbar toolbar;

    @Bind(R.id.person_info_pic)
    ImageView portraitView;

    @Bind(R.id.person_info_list_view)
    ListView listView;

    @Bind(R.id.person_info_ok_button)
    Button okBtn;

    @Bind(R.id.person_info_cancel_button)
    Button cancelBtn;

    private ScanResult scanResult;
    private UserInfo userInfo;
    private PersonInfoAdapter personInfoAdapter;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        ButterKnife.bind(this);

        this.userInfo = ResourceManager.getInstance().getLoginUser(PersonInfoActivity.this);
        if (userInfo != null && userInfo.getMemberGrade() != null) {
            String grade = userInfo.getMemberGrade();
            if (Constant.ADMIN.equals(grade)) {
                isAdmin = true;
                //admin用户不能下订单
                okBtn.setVisibility(View.INVISIBLE);
                cancelBtn.setVisibility(View.INVISIBLE);
            }else{
                isAdmin = false;
                okBtn.setVisibility(View.VISIBLE);
                cancelBtn.setVisibility(View.VISIBLE);
            }
        }

        Bundle bundle = getIntent().getExtras();
        this.scanResult = bundle.getParcelable(Constant.PutExtraDataStr);


        if (TextUtils.isEmpty(this.scanResult.getMemberId())) {
            handler.sendEmptyMessage(12);
            Toast.makeText(PersonInfoActivity.this,getString(R.string.person_barcode_invalid_scan_info),Toast.LENGTH_SHORT).show();
        }else {
            searchPersonInfo(this.scanResult.getMemberId());
        }

        toolbar.setTitle(getString(R.string.person_barcode_page_title));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonInfoActivity.this.finish();
            }
        });
    }


    @OnClick(R.id.person_info_ok_button)
    void addOrder() {
        handler.sendEmptyMessage(0);

        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody  = new FormBody.Builder()
                .add("userId",scanResult.getMemberId())
                .add("bulletId",scanResult.getBulletinId())
                .add("channelId","android")
                .add("sellerId",this.userInfo.getStoreId()).build();
        Request request = new Request.Builder().url(Constant.ServiceEndpoint + "commitOrder").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, e.getLocalizedMessage());
                handler.sendEmptyMessage(1);
                Message message = new Message();
                message.what = 2;
                message.obj = getString(R.string.lost_connection);
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Log.d(TAG, responseText);

                HashMap<String, Object> resMap = JSON.parseObject(responseText, HashMap.class);
                String repCode = (String) resMap.get(Constant.RepCode);
                String repMSG = (String) resMap.get(Constant.RepMSG);

                if (Constant.RepCodeSuccess.equals(repCode)) {
                    handler.sendEmptyMessage(1);
                    Message message = new Message();
                    message.what = 2;
                    message.obj = repMSG;
                    handler.sendMessage(message);

                    PersonInfoActivity.this.finish();
                }else{
                    handler.sendEmptyMessage(1);
                    Message message = new Message();
                    message.what = 2;
                    message.obj = repMSG;
                    handler.sendMessage(message);
                }
            }
        });
    }

    @OnClick(R.id.person_info_cancel_button)
    void cancelOrder() {
        finish();
    }


    void searchPersonInfo(String userId) {

        handler.sendEmptyMessage(0);

        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder().add("userId",userId).build();
        Request request = new Request.Builder().url(Constant.ServiceEndpoint + "queryMemberInfo").post(requestBody).build();
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
                    String repCode = (String) resMap.get(Constant.RepCode);
                    String repMSG = (String) resMap.get(Constant.RepMSG);

                    if (Constant.RepCodeSuccess.equals(repCode)) {

                        JSONObject repData = (JSONObject) resMap.get(Constant.RepData);
                        Member member = JSON.parseObject(JSON.toJSONString(repData), Member.class);

                        infoDatas.clear();

                        picUrl = member.getUserPic();
                        if (isAdmin) {
                            //admin用户查看全部个人信息
//                            infoDatas.add(new PersonInfoData("", member.getUserPic()));

                            infoDatas.add(new PersonInfoData("用户Id", member.getUserId()));
                            infoDatas.add(new PersonInfoData("用户名", member.getUserName()));
                            infoDatas.add(new PersonInfoData("所属基层工会", member.getOrgname()));
                            infoDatas.add(new PersonInfoData("身份证号码", member.getCard()));
                            infoDatas.add(new PersonInfoData("联系电话", member.getPhone()));
                            infoDatas.add(new PersonInfoData("性别", member.getSexDisplay()));
                        }else{
//                            infoDatas.add(new PersonInfoData("", member.getUserPic()));
                            infoDatas.add(new PersonInfoData("用户名", member.getUserName()));
                        }




                        handler.sendEmptyMessage(1);
                        Message message = new Message();
                        message.what = 10;
                        handler.sendMessage(message);

                    } else {
                        handler.sendEmptyMessage(12);
                        handler.sendEmptyMessage(1);
                        Message message = new Message();
                        message.what = 2;
                        message.obj = repMSG;
                        handler.sendMessage(message);
                    }
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
