package com.hellotheworld.mineclearance;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hellotheworld.mineclearance.Common.Rank.Bean.RankGradeEnum;
import com.hellotheworld.mineclearance.Common.Rank.Dao.RankListDao;

public class Success_Fail_Scene extends AppCompatActivity {
    private boolean bIF_SUCCESS=false;
    private int iTIME;
    private String address;
    private int difficulty;
    private String[] FAMOUS_QUOTES;
    private boolean UPDATE_FIRST_TIME=false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
            return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.win_and_fail);


        Intent intent = getIntent();
        iTIME = intent.getIntExtra("GameTime",-1);
        address = intent.getStringExtra("address");
        difficulty = intent.getIntExtra("difficulty",0);

        EditText name =(EditText)findViewById(R.id.Name);
        name.setTypeface(Typeface.createFromAsset(getAssets(),"font/MFBanHei_Noncommercial-Regular.otf"));
        ImageButton btn_Return =(ImageButton)findViewById(R.id.ReturnBtn);
        ImageButton btn_SendGrade =(ImageButton)findViewById(R.id.SendGradeBtn);
        ImageButton btn_PlayAgain =(ImageButton)findViewById(R.id.PlayAgainBtn);
        SetTime();
        setButton(bIF_SUCCESS);

        btn_Return.setOnClickListener(new View.OnClickListener() {//点击返回按钮
            @Override
            public void onClick(View v) {
                //返回主菜单
                returnMainScene();
            }
        });
        btn_SendGrade.setOnClickListener(new View.OnClickListener() {//点击返回按钮
            @Override
            public void onClick(View v) {
                //上传信息到排行榜
                uploadRankingList();
            }
        });
        btn_PlayAgain.setOnClickListener(new View.OnClickListener() {//点击返回按钮
            @Override
            public void onClick(View v) {
                //再玩一次（返回主游戏界面）
                playAgain();
            }
        });
    }
    private void setButton(boolean IF_SUCCESS){
        if(IF_SUCCESS){
            UPDATE_FIRST_TIME=true;
            setClearanceTime();
        }
        else{
            EditText name =(EditText)findViewById(R.id.Name);
            name.setVisibility(View.GONE);
            ImageButton btn_SendGrade =(ImageButton)findViewById(R.id.SendGradeBtn);
            btn_SendGrade.setEnabled(false);
            setFamousQuotes();
        }
    }
    private void setFamousQuotes(){
        TextView text =(TextView)findViewById(R.id.text);
        //将名言到text显示
        FAMOUS_QUOTES = new String[]{"古之立大事者，不惟有超世之才，亦必有坚韧不拔之志。——苏轼",
                "付出，不一定会有收获；不付出，却一定不会有收获，不要奢望出现奇迹。",
                "拥有梦想只是一种智力，实现梦想才是一种能力。"};
        text.setTypeface(Typeface.createFromAsset(getAssets(),"font/MFBanHei_Noncommercial-Regular.otf"));
        text.setText(FAMOUS_QUOTES[((int)(Math.random()*100) % FAMOUS_QUOTES.length)]);
        text.setTextSize(20);
    }
    private void setClearanceTime(){//设置通关时间
        TextView text =(TextView)findViewById(R.id.text);
        text.setTypeface(Typeface.createFromAsset(getAssets(),"font/MFBanHei_Noncommercial-Regular.otf"));
        text.setText("通关时间为 "+iTIME+" 秒");
    }
    private void uploadRankingList(){
        EditText name =(EditText)findViewById(R.id.Name);
        if(name.getText().toString()=="请输入用户名"){
            Toast.makeText(Success_Fail_Scene.this,"请先输入用户名再上传",Toast.LENGTH_LONG).show();
        }
        else{
            if(UPDATE_FIRST_TIME){
                UPDATE_FIRST_TIME=false;
                //上传通关时间和用户名到排行榜
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
                new RankListDao(this).InsertWinRecord(name.getText().toString(),(float)iTIME,RANK_GRADE_ENUM,address);
                Toast.makeText(Success_Fail_Scene.this,"已成功上传排行榜",Toast.LENGTH_LONG).show();
            }
        }
    }
    private void playAgain(){
        //返回主游戏界面
        Intent intent = new Intent(Success_Fail_Scene.this,GameSceneActivity.class);
        intent.putExtra("ReturnPage",false);
        setResult(0x718,intent);
        finish();
    }
    private void returnMainScene(){
        //返回主菜单
        Intent intent = new Intent(Success_Fail_Scene.this,GameSceneActivity.class);
        intent.putExtra("ReturnPage",true);
        setResult(0x718,intent);
        finish();
    }
    private void SetTime() {
        if (iTIME == -1) {
            bIF_SUCCESS = false;
        } else {
            bIF_SUCCESS = true;
        }
    }

}
