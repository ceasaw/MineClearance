package com.hellotheworld.mineclearance;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.hellotheworld.mineclearance.Common.Rank.Activity.RankActivity;

public class MainActivity extends AppCompatActivity {

    public static int icurrentTheme = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_main);

        ImageButton startGame = (ImageButton)findViewById(R.id.DifficultyButton);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ChooseViewActivity.class);
                startActivity(intent);
            }
        });

        ImageButton selectTheme = (ImageButton)findViewById(R.id.ThemeButton);
        selectTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Background_Img_Change.class);
                startActivity(intent);
            }
        });

        ImageButton rankingList = (ImageButton)findViewById(R.id.RankinglistButton);
        rankingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RankActivity.class));
            }
        });

        ImageButton exitGame = (ImageButton)findViewById(R.id.ExitButton);
        exitGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        });

    }


}
