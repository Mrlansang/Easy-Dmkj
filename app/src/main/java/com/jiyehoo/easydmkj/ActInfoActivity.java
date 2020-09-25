package com.jiyehoo.easydmkj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;


import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.jiyehoo.easydmkj.json.ActInfo;
import com.jiyehoo.easydmkj.json.SpecialList;
import com.jiyehoo.easydmkj.json.TribeVo;

import java.util.List;


public class ActInfoActivity extends AppCompatActivity {


    public static final String ACT_NAME = "act_name";
    public static final String ACT_IMG = "act_img";
    public static final String JSON = "act_json";

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_info);

        Intent intent = getIntent();
        String actName = intent.getStringExtra(ACT_NAME);
        String actImgUrl = intent.getStringExtra(ACT_IMG);
        String jsonString = intent.getStringExtra(JSON);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        ImageView imageView = findViewById(R.id.iv_act_info_img);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout.setTitle(actName);


        Glide.with(this).load(actImgUrl).into(imageView);

        //活动详情
        ActInfo actInfo = new Gson().fromJson(jsonString, ActInfo.class);

        //活动名
        String activityName = actInfo.getActivityName();
        //介绍
        String content = null;
        try {
            content = actInfo.getContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView mTvContent = findViewById(R.id.tv_act_info_content);
        mTvContent.setText(content);

        //人数上限
        String joinmaxnum = actInfo.getJoinmaxnum();
        //学分
        String accountTypeName = "学分";
        String unitcount = "没有分！别参加了吧...o(TヘTo)";
        String unitcountType = "没有类型";
        try {
            List<SpecialList> specialListList = actInfo.getSpecialList();
            accountTypeName = specialListList.get(0).getAccountTypeName();
            unitcount = specialListList.get(0).getUnitcount();
            unitcountType = specialListList.get(0).getName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView mTvCount = findViewById(R.id.tv_act_info_count);
        mTvCount.setText("学分类型：" + unitcountType + "\n\n" +
                accountTypeName + "：" + unitcount + " / " + joinmaxnum);

        //活动时间
        String joindate = actInfo.getJoindate();//报名时间
        String startdate = actInfo.getStartdate();
        TextView mTvTime = findViewById(R.id.tv_act_info_time);
        mTvTime.setText("报名时间：" + joindate + "\n\n" +
                "活动时间：" + startdate);
        //参加须知
        String joinWayDesc = "发布者没有添加须知，你可以乱来...";
        try {
            joinWayDesc = actInfo.getJoinWayDesc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView mTvJoinWayDesc = findViewById(R.id.tv_act_info_desc);
        mTvJoinWayDesc.setText(joinWayDesc);
        //举办部门
        String levelText = "活动级别未知";//活动级别
        String tribeVoName = "举办部门未知";
        String typeDesc = "举办部门没有描述";
        try {
            TribeVo tribeVo = actInfo.getTribeVo();
            tribeVoName = tribeVo.getName();
            typeDesc = tribeVo.getTypeDesc();
            levelText = actInfo.getLevelText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView mTvTribeVo = findViewById(R.id.tv_act_info_tribeVo);
        mTvTribeVo.setText("主办方：" + tribeVoName + "\n\n" +
                "举办部门介绍：" + typeDesc + "\n\n" +
                "活动级别：" + levelText);
        //地点、导航
        String addressLatitude = "0";
        String addressLongitude = "0";
        String address = "未知地点";
        TextView mTvAddress = findViewById(R.id.tv_act_info_address);
        try {
            addressLatitude = actInfo.getAddressLatitude();
            addressLongitude = actInfo.getAddressLongitude();
            address = actInfo.getAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTvAddress.setText("地址：" + address + "\n" +
                "纬度：" + addressLatitude + "\n" +
                "经度：" + addressLongitude);
        String finalAddressLatitude = addressLatitude;
        String finalAddressLongitude = addressLongitude;
//        findViewById(R.id.cv_navi).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });


        //悬浮按钮
        FloatingActionButton floatingActionButton = findViewById(R.id.fb_copy);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                ClipData clipData = ClipData.newPlainText("Aid", String.valueOf(actInfo.getActivityId()));
//                clipboardManager.setPrimaryClip(clipData);
//                Toast.makeText(ActInfoActivity.this, "活动Aid复制到剪贴板", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(ActInfoActivity.this, NaviActivity.class);
                intent1.putExtra("latitude", finalAddressLatitude);
                intent1.putExtra("longitude", finalAddressLongitude);
                startActivity(intent1);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

}