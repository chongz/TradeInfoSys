/*
 * Created by zhangchong on 5/6/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.infohold.trade.config.Constant;
import com.infohold.trade.model.ImageItem;
import com.infohold.trade.model.UserInfo;
import com.infohold.trade.util.FileUtils;
import com.infohold.trade.util.ResourceManager;
import com.umeng.analytics.MobclickAgent;
import com.zfdang.multiple_images_selector.ImagesSelectorActivity;
import com.zfdang.multiple_images_selector.SelectorSettings;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.infohold.trade.R.drawable.back;

public class NewBulletinActivity extends BaseActivity {

    @Bind(R.id.toolbar_new_bulletin)
    Toolbar toolbar;

    @Bind(R.id.new_bulletin_title)
    EditText title;

    @Bind(R.id.new_bulletin_thumb_grid_view)
    GridView gridThumView;

    @Bind(R.id.new_bulletin_text_context)
    EditText textContent;

    @Bind(R.id.new_bulletin_start)
    DatePicker startDatePicker;

    @Bind(R.id.new_bulletin_end)
    DatePicker endDatePicker;

    private String startDateString;
    private String endDateString;

    @Bind(R.id.new_bulletin_grid_view)
    GridView gridView;

    @Bind(R.id.create_new_bulletin_btn)
    Button publishBtn;

    private GridAdapter gridAdapter;
    private GridAdapter gridThumAdapter;

    private ArrayList<ImageItem> gridResults = new ArrayList<>();
    private ArrayList<String> gridData = new ArrayList<>();

    private ArrayList<ImageItem> gridThumResults = new ArrayList<>();
    private ArrayList<String> gridThumData = new ArrayList<>();

    private static final String TAG = "NewBulletinActivity";

    private SweetAlertDialog pDialogLoading;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    pDialogLoading = new SweetAlertDialog(NewBulletinActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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
                    Toast.makeText(NewBulletinActivity.this,ret,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bulletin);

        ButterKnife.bind(this);
        Log.d(TAG, "onCreate: ");

        toolbar.setTitle(getString(R.string.action_new_bulletin));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewBulletinActivity.this.finish();
            }
        });

        Bitmap Bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.icon_addpic_unfocused);
        FileUtils.saveBitmap(Bitmap,"icon_addpic_unfocused");


        gridThumResults.add(getDefaultImageItem());
        gridThumView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridThumAdapter = new GridAdapter(this, gridThumResults);
        gridThumView.setAdapter(gridThumAdapter);
        gridThumView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageItem imageItem = gridThumResults.get(position);
                if (imageItem.getType() == 0) {
                    if (ContextCompat.checkSelfPermission(NewBulletinActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(NewBulletinActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(NewBulletinActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.REQUEST_CODE_ASK_PERMISSIONS_FROM_PHOTO_THUMB_SELECTOR);
                    }else {
                        takeThumbPhotoSelectorAction();
                    }

                }else{
                    if (ContextCompat.checkSelfPermission(NewBulletinActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(NewBulletinActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(NewBulletinActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.REQUEST_CODE_ASK_PERMISSIONS_FROM_PHOTO_THUMB);
                    }else{
                        Intent intent = new Intent(NewBulletinActivity.this,PhotoViewerActivity.class);
                        intent.putStringArrayListExtra("data", gridThumData);
                        startActivityForResult(intent,Constant.REQUEST_CODE_PHOTO_VIEWER_THUMB);
                    }
                }
            }
        });

        gridResults.add(getDefaultImageItem());
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridAdapter = new GridAdapter(this, gridResults);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageItem imageItem = gridResults.get(position);
                if (imageItem.getType() == 0) {
                    if (ContextCompat.checkSelfPermission(NewBulletinActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(NewBulletinActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(NewBulletinActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.REQUEST_CODE_ASK_PERMISSIONS_FROM_PHOTO_SELECTOR);
                    }else {
                        takePhotoSelectorAction();
                    }

                }else{
                    if (ContextCompat.checkSelfPermission(NewBulletinActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(NewBulletinActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(NewBulletinActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.REQUEST_CODE_ASK_PERMISSIONS_FROM_PHOTO);
                    }else{
                        Intent intent = new Intent(NewBulletinActivity.this,PhotoViewerActivity.class);
                        intent.putStringArrayListExtra("data", gridData);
                        startActivityForResult(intent,Constant.REQUEST_CODE_PHOTO_VIEWER);
                    }

                }
            }
        });

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Date currentDate = new Date();
        startDatePicker.setMinDate(currentDate.getTime()-1000);

        startDatePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            }
        });

        endDatePicker.setMinDate(currentDate.getTime() - 1000);
        endDatePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            }
        });
    }

    boolean checkPublish() {

        if (TextUtils.isEmpty(title.getText())) {
            Toast.makeText(NewBulletinActivity.this, getString(R.string.new_bulletin_title_empty), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (gridThumData.size() == 0) {
            Toast.makeText(NewBulletinActivity.this, getString(R.string.new_bulletin_thumb_pic_empty), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(textContent.getText())) {
            Toast.makeText(NewBulletinActivity.this, getString(R.string.new_bulletin_content_empty), Toast.LENGTH_SHORT).show();
            return false;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date activityStartDate = null;
        Date activityEndDate = null;
        try {
            startDateString = "" + startDatePicker.getYear() + "-" + (startDatePicker.getMonth() + 1) + "-" + startDatePicker.getDayOfMonth();
            activityStartDate = simpleDateFormat.parse(startDateString);
            endDateString = "" + endDatePicker.getYear() + "-" + (endDatePicker.getMonth()+1) + "-" + endDatePicker.getDayOfMonth();
            activityEndDate = simpleDateFormat.parse(endDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "checkPublish: activityStartDate:" + activityStartDate);
        Log.d(TAG, "checkPublish: activityEndDate:" + activityEndDate);


        if (activityEndDate.before(activityStartDate)) {
            Toast.makeText(NewBulletinActivity.this, getString(R.string.new_bulletin_end_date_before_start_date), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (gridData.size() == 0) {
            Toast.makeText(NewBulletinActivity.this, getString(R.string.new_bulletin_pic_content_empty), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @OnClick(R.id.create_new_bulletin_btn)
    void publish() {
        if (!checkPublish()) {
            return;
        }

        handler.sendEmptyMessage(0);
        UserInfo userInfo = ResourceManager.getInstance().getLoginUser(NewBulletinActivity.this);


        OkHttpClient okHttpClient = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"activityName\""),RequestBody.create(null, title.getText().toString()));
        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"storeId\""),RequestBody.create(null, userInfo.getStoreId()));
        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"contText\""),RequestBody.create(null, textContent.getText().toString()));
        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"activityStartDate\""),RequestBody.create(null, startDateString));
        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"activityEndDate\""),RequestBody.create(null, endDateString));
        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"applyStatus\""),RequestBody.create(null, "0"));

        for (int i=0; i< gridData.size(); i++) {
            String path = gridData.get(i);
            File file = new File(path);
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
            builder.addPart(Headers.of("Content-Disposition","form-data; name=\"activityPic\";filename=\"activityPic.png\""), fileBody);
        }

        for (int i=0; i< gridThumData.size(); i++) {
            String path = gridThumData.get(i);
            File file = new File(path);
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
            builder.addPart(Headers.of("Content-Disposition","form-data; name=\"contPic\";filename=\"contPic.png\""), fileBody);
        }


        RequestBody requestBody = builder.build();

        Request request = new Request.Builder().url(Constant.ServiceEndpoint + "applyActivity").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                    handler.sendEmptyMessage(1);

                    Message message = new Message();
                    message.what = 2;
                    message.obj = getString(R.string.lost_connection);
                    handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: " + response.body().string());

                String retJsonStr = response.body().string();
                try {
                    HashMap<String, Object> resMap = JSON.parseObject(retJsonStr, HashMap.class);
                    String repCode = (String) resMap.get(Constant.RepCode);
                    String repMSG = (String) resMap.get(Constant.RepMSG);

                    if (Constant.RepCodeSuccess.equals(repCode)) {

                        handler.sendEmptyMessage(1);

                        Message message = new Message();
                        message.what = 2;
                        message.obj = repMSG;
                        handler.sendMessage(message);

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

    void takeThumbPhotoSelectorAction() {
        Intent intent = new Intent(NewBulletinActivity.this, ImagesSelectorActivity.class);
        intent.putExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, Constant.MAX_DISPLAY_PIC);
        intent.putExtra(SelectorSettings.SELECTOR_MIN_IMAGE_SIZE, 100000);
        intent.putExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, true);
        intent.putStringArrayListExtra(SelectorSettings.SELECTOR_INITIAL_SELECTED_LIST, gridThumData);
        startActivityForResult(intent,Constant.REQUEST_CODE_FROM_NEW_BULLETIN_THUMB_GRID_VIEW);
    }

    void takePhotoSelectorAction() {
        Intent intent = new Intent(NewBulletinActivity.this, ImagesSelectorActivity.class);
        intent.putExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, Constant.MAX_DISPLAY_PIC);
        intent.putExtra(SelectorSettings.SELECTOR_MIN_IMAGE_SIZE, 100000);
        intent.putExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, true);
        intent.putStringArrayListExtra(SelectorSettings.SELECTOR_INITIAL_SELECTED_LIST, gridData);
        startActivityForResult(intent,Constant.REQUEST_CODE_FROM_NEW_BULLETIN);
    }

    void removeThumPlus() {
        for (ImageItem imageItem : gridThumResults) {
            if (imageItem.getType() == 0) {
                gridThumResults.remove(imageItem);
                break;
            }
        }
    }

    void removePlus() {
        for (ImageItem imageItem : gridResults) {
            if (imageItem.getType() == 0) {
                gridResults.remove(imageItem);
                break;
            }
        }
    }

    ImageItem getDefaultImageItem() {
        return new ImageItem("icon_addpic_unfocused.jpg",false,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constant.REQUEST_CODE_FROM_NEW_BULLETIN) {
            if(resultCode == RESULT_OK) {
                gridData = data.getStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS);
                assert gridData != null;

                gridResults.clear();
                for(String result : gridData) {
                    gridResults.add(new ImageItem(result,false,1));
                }

                removePlus();
                gridResults.add(getDefaultImageItem());
                gridAdapter.notifyDataSetChanged();
            }
        }else if (requestCode == Constant.REQUEST_CODE_FROM_NEW_BULLETIN_THUMB_GRID_VIEW) {
            if(resultCode == RESULT_OK) {
                gridThumData = data.getStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS);
                assert gridThumData != null;

                gridThumResults.clear();
                for(String result : gridThumData) {
                    gridThumResults.add(new ImageItem(getPicPath(result),false,1));
                }

                removeThumPlus();
                gridThumResults.add(getDefaultImageItem());
                gridThumAdapter.notifyDataSetChanged();
            }
        }else if(requestCode == Constant.REQUEST_CODE_PHOTO_VIEWER_THUMB) {
            if(resultCode == RESULT_OK) {
                ArrayList<String> ret = data.getStringArrayListExtra("data");
                gridThumResults.clear();
                gridThumData.clear();
                gridThumData.addAll(ret);
                for(String result : gridThumData) {
                    gridThumResults.add(new ImageItem(getPicPath(result),false,1));
                }

                removeThumPlus();
                gridThumResults.add(getDefaultImageItem());

                gridThumAdapter.notifyDataSetChanged();
            }

        }else if(requestCode == Constant.REQUEST_CODE_PHOTO_VIEWER) {
            if(resultCode == RESULT_OK) {
                ArrayList<String> ret = data.getStringArrayListExtra("data");
                gridData.clear();
                gridData.addAll(ret);

                gridResults.clear();
                for(String result : gridData) {
                    gridResults.add(new ImageItem(result,false,1));
                }
                removePlus();
                gridResults.add(getDefaultImageItem());
                gridAdapter.notifyDataSetChanged();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    String getPicPath(String virtualPath) {
        String path = virtualPath;
//        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.pathSeparator + virtualPath;
        return path;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constant.REQUEST_CODE_ASK_PERMISSIONS_FROM_PHOTO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(NewBulletinActivity.this,PhotoViewerActivity.class);
                intent.putStringArrayListExtra("data", gridData);
                startActivityForResult(intent,Constant.REQUEST_CODE_PHOTO_VIEWER);
            }else{
                openReadWriteStoragePermissionSetting();
            }
        }else if (requestCode == Constant.REQUEST_CODE_ASK_PERMISSIONS_FROM_PHOTO_THUMB) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(NewBulletinActivity.this,PhotoViewerActivity.class);
                intent.putStringArrayListExtra("data", gridThumData);
                startActivityForResult(intent,Constant.REQUEST_CODE_PHOTO_VIEWER_THUMB);
            }else{
                openReadWriteStoragePermissionSetting();
            }
        }else if (requestCode == Constant.REQUEST_CODE_ASK_PERMISSIONS_FROM_PHOTO_SELECTOR) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhotoSelectorAction();
            }else{
                openReadWriteStoragePermissionSetting();
            }
        }else if (requestCode == Constant.REQUEST_CODE_ASK_PERMISSIONS_FROM_PHOTO_THUMB_SELECTOR) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takeThumbPhotoSelectorAction();
            }else{
                openReadWriteStoragePermissionSetting();
            }
        }
    }

    void openReadWriteStoragePermissionSetting() {
        new SweetAlertDialog(NewBulletinActivity.this,SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.open_read_write_storage_permission_title))
                .setContentText(getString(R.string.open_read_write_storage_permission_message))
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


    public class GridAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private ArrayList<ImageItem> imageItems;

        public GridAdapter(Context context,ArrayList<ImageItem> imageItems) {
            inflater = LayoutInflater.from(context);
            this.imageItems = imageItems;
        }


        @Override
        public int getCount() {
            if (imageItems.size() == Constant.MAX_DISPLAY_PIC - 1) {
                return Constant.MAX_DISPLAY_PIC - 1;
            }

            return imageItems.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activitiy_new_bulletin_adapter, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ImageItem imageItem =  this.imageItems.get(position);

            if (imageItem.getType() == 0) {
                viewHolder.imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.icon_addpic_unfocused));
                if (position == Constant.MAX_DISPLAY_PIC) {
                    viewHolder.imageView.setVisibility(View.GONE);
                }
            }else{
                Bitmap d = new BitmapDrawable(getResources() , imageItem.getImagePath()).getBitmap();
                int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
                viewHolder.imageView.setImageBitmap(scaled);
//                viewHolder.imageView.setImageBitmap(imageItem.getBitmap());
            }

            return convertView;
        }

        public ArrayList<ImageItem> getImageItems() {
            return imageItems;
        }

        public void setImageItems(ArrayList<ImageItem> imageItems) {
            this.imageItems = imageItems;
        }

        class ViewHolder {
            @Bind(R.id.new_bulletin_adapter_image_view)
            ImageView imageView;

            ViewHolder(View view) {
                ButterKnife.bind(this,view);
            }
        }
    }
}
