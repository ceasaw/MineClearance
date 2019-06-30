package com.hellotheworld.mineclearance.Common.Rank.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hellotheworld.mineclearance.Common.Rank.Bean.RankGradeEnum;
import com.hellotheworld.mineclearance.Common.Rank.Dao.RankListDao;
import com.hellotheworld.mineclearance.R;
import com.hellotheworld.mineclearance.Common.Rank.Bean.RankBean;
import com.hellotheworld.mineclearance.Common.Rank.Adapter.RankViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class RankActivity extends Activity implements View.OnClickListener {

    private ViewPager vViewPager;
    private ArrayList<View> aPageView;
    private RelativeLayout tBasicLayout;
    private RelativeLayout tIntermediateLayout;
    private RelativeLayout tAdvancedLayout;
    private ImageView iRankBack;

    private List<RankView> rankViewList = new ArrayList<RankView>();

    public static Integer REQUEST_CODE_OPEN_SCREENSHOT = 135;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        vViewPager = findViewById(R.id.viewPager);
        //查找并实例化布局文件用LayoutInflater.inflate
        LayoutInflater inflater = getLayoutInflater();
        rankViewList.add(new RankView(RankGradeEnum.BASIC, this));
        rankViewList.add(new RankView(RankGradeEnum.INTERMEDIATE, this));
        rankViewList.add(new RankView(RankGradeEnum.ADVANCED, this));


        tBasicLayout = findViewById(R.id.basicLayout);
        tIntermediateLayout = findViewById(R.id.intermediateLayout);
        tAdvancedLayout = findViewById(R.id.advancedLayout);
        iRankBack = findViewById(R.id.rank_back);

        tBasicLayout.setOnClickListener(this);
        tIntermediateLayout.setOnClickListener(this);
        tAdvancedLayout.setOnClickListener(this);
        iRankBack.setOnClickListener(this);

        aPageView = new ArrayList<View>();
        //添加想要切换的界面
        aPageView.add(rankViewList.get(0).getView());
        aPageView.add(rankViewList.get(1).getView());
        aPageView.add(rankViewList.get(2).getView());
        //数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter() {

            @Override
            //获取当前窗体界面数
            public int getCount() {
                // TODO Auto-generated method stub
                return aPageView.size();
            }

            @Override
            //判断是否由对象生成界面
            public boolean isViewFromObject(@NonNull View arg0, @NonNull Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            //使从ViewGroup中移出当前View
            public void destroyItem(@NonNull View arg0, int arg1, @NonNull Object arg2) {
                ((ViewPager) arg0).removeView(aPageView.get(arg1));
            }

            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            @NonNull
            public Object instantiateItem(@NonNull View arg0, int arg1) {
                ((ViewPager) arg0).addView(aPageView.get(arg1));
                return aPageView.get(arg1);
            }
        };
        //绑定适配器
        vViewPager.setAdapter(mPagerAdapter);
        //设置viewPager的初始界面为第一个界面
        vViewPager.setCurrentItem(0);
        //添加切换界面的监听器
        vViewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        //设置页卡之间的间距
        vViewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.ui_10_dip));

        tBasicLayout.setBackgroundColor(getResources().getColor(R.color.rank_selected));
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {

            //把所有选择项的颜色还原，再把选择项变为深色
            tBasicLayout.setBackgroundColor(getResources().getColor(R.color.rank_not_selected));
            tIntermediateLayout.setBackgroundColor(getResources().getColor(R.color.rank_not_selected));
            tAdvancedLayout.setBackgroundColor(getResources().getColor(R.color.rank_not_selected));

            switch (arg0) {
                case 0:
                    Log.i("", "onPageSelected: 0");
                    tBasicLayout.setBackgroundColor(getResources().getColor(R.color.rank_selected));
                    break;
                case 1:
                    Log.i("", "onPageSelected: 1");
                    tIntermediateLayout.setBackgroundColor(getResources().getColor(R.color.rank_selected));
                    break;
                case 2:
                    Log.i("", "onPageSelected: 2");
                    tAdvancedLayout.setBackgroundColor(getResources().getColor(R.color.rank_selected));
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.basicLayout:
                //点击"basic"时切换到第一页
                vViewPager.setCurrentItem(0);
                break;
            case R.id.intermediateLayout:
                //点击"intermediate"时切换的第二页
                vViewPager.setCurrentItem(1);
                break;
            case R.id.advancedLayout:
                //点击"advanced"时切换的第三页
                vViewPager.setCurrentItem(2);
                break;
            case R.id.rank_back:
                finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RankActivity.REQUEST_CODE_OPEN_SCREENSHOT && resultCode == ScreenShotActivity.IMAGE_NOT_FOUND){
            makeToast("截图不存在或者保存位置被改动！").show();
        }else if(requestCode == RankActivity.REQUEST_CODE_OPEN_SCREENSHOT && resultCode == ScreenShotActivity.IMAGE_TURN_BACK_SUCCESS){

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private Toast tToast;
    private Toast makeToast(String text){

        if(tToast!=null){
            tToast.cancel();
            tToast = null;
        }

        Display display = getWindowManager().getDefaultDisplay();
        // 获取屏幕高度
        int height = display.getHeight();
        tToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        // 这里给了一个1/5屏幕高度的y轴偏移量
        tToast.setGravity(Gravity.BOTTOM, 0, height / 5);
        return tToast;
    }

    class RankView {
        private ListView listView;
        private RankViewAdapter adapter;
        private ListView view;
        List<RankBean> list = new ArrayList<RankBean>();

        public RankView() {

        }

        public RankView(RankGradeEnum grade, final Context mContext) {
            //从数据库获取RankBeanList
//            if (grade.equals(RankGradeEnum.ADVANCED)) {
//                list.add(new RankBean("匿名", 120, "www.advanced.com"));
//                list.add(new RankBean("tom", 156, "www.advanced.com"));
//                list.add(new RankBean("peter", 178, "www.advanced.com"));
//                list.add(new RankBean("jack", 222, "www.advanced.com"));
//                list.add(new RankBean("匿名", 365, "www.advanced.com"));
//                list.add(new RankBean("匿名", 400, "www.advanced.com"));
//                list.add(new RankBean("peter", 999, "www.advanced.com"));
//            } else if (grade.equals(RankGradeEnum.INTERMEDIATE)) {
//                list.add(new RankBean("peter", 12, null));
//            } else if (grade.equals(RankGradeEnum.BASIC)) {
//                list.add(new RankBean("peter", 1, "/mnt/shell/emulated/0/Images/Screenshot.png"));
//            }

            list.clear();
            list.addAll((new RankListDao(mContext).getRankListByLevel(grade)));

            view = getLayoutInflater().inflate(R.layout.rank_list, null).findViewById(R.id.rank_list);
            //构造adapter
            adapter = new RankViewAdapter(list, mContext);
            view.setAdapter(adapter);

            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String imageUrl = list.get(position).getScreenShot();
                    if(imageUrl == null||imageUrl.isEmpty()){
                        makeToast("未保存截图").show();
                    }else{
                        Intent intent = new Intent(RankActivity.this, ScreenShotActivity.class);
                        intent.putExtra("url",imageUrl);
                        //intent.setData(Uri.parse(imageUrl));
                        startActivityForResult(intent,RankActivity.REQUEST_CODE_OPEN_SCREENSHOT);
                    }
                }
            });
        }

        public View getView() {
            return view;
        }


    }

}
