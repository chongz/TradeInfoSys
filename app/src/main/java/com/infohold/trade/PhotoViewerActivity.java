/*
 * Created by zhangchong on 5/10/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoViewerActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.del_btn)
    Button delete;

    @Bind(R.id.image_view)
    ImageView imageView;

    private PhotoViewAttacher photoViewAttacher;
    private ArrayList<String> data;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        ButterKnife.bind(this);

        toolbar.setTitle(getString(R.string.action_back));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra("data", data);
                setResult(RESULT_OK,intent);
                PhotoViewerActivity.this.finish();
            }
        });

        data = getIntent().getStringArrayListExtra("data");
        index = 0;
        Bitmap bitmap = BitmapFactory.decodeFile(data.get(index));
        imageView.setImageBitmap(bitmap);
        photoViewAttacher = new PhotoViewAttacher(imageView);
    }


    @OnClick(R.id.del_btn)
    void deleteSelectedImg() {

        data.remove(index);
        if (data.size() == 0) {
            Intent intent = new Intent();
            intent.putStringArrayListExtra("data", data);
            setResult(RESULT_OK,intent);
            finish();
        }
    }
}
