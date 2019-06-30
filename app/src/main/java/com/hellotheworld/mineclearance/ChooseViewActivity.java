package com.hellotheworld.mineclearance;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseViewActivity extends AppCompatActivity {

    private int iColumn;
    private int iRow;
    private int iMineCount;

    private Intent oIntent;

    final public int CODE = 0x717;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_view);

    }

    public void Basic(View v)
    {
        oIntent = new Intent(ChooseViewActivity.this, GameSceneActivity.class);
        iColumn = 9;
        iRow = 11;
        iMineCount = 10;
        oIntent.putExtra("Column", iColumn);
        oIntent.putExtra("Row", iRow);
        oIntent.putExtra("MineCount", iMineCount);
        oIntent.putExtra("Difficulty", 0);

        //开启activity
        startActivityForResult(oIntent,CODE);
    }

    public void Intermediate(View v)
    {
        oIntent = new Intent(ChooseViewActivity.this, GameSceneActivity.class);
        iColumn = 10;
        iRow = 13;
        iMineCount = 20;
        oIntent.putExtra("Column", iColumn);
        oIntent.putExtra("Row", iRow);
        oIntent.putExtra("MineCount", iMineCount);
        oIntent.putExtra("Difficulty", 1);

        //开启activity
        startActivityForResult(oIntent,CODE);
    }

    public void Advanced(View v)
    {
        oIntent = new Intent(ChooseViewActivity.this, GameSceneActivity.class);
        iColumn = 13;
        iRow = 17;
        iMineCount = 50;
        oIntent.putExtra("Column", iColumn);
        oIntent.putExtra("Row", iRow);
        oIntent.putExtra("MineCount", iMineCount);
        oIntent.putExtra("Difficulty", 2);

        //开启activity
        startActivityForResult(oIntent,CODE);
    }

    public void Return(View v)
    {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODE && resultCode == CODE)
        {
            finish();
        }
    }
}
