package com.hellotheworld.mineclearance.Common.Rank.Activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hellotheworld.mineclearance.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ScreenShotActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iScreenShoot;

    public static Integer IMAGE_NOT_FOUND = 123;
    public static Integer IMAGE_TURN_BACK_SUCCESS = 456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_shot);
        //隐藏标题栏
        ActionBar bar = getSupportActionBar();
        if(bar != null){
            bar.hide();
        }
        iScreenShoot = findViewById(R.id.screen_shot_image);

        //根据路径获取图片资源
        Intent intent = getIntent();
//        Uri uri = intent.getData();
//        String img_url = uri.getPath();

        String img_url = intent.getStringExtra("url");
        String img_name = img_url.substring(img_url.lastIndexOf("/") + 1, img_url.length());
        try {
            FileInputStream localStream = openFileInput(img_name);
            Bitmap bitmap = BitmapFactory.decodeStream(localStream);
            //Bitmap bitmap = getLocalBitmap(img_url);
            iScreenShoot.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            //未找到图片则返回
            setResult(IMAGE_NOT_FOUND);
            finish();
        }

    }

    public void back(View v) {
        setResult(IMAGE_TURN_BACK_SUCCESS);
        finish();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.screen_shot_image:
                back(v);
        }
    }

    /**
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLocalBitmap(String url) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(url);
        return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片
    }

    public Bitmap getRes(String name) {
        ApplicationInfo appInfo = getApplicationInfo();
        int resID = getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return BitmapFactory.decodeResource(getResources(), resID);
    }

}
