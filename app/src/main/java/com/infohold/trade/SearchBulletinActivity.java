/*
 * Created by zhangchong on 4/19/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.infohold.trade.config.Constant;
import com.infohold.trade.model.Bulletin;
import com.infohold.trade.model.UserInfo;
import com.infohold.trade.util.ResourceManager;
import com.umeng.analytics.MobclickAgent;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchBulletinActivity extends BaseActivity implements SearchView.OnQueryTextListener {


    private static final String TAG = SearchBulletinActivity.class.getName();

    //请求获取数据条数
    private int currentItemIndex = 0;

    private SweetAlertDialog pDialogLoading;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    pDialogLoading = new SweetAlertDialog(SearchBulletinActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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
                    Toast.makeText(SearchBulletinActivity.this,ret,Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    ptrFrameLayout.refreshComplete();
                    //第一个参数是：数据是否为空；第二个参数是：是否还有更多数据
                    String has = (String) msg.obj;
                    if (Constant.HAS_CONTENT.equals(has)) {
                        loadMoreListViewContainer.loadMoreFinish(false, true);
                    }else{
                        loadMoreListViewContainer.loadMoreFinish(false, false);
                    }

                    bulletinAdapter.notifyDataSetChanged();
                    break;
                case 4:
                    ptrFrameLayout.refreshComplete();
                    String have = (String) msg.obj;
                    if (Constant.HAS_CONTENT.equals(have)) {
                        loadMoreListViewContainer.loadMoreFinish(false, true);
                    }else{
                        loadMoreListViewContainer.loadMoreFinish(false, false);
                    }

                    bulletinAdapter.notifyDataSetChanged();
                case 5:
                    ptrFrameLayout.refreshComplete();
                    break;
                default:
                    break;
            }

        }
    };


    @Bind(R.id.load_more_list_view_ptr_frame)
    PtrFrameLayout ptrFrameLayout;

    @Bind(R.id.load_more_list_view_container)
    LoadMoreListViewContainer loadMoreListViewContainer;

    @Bind(R.id.bulletin_main_list_view)
    ListView bulletinListView;

    @Bind(R.id.toolbar_search_bulletin)
    Toolbar toolbar;

    @Bind(R.id.search_view)
    SearchView searchView;

    private long exitTime = 0;
    private List<Bulletin> data = new ArrayList<>();
    private SearchBulletinAdapter bulletinAdapter;
    private String queryStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bulletin);

        ButterKnife.bind(this);

        bulletinAdapter = new SearchBulletinAdapter(SearchBulletinActivity.this, R.layout.activity_bulletin_item, data);
        bulletinListView.setAdapter(bulletinAdapter);
        bulletinListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bulletin bulletin = data.get(position);
                Intent detailBulletin = new Intent(SearchBulletinActivity.this, BulletinDetailActivity.class);
                detailBulletin.putExtra("data", bulletin);
                startActivity(detailBulletin);
            }
        });

        toolbar.setTitle(getString(R.string.action_search_bulletin));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchBulletinActivity.this.finish();
            }
        });

        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(false);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("查找活动");

        ptrFrameLayout = (PtrFrameLayout) findViewById(R.id.load_more_list_view_ptr_frame);
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, bulletinListView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                currentItemIndex = 0;
                requestDataQuery(currentItemIndex, Constant.PageSize,SearchBulletinActivity.this.queryStr,0);
            }
        });

        loadMoreListViewContainer.setAutoLoadMore(true);
        loadMoreListViewContainer.useDefaultFooter();
        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {

            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {

                currentItemIndex += Constant.PageSize;
                requestDataMoreQuery(currentItemIndex,Constant.PageSize,SearchBulletinActivity.this.queryStr);
            }
        });
    }


    /**
     * 获取查询内容的活动
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @param queryParam 查询内容
     */
    void requestDataQuery(final int pageNum, final int pageSize , String queryParam, final int type) {
        handler.sendEmptyMessage(0);


        UserInfo userInfo = ResourceManager.getInstance().getLoginUser(SearchBulletinActivity.this);
        String id = userInfo.getStoreId();
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder().add("firstRow","" + pageNum).add("listRow","" + pageSize).add("id",id);

        if (!TextUtils.isEmpty(queryParam)) {
            builder.add("activityName", queryParam);
        }
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder().url(Constant.ServiceEndpoint + "queryActivity").post(requestBody).build();
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

                try {
                    HashMap<String, Object> resMap = JSON.parseObject(responseText, HashMap.class);
                    String repCode = (String) resMap.get(Constant.RepCode);
                    String repMSG = (String) resMap.get(Constant.RepMSG);

                    if (Constant.RepCodeSuccess.equals(repCode)) {

                        if (type == 0) {
                            data.clear();
                        }

                        JSONObject repData = (JSONObject) resMap.get(Constant.RepData);
                        String totalNum = repData.getString("totalNum");
                        JSONArray list = repData.getJSONArray("list");

                        if (totalNum != null ) {
                            if ("0".equals(totalNum)) {
                                handler.sendEmptyMessage(1);
                                Message message = new Message();
                                message.what = 4;
                                message.obj = "";
                                handler.sendMessage(message);

                                return;
                            }
                        }

                        if (list == null) {
                            handler.sendEmptyMessage(1);

                            Message message = new Message();
                            message.what = 5;
                            handler.sendMessage(message);

                            return;
                        }

                        ArrayList<Bulletin> listJson = (ArrayList<Bulletin>) JSON.parseArray(JSON.toJSONString(list), Bulletin.class);

                        for (Bulletin b : listJson) {
                            data.add(b);
                        }

                        handler.sendEmptyMessage(1);
                        Message message = new Message();
                        message.what = 3;

                        if (currentItemIndex + pageSize <= Integer.parseInt(totalNum) ) {
                            message.obj = Constant.HAS_CONTENT;
                        }else{
                            message.obj = "";
                        }

                        handler.sendMessage(message);
                    }else{
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

    /**
     * 获取查询内容的活动
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @param queryParam 查询内容
     */
    void requestDataMoreQuery(final int pageNum, final int pageSize , String queryParam) {
        handler.sendEmptyMessage(0);

        UserInfo userInfo = ResourceManager.getInstance().getLoginUser(SearchBulletinActivity.this);
        String id = userInfo.getStoreId();
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder().add("firstRow","" + pageNum).add("listRow","" + pageSize).add("id",id);

        if (!TextUtils.isEmpty(queryParam)) {
            builder.add("activityName", queryParam);
        }
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder().url(Constant.ServiceEndpoint + "queryActivity").post(requestBody).build();
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

                try {
                    HashMap<String, Object> resMap = JSON.parseObject(responseText, HashMap.class);
                    String repCode = (String) resMap.get(Constant.RepCode);
                    String repMSG = (String) resMap.get(Constant.RepMSG);

                    if (Constant.RepCodeSuccess.equals(repCode)) {

                        JSONObject repData = (JSONObject) resMap.get(Constant.RepData);
                        String totalNum = repData.getString("totalNum");
                        JSONArray list = repData.getJSONArray("list");

                        if (totalNum != null ) {
                            if ("0".equals(totalNum)) {
                                handler.sendEmptyMessage(1);
                                Message message = new Message();
                                message.what = 4;
                                message.obj = "";
                                handler.sendMessage(message);
                                return;
                            }
                        }

                        if (list == null) {
                            handler.sendEmptyMessage(1);
                            Message message = new Message();
                            message.what = 4;
                            message.obj = "";
                            handler.sendMessage(message);
                            return;
                        }

                        ArrayList<Bulletin> listJson = (ArrayList<Bulletin>) JSON.parseArray(JSON.toJSONString(list), Bulletin.class);

                        for (Bulletin b : listJson) {
                            data.add(b);
                        }

                        handler.sendEmptyMessage(1);
                        Message message = new Message();
                        message.what = 4;

                        if (currentItemIndex + pageSize <= Integer.parseInt(totalNum) ) {
                            message.obj = Constant.HAS_CONTENT;
                        }else{
                            message.obj = "";
                        }


                        handler.sendMessage(message);
                    }else{
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

    void openCameraPermissionSetting() {
        new SweetAlertDialog(SearchBulletinActivity.this,SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.open_camera_permission_title))
                .setContentText(getString(R.string.open_camera_permission_message))
                .setConfirmText(getString(R.string.open_camera_permission_ok))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                    }
                })
                .setCancelText(getString(R.string.open_camera_permission_cancel))
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                    }
                }).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constant.REQUEST_CODE_ASK_PERMISSIONS_FROM_MAIN) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(SearchBulletinActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 0);
            }else{
                openCameraPermissionSetting();
            }
        }else if (requestCode == Constant.REQUEST_CODE_ASK_PERMISSIONS_FROM_ADAPTER) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(SearchBulletinActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 1);
            }else{
                openCameraPermissionSetting();
            }
        }
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (TextUtils.isEmpty(query)) {
            return false;
        }

        this.queryStr = query;
        requestDataQuery(currentItemIndex,Constant.PageSize,query,0);
        return true;

    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }
}
