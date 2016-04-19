/*
 * Created by zhangchong on 4/19/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {

    @Bind(R.id.splash_image)
    ImageView imageView;

    @Bind(R.id.splash_version)
    TextView versionTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            versionTextView.setText("Version " + packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        AlphaAnimation animation = new AlphaAnimation(0.3f,1.0f);
        animation.setDuration(3000);
        imageView.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                imageView.setImageResource(R.drawable.icon);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}
