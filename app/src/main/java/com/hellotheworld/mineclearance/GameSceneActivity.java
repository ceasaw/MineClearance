package com.hellotheworld.mineclearance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Thread.*;

public class GameSceneActivity extends AppCompatActivity {

    private static final int UPDATE_TEXT = 1;
    private final int CODE = 0x718;
    private static int gameTime = 0;
    private TextView timer;
    private TextView mineNum;

    private int[][] array;

    private int[] imageRes;
    private GameSceneActivity.THEME theme;
    private int leftoverMine;
    private int allMine;
    private boolean isFlag[][];
    private boolean isOpen[][];
    private GridLayout mineLayout;
    private int widthMine;
    private int heightMine;
    private int iDifficulty;

    private ImageButton[][] imgButtons;
    private ImageView[][] imgViews;
    private ImageButton restart;
    private ImageButton pause;
    private ImageButton returnGame;
    private ImageButton returnPage;
    private ImageView imagePause;
    private ImageView backGround;
    private ImageView imageDifficulty;

    private GameSceneActivity.ThreadSafe thead;
    private boolean gameMode;
    private boolean gamePause;
    private boolean gameOver;

    private MineZone mineZone;
    private Context context;


    private final Object lock = new Object();


    private Handler handler = new Handler(){
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case UPDATE_TEXT:
                    timer.setTypeface(Typeface.createFromAsset(getAssets(),"font/MFBanHei_Noncommercial.otf"));
                    timer.setText("时间：" + gameTime);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
            return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_main);
        context =getApplicationContext();


        Intent intent = getIntent();
        if (intent != null) {
            widthMine = intent.getIntExtra("Column",9);
            heightMine = intent.getIntExtra("Row",11);
            allMine = intent.getIntExtra("MineCount",10);
            iDifficulty = intent.getIntExtra("Difficulty",0);
        }
        else
        {
            widthMine = 9;
            heightMine = 11;
            allMine = 10;
            iDifficulty = 0;
        }



        imagePause = (ImageView)findViewById(R.id.imagePause);
        backGround = (ImageView)findViewById(R.id.backGround);
        imageDifficulty = (ImageView)findViewById(R.id.difficulty);

        restart = (ImageButton)findViewById(R.id.restart);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gameOver){
                    thead.interrupt();
                    createGame();
                }
            }
        });
        pause = (ImageButton)findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gameOver){
                    gamePause = true;
                    pauseGame();
                }
            }
        });
        returnGame = (ImageButton)findViewById(R.id.returnGame);
        returnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gamePause = false;
                resumeGame();
                resumeThread();
            }
        });
        returnPage = (ImageButton)findViewById(R.id.returnPage);
        returnPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iPage = new Intent(GameSceneActivity.this,MainActivity.class);
                setResult(0x717, iPage);
                finish();
            }
        });

        createGame();
    }

    private void createGame(){

        switch (MainActivity.icurrentTheme) {
            case 0:
                theme = GameSceneActivity.THEME.THEMEONE;
                break;
            case 1:
                theme = GameSceneActivity.THEME.THEMETWO;
                break;
            case 2:
                theme = GameSceneActivity.THEME.THEMETHREE;
                break;
            case 3:
                theme = GameSceneActivity.THEME.THEMEFOUR;
                break;
            default:
                theme = GameSceneActivity.THEME.THEMEONE;
                break;
        }
        initImage();
        backGround.setImageDrawable(getResources().getDrawable(imageRes[13]));

        switch(iDifficulty){
            case 0:
                imageDifficulty.setImageDrawable(getResources().getDrawable(R.drawable.primary));
                break;
            case 1:
                imageDifficulty.setImageDrawable(getResources().getDrawable(R.drawable.intermediate));
                break;
            case 2:
                imageDifficulty.setImageDrawable(getResources().getDrawable(R.drawable.senior));
                break;
            default:
                imageDifficulty.setImageDrawable(getResources().getDrawable(R.drawable.primary));
                break;
        }

        gameOver = false;
        leftoverMine = allMine;
        mineZone = new MineZone(widthMine,heightMine,allMine);
        array = mineZone.getMineZone();

        gameTime = 0;
        gameMode = true;
        gamePause = false;
        imgButtons = new ImageButton[heightMine][widthMine];
        imgViews = new ImageView[heightMine][widthMine];

        timer = (TextView)findViewById(R.id.timer);
        mineNum = (TextView)findViewById(R.id.mineNum);
        showLeftoverMine();
        refreshTimer();

        initChessBoard(heightMine,widthMine);


        final ImageButton mode = (ImageButton)findViewById(R.id.gameMode);
        mode.setBackgroundResource(R.drawable.iv_minemode);
        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameMode){
                    gameMode = false;
                    mode.setBackgroundResource(R.drawable.iv_flagmode);
                }
                else{
                    gameMode = true;
                    mode.setBackgroundResource(R.drawable.iv_minemode);
                }
            }
        });
    }

    private void initChessBoard(int widthNum,int heightNum){
        mineLayout = (GridLayout)findViewById(R.id.mineLayout);

        mineLayout.setRowCount(widthNum);
        mineLayout.setColumnCount(heightNum);

        isFlag = new boolean[heightMine][widthMine];
        for(int i=0;i<heightMine;i++){
            for(int j=0;j<widthMine;j++){
                isFlag[i][j] = false;
            }
        }

        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        int length;
        if((width - widthMine*2) / widthMine > (height - 200) / heightMine){
            length = (height - 200) / heightMine;
        }
        else{
            length = (width - widthMine*2) / widthMine;
        }
        for(int i=0;i<mineLayout.getRowCount();i++)
        {
            for(int j=0;j<mineLayout.getColumnCount();j++)
            {
                GridLayout.LayoutParams glParam = new GridLayout.LayoutParams(
                        GridLayout.spec(i),GridLayout.spec(j));
                glParam.width = length;
                glParam.height = length;
                glParam.setMargins(1,1,1,1);
                final ImageButton btn = new ImageButton(GameSceneActivity.this);
                btn.setBackgroundResource(imageRes[12]);
                btn.setScaleType(ImageView.ScaleType.FIT_XY);
                btn.setLayoutParams(glParam);
                imgButtons[i][j] = btn;
                final ImageView img = new ImageView(GameSceneActivity.this);
                img.setLayoutParams(glParam);
                img.setVisibility(View.GONE);
                imgViews[i][j] = img;
                final int hTemp = i;
                final int wTemp = j;
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!gamePause){
                            isOpen = mineZone.getOpenZone();
                            if(gameMode){
                                if(!isFlag[hTemp][wTemp] && !isOpen[hTemp][wTemp]){
                                    openMineZone(hTemp,wTemp);
                                }
                            }
                            else{
                                if(!isOpen[hTemp][wTemp])
                                {
                                    mineZone.MarkZone(hTemp,wTemp);
                                    if(isFlag[hTemp][wTemp]){
                                        isFlag[hTemp][wTemp] = false;
                                        btn.setBackgroundResource(imageRes[12]);
                                        leftoverMine++;
                                    }
                                    else{
                                        isFlag[hTemp][wTemp] = true;
                                        btn.setBackgroundResource(imageRes[11]);
                                        leftoverMine--;
                                    }
                                    showLeftoverMine();
                                }
                            }
                        }
                    }
                });

                mineLayout.addView(btn);
                mineLayout.addView(img);
            }
        }
    }

    private void openMineZone(int height,int width){
        mineZone.OpenZone(height,width);
        refreshZone();
        if(-1 == array[height][width]){
            thead.interrupt();
            gamePause = true;
            for(int i=0;i<heightMine;i++){
                for(int j=0;j<widthMine;j++){
                    if(isFlag[i][j] && -1 != array[i][j]){
                        imgViews[i][j].setVisibility(View.VISIBLE);
                        imgViews[i][j].setImageDrawable(getResources().getDrawable(imageRes[14]));
                        imgButtons[i][j].setVisibility(View.GONE);
                    }
                    else if(!isOpen[i][j]){
                        imgViews[i][j].setVisibility(View.VISIBLE);
                        imgViews[i][j].setImageDrawable(getResources().getDrawable(getImage(i,j)));
                        imgButtons[i][j].setVisibility(View.GONE);
                    }
                }
            }
            imgViews[height][width].setImageDrawable(getResources().getDrawable(imageRes[10]));
            //game over
            gameOver = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        sleep(3000);
                        Intent intent = new Intent(GameSceneActivity.this,Success_Fail_Scene.class);
                        intent.putExtra("GameTime",-1);
                        startActivityForResult(intent,CODE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
        else{
            int closeNum = 0;
            for(int i=0;i<heightMine;i++){
                for(int j=0;j<widthMine;j++){
                    if(!isOpen[i][j]){
                        closeNum++;
                    }
                }
            }
            if(closeNum == allMine){
                thead.interrupt();
                gamePause = true;
                //win
                gameOver = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sleep(500);
                            String addr = getScreenHot();
                            Intent intent = new Intent(GameSceneActivity.this,Success_Fail_Scene.class);
                            intent.putExtra("GameTime",gameTime);
                            intent.putExtra("address",addr);
                            intent.putExtra("difficulty",iDifficulty);
                            startActivityForResult(intent,CODE);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODE && resultCode == CODE)
        {
            boolean returnPage = data.getBooleanExtra("ReturnPage",true);
            if(returnPage){
                Intent iPage = new Intent(GameSceneActivity.this,MainActivity.class);
                setResult(0x717, iPage);
                finish();
            }
            else{
                thead.interrupt();
                createGame();
            }
        }
    }

    private void refreshZone(){
        for(int i=0; i<heightMine; i++){
            for(int j=0; j<widthMine; j++){
                if(isOpen[i][j] && !isFlag[i][j])
                {
                    imgViews[i][j].setVisibility(View.VISIBLE);
                    imgViews[i][j].setImageDrawable(getResources().getDrawable(getImage(i,j)));
                    imgButtons[i][j].setVisibility(View.GONE);
                }
            }
        }
    }

    private int getImage(int height,int width){
        int imgName = imageRes[0];
        switch(array[height][width]){
            case -1:
                imgName = imageRes[9];
                break;
            case 0:
                imgName = imageRes[0];
                break;
            case 1:
                imgName = imageRes[1];
                break;
            case 2:
                imgName = imageRes[2];
                break;
            case 3:
                imgName = imageRes[3];
                break;
            case 4:
                imgName = imageRes[4];
                break;
            case 5:
                imgName = imageRes[5];
                break;
            case 6:
                imgName = imageRes[6];
                break;
            case 7:
                imgName = imageRes[7];
                break;
            default:
                imgName = imageRes[8];
                break;
        }
        return imgName;
    }

    public void refreshTimer(){
        thead = new ThreadSafe();
        thead.start();
    }

    public void showLeftoverMine(){
        mineNum.setTypeface(Typeface.createFromAsset(getAssets(),"font/MFBanHei_Noncommercial-Regular.otf"));
        mineNum.setText("剩余雷数："+leftoverMine);
    }

    public class ThreadSafe extends Thread {
        @Override
        public void run() {
            while (!isInterrupted()){
                while(gamePause){
                    Pause();
                }

                Message message = new Message();
                message.what = UPDATE_TEXT;
                handler.sendMessage(message);
                try{
                    sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                    break;
                }
                gameTime++;
            }

        }
    }

    public void Pause() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void resumeThread() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public void pauseGame(){
        imagePause.setVisibility(View.VISIBLE);
        returnGame.setVisibility(View.VISIBLE);
        returnPage.setVisibility(View.VISIBLE);
    }

    public void resumeGame(){
        imagePause.setVisibility(View.GONE);
        returnGame.setVisibility(View.GONE);
        returnPage.setVisibility(View.GONE);
    }

    public enum THEME{
        THEMEONE,
        THEMETWO,
        THEMETHREE,
        THEMEFOUR
    }

    public void initImage(){
        imageRes = new int[15];
        switch(theme){
            case THEMEONE:
                //0~8
                imageRes[0] = R.drawable.empty_0;
                imageRes[1] = R.drawable.n1_0;
                imageRes[2] = R.drawable.n2_0;
                imageRes[3] = R.drawable.n3_0;
                imageRes[4] = R.drawable.n4_0;
                imageRes[5] = R.drawable.n5_0;
                imageRes[6] = R.drawable.n6_0;
                imageRes[7] = R.drawable.n7_0;
                imageRes[8] = R.drawable.n8_0;
                //地雷
                imageRes[9] = R.drawable.gmine_0;
                imageRes[10] = R.drawable.rmine_0;
                //旗子
                imageRes[11] = R.drawable.flag_0;
                //区块
                imageRes[12] = R.drawable.button_0;
                //背景
                imageRes[13] = R.drawable.background_0;
                //错误旗子
                imageRes[14] = R.drawable.flagerr_0;
                break;
            case THEMETWO:
                //0~8
                imageRes[0] = R.drawable.empty_1;
                imageRes[1] = R.drawable.n1_1;
                imageRes[2] = R.drawable.n2_1;
                imageRes[3] = R.drawable.n3_1;
                imageRes[4] = R.drawable.n4_1;
                imageRes[5] = R.drawable.n5_1;
                imageRes[6] = R.drawable.n6_1;
                imageRes[7] = R.drawable.n7_1;
                imageRes[8] = R.drawable.n8_1;
                //地雷
                imageRes[9] = R.drawable.gmine_1;
                imageRes[10] = R.drawable.rmine_1;
                //旗子
                imageRes[11] = R.drawable.flag_1;
                //区块
                imageRes[12] = R.drawable.button_1;
                //背景
                imageRes[13] = R.drawable.background_1;
                //错误旗子
                imageRes[14] = R.drawable.flagerr_1;
                break;
            case THEMETHREE:
                //0~8
                imageRes[0] = R.drawable.empty_2;
                imageRes[1] = R.drawable.n1_2;
                imageRes[2] = R.drawable.n2_2;
                imageRes[3] = R.drawable.n3_2;
                imageRes[4] = R.drawable.n4_2;
                imageRes[5] = R.drawable.n5_2;
                imageRes[6] = R.drawable.n6_2;
                imageRes[7] = R.drawable.n7_2;
                imageRes[8] = R.drawable.n8_2;
                //地雷
                imageRes[9] = R.drawable.gmine_2;
                imageRes[10] = R.drawable.rmine_2;
                //旗子
                imageRes[11] = R.drawable.flag_2;
                //区块
                imageRes[12] = R.drawable.button_2;
                //背景
                imageRes[13] = R.drawable.background_2;
                //错误旗子
                imageRes[14] = R.drawable.flagerr_2;
                break;
            case THEMEFOUR:
                //0~8
                imageRes[0] = R.drawable.empty_3;
                imageRes[1] = R.drawable.n1_3;
                imageRes[2] = R.drawable.n2_3;
                imageRes[3] = R.drawable.n3_3;
                imageRes[4] = R.drawable.n4_3;
                imageRes[5] = R.drawable.n5_3;
                imageRes[6] = R.drawable.n6_3;
                imageRes[7] = R.drawable.n7_3;
                imageRes[8] = R.drawable.n8_3;
                //地雷
                imageRes[9] = R.drawable.gmine_3;
                imageRes[10] = R.drawable.rmine_3;
                //旗子
                imageRes[11] = R.drawable.flag_3;
                //区块
                imageRes[12] = R.drawable.button_3;
                //背景
                imageRes[13] = R.drawable.background_3;
                //错误旗子
                imageRes[14] = R.drawable.flagerr_3;
                break;
            default:
                break;
        }
    }

    private String getScreenHot()
    {
        //获取数据库长
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String fname = context.getFilesDir().getPath()+"/"+ format.format(date) +".jpg";
        View view = GameSceneActivity.this.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        if(bitmap != null) {
            System.out.println("bitmap got!");
            try{
                File foder = new File(context.getFilesDir().getPath()+"/");
                if (!foder.exists()) {
                    foder.mkdirs();
                }
                FileOutputStream out = new FileOutputStream(fname);
                bitmap.compress(Bitmap.CompressFormat.PNG,100, out);
                System.out.println("file " + fname + "output done.");
                //Toast.makeText(GameSceneActivity.this,"截图 " + fname + " 已储存.",Toast.LENGTH_LONG).show();
            }catch(Exception e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("bitmap is NULL!");
        }
        return fname;
    }

}
