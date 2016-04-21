/*
 * Created by zhangchong on 4/19/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.infohold.trade.config.Constant;
import com.infohold.trade.model.Bulletin;
import com.infohold.trade.model.ScanResult;
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
import in.srain.cube.util.LocalDisplay;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static String TAG = MainActivity.class.getName();

    //请求获取数据条数
    private int currentItemIndex = 0;
    private int pageSize = 1;
    private Bulletin selectBulletin;

    private SweetAlertDialog pDialogLoading;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    pDialogLoading = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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
                    Toast.makeText(MainActivity.this,ret,Toast.LENGTH_SHORT).show();
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
                    if ("has".equals(have)) {
                        loadMoreListViewContainer.loadMoreFinish(false, true);
                    }else{
                        loadMoreListViewContainer.loadMoreFinish(false, false);
                    }

                    bulletinAdapter.notifyDataSetChanged();
                default:
                    break;
            }

        }
    };
    @Bind(R.id.toolbar_common)
    Toolbar toolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Bind(R.id.nav_view)
    NavigationView navigationView;

    //登录后抽屉用户信息
    TextView navUserName;
    TextView navUserDesc;

    @Bind(R.id.load_more_list_view_ptr_frame)
    PtrFrameLayout ptrFrameLayout;

    @Bind(R.id.load_more_list_view_container)
    LoadMoreListViewContainer loadMoreListViewContainer;

    @Bind(R.id.bulletin_main_list_view)
    ListView bulletinListView;

    private long exitTime = 0;
    private List<Bulletin> data = new ArrayList<>();
    private BulletinAdapter bulletinAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        toolbar.setTitle(getString(R.string.action_bulletin));
        setSupportActionBar(toolbar);


        bulletinAdapter = new BulletinAdapter(MainActivity.this, R.layout.activity_bulletin_item,data);
        bulletinListView.setAdapter(bulletinAdapter);

        bulletinListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bulletin bulletin = data.get(position-1);
                Intent detailBulletin = new Intent(MainActivity.this, BulletinDetailActivity.class);
                detailBulletin.putExtra("data", bulletin);
                startActivity(detailBulletin);
            }
        });

        View headerMarginView = new View(this);
        headerMarginView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LocalDisplay.dp2px(20)));
        bulletinListView.addHeaderView(headerMarginView);

        ptrFrameLayout = (PtrFrameLayout) findViewById(R.id.load_more_list_view_ptr_frame);
        ptrFrameLayout.setLoadingMinTime(1000);
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, bulletinListView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                currentItemIndex = 0;
                requestDataQuery(currentItemIndex, pageSize,"",0);
            }
        });


        requestDataQuery(currentItemIndex, pageSize,"",0);

        loadMoreListViewContainer.setAutoLoadMore(true);
        loadMoreListViewContainer.useDefaultFooter();
        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {

                currentItemIndex += pageSize;
                requestDataMoreQuery(currentItemIndex,pageSize,"");
            }
        });


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        Log.d(TAG, "onCreate");


        View headerView = navigationView.getHeaderView(0);
        navUserName = (TextView) headerView.findViewById(R.id.nav_header_user_name);
        navUserDesc = (TextView) headerView.findViewById(R.id.nav_header_user_desc);

        UserInfo userInfo = ResourceManager.getInstance().getLoginUser(MainActivity.this);
        String userName = userInfo.getStorename();
        String userDesc = userInfo.getMemberGrade();

        navUserName.setText(userName);
        if ("1".equals(userDesc)) {
            navUserDesc.setText("管理员");
        }else if("0".equals(userDesc)){
            navUserDesc.setText("普通用户");
        }
    }

    /**
     * 获取查询内容的活动
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @param queryParam 查询内容
     */
    void requestDataQuery(final int pageNum, final int pageSize , String queryParam, final int type) {
        handler.sendEmptyMessage(0);

        SharedPreferences preferences = getSharedPreferences(LoginActivity.class.getName(), MODE_PRIVATE);
        UserInfo userInfo = ResourceManager.getInstance().getLoginUser(MainActivity.this);
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

                        if (list == null) {
                            handler.sendEmptyMessage(1);

                            Message message = new Message();
                            message.what = 4;
                            message.obj = Constant.HAS_EMPTY_CONTENT;
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
                            message.obj = Constant.HAS_EMPTY_CONTENT;
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

        UserInfo userInfo = ResourceManager.getInstance().getLoginUser(MainActivity.this);
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

                        if (list == null) {
                            handler.sendEmptyMessage(1);

                            Message message = new Message();
                            message.what = 4;
                            message.obj = Constant.HAS_EMPTY_CONTENT;
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
                            message.obj = Constant.HAS_EMPTY_CONTENT;
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString("result");
                Log.d(TAG, result);

                Intent intent = new Intent(MainActivity.this, PersonInfoActivity.class);

                Bulletin bulletin = ResourceManager.getInstance().getSelectedBulletin();
                ScanResult scanResult = new ScanResult(result);
                if (bulletin!=null) {
                    scanResult.setBulletinId(bulletin.getActivityId());
                }
                intent.putExtra(Constant.PutExtraDataStr, scanResult);
                startActivityForResult(intent,1);
            }else if (requestCode == 1) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString("result");
                Log.d(TAG, result);

                Intent intent = new Intent(MainActivity.this, PersonInfoActivity.class);

                Bulletin bulletin = ResourceManager.getInstance().getSelectedBulletin();
                ScanResult scanResult = new ScanResult(result);
                if (bulletin!=null) {
                    scanResult.setBulletinId(bulletin.getActivityId());
                }
                intent.putExtra(Constant.PutExtraDataStr, scanResult);
                startActivityForResult(intent,1);
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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent intent = new Intent(MainActivity.this, SearchBulletinActivity.class);
            startActivity(intent);
        }else if (id == R.id.action_change_password) {
            Intent intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            return true;
        }else if (id == R.id.action_about_us) {
            Intent intent = new Intent(MainActivity.this,AboutUsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {

                Toast.makeText(getApplicationContext(), getString(R.string.close_our_app_promote), Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constant.REQUEST_CODE_ASK_PERMISSIONS_FROM_MAIN) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 0);
            }else{
                openCameraPermissionSetting();
            }
        }else if (requestCode == Constant.REQUEST_CODE_ASK_PERMISSIONS_FROM_ADAPTER) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 1);
            }else{
                openCameraPermissionSetting();
            }
        }
    }

    void openCameraPermissionSetting() {
        new SweetAlertDialog(MainActivity.this,SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.open_camera_permission_title))
                .setContentText(getString(R.string.open_camera_permission_message))
                .setConfirmText(getString(R.string.open_camera_permission_ok))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();

                        Intent openIntent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                        String pkg = "com.android.settings";
                        String cls = "com.android.settings.applications.InstalledAppDetails";

                        openIntent.setComponent(new ComponentName(pkg, cls));
                        openIntent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(openIntent);

                    }
                })
                .setCancelText(getString(R.string.open_camera_permission_cancel))
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                }).show();
    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_scan) {

            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.CAMERA},Constant.REQUEST_CODE_ASK_PERMISSIONS_FROM_MAIN);
            }else if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 0);
            }

        }else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setIcon(R.drawable.icon);
            builder.setTitle(getString(R.string.logout_title));
            builder.setMessage(getString(R.string.logout_message));
            builder.setPositiveButton(getString(R.string.logout_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginIntent);
                    MainActivity.this.finish();
                }
            });
            builder.setNegativeButton(getString(R.string.logout_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();
        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}
