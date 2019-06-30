package com.hellotheworld.mineclearance;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.hellotheworld.mineclearance.Common.Rank.Bean.RankGradeEnum;
import com.hellotheworld.mineclearance.Common.Rank.Dao.RankListDao;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Background_Img_Change extends AppCompatActivity {
    private int iBACKGROUND_IMG;
    private int[] imageId = new int[]{R.drawable.background_0, R.drawable.background_1, R.drawable.background_2, R.drawable.background_3};
    private String[] title;
    private List<Map<String, Object>> listItems;
    private Map<String, Object> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.background_image_change);
        GridView gridview = (GridView) findViewById(R.id.GridView1);
        ImageButton btn_return = (ImageButton) findViewById(R.id.backReturn);
        btn_return.setOnClickListener(new View.OnClickListener() {//点击返回按钮
            @Override
            public void onClick(View v) {
                MainActivity.icurrentTheme = iBACKGROUND_IMG;
                returnBackgroundImgToMainScene();
            }
        });

        title = new String[]{"背景图片（基础）", "背景图片（初级）", "背景图片（中级）", "背景图片（高级）"};
        listItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < imageId.length; i++) {
            map = new HashMap<String, Object>();
            map.put("image", imageId[i]);
            map.put("title", title[i]);
            listItems.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.items, new String[]{"title", "image"}, new int[]{R.id.title, R.id.image});
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position==0){
                    setBackgroundImg(position);
                    Toast.makeText(Background_Img_Change.this, "当前选择背景  " + title[position].toString() + "。", Toast.LENGTH_LONG).show();
                }
                else if(position!=0&&setBackgroundUnloked(position-1)){
                    setBackgroundImg(position);
                    Toast.makeText(Background_Img_Change.this, "当前选择背景  " + title[position].toString() + "。", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(Background_Img_Change.this, "请先解锁  " + title[position].toString() + " 背景。", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void setBackgroundImg(int IMG_NUM) {
        iBACKGROUND_IMG = IMG_NUM;
    }

    private int returnBackgroundImg(int IMG_NUM) {
        return iBACKGROUND_IMG;
    }

    private void returnBackgroundImgToMainScene() {
        finish();
    }

    private boolean setBackgroundUnloked(int difficulty){
        RankGradeEnum RANK_GRADE_ENUM=RankGradeEnum.BASIC;
        switch(difficulty){
            case 0:
                RANK_GRADE_ENUM=RankGradeEnum.BASIC;
                break;
            case 1:
                RANK_GRADE_ENUM=RankGradeEnum.INTERMEDIATE;
                break;
            case 2:
                RANK_GRADE_ENUM=RankGradeEnum.ADVANCED;
                break;
        }
        List list = new RankListDao(this).getRankListByLevel(RANK_GRADE_ENUM);
        if(list.size()==0){
            return false;
        }
        else{
            return true;
        }
    }

}
