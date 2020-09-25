package com.jiyehoo.easydmkj;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.jiyehoo.easydmkj.json.ActInfo;

import org.json.JSONException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.jiyehoo.easydmkj.R.id.cancel_button;
import static com.jiyehoo.easydmkj.R.id.ll_aid_and_alarm_layout;

public class MainActivity extends AppCompatActivity {
    private Button mBtAidJoin;
    private EditText mEtAid;
    private static final String dateFormat = "yyyy.MM.dd HH:mm";//"yyyy/MM/dd HH:mm"
    //private static TextView tvLog;
    private static StringBuilder stringBuilder = new StringBuilder();
    private String token;
    private String name;
    private Integer uid;
    private String userAcc;
    private String userPwd;
    private Map<String, String> aids = new HashMap<>();
    private Map<String, String> time_aid = new HashMap<>();
    private Map<String, String> aid_time = new HashMap<>();

    private Map<String, String> aid_img = new HashMap<>();
    //private Map<String, String> aid_content = new HashMap<>();
    private Map<String, String> aid_json = new HashMap<>();

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    private TextView mTvTitleUsername;
    private TextView mTvTitleUserPhone;
    private SwipeRefreshLayout swipeRefreshLayout;

    //cardView
    private List<ActCardView> actCardViewList = new ArrayList<>();
    private CardAdapter adapter;


    //定时
    private Button mBtAlarm;
    private EditText mEtHour, mEtMinute, mEtSecond;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private IntentFilter intentFilter;
    private AlarmReceiver alarmReceiver;
    final boolean[] flagAlarm = {false};//当前是否设置了定时器
    final boolean[] flagAlarmShow = {false}; //是否显示定时器
    //String inputAidForAlarm = null;


    //定时接收器
    class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //时间到了执行
            new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    String inputStrAid = mEtAid.getText().toString().trim();
                    if (enter(inputStrAid)) {
                        //card关闭1
                        //updataLog("报名成功");
                        Log.d("alarm", "报名成功");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                notificationManager.cancelAll();
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("定时器信息")
                                        .setMessage("报名成功!<(*￣▽￣*)/")
                                        .setPositiveButton("确定", null)
                                        .show();
                            }
                        });
                    } else {
                        //card关闭1
                        //updataLog("报名失败（enter返回）");
                        Log.d("alarm", "报名失败");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                notificationManager.cancelAll();
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("定时器信息")
                                        .setMessage("报名失败!(*￣︿￣)")
                                        .setPositiveButton("确定", null)
                                        .show();
                                //按钮变为定时
                                mBtAlarm.setText("设置定时");
                                flagAlarm[0] = false;
                                //设置为能输入
                                mEtSecond.setEnabled(true);
                                mEtSecond.setFocusable(true);
                                mEtSecond.setFocusableInTouchMode(true);
                                mEtMinute.setEnabled(true);
                                mEtMinute.setFocusable(true);
                                mEtMinute.setFocusableInTouchMode(true);
                                mEtHour.setEnabled(true);
                                mEtHour.setFocusable(true);
                                mEtHour.setFocusableInTouchMode(true);
                                mEtAid.setEnabled(true);
                                mEtAid.setFocusable(true);
                                mEtAid.setFocusableInTouchMode(true);
                            }
                        });
                    }
                }
            }).start();
        }
    }
    //定时接收器

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.tb_toolbar);
        setSupportActionBar(mToolbar);

        //账号密码
        String str = getIntent().getStringExtra("acc");
        String pwd = getIntent().getStringExtra("password");
        userAcc = str;
        userPwd = pwd;

        //card关闭1
        //tvLog = findViewById(R.id.tv_log);
        mBtAidJoin = findViewById(R.id.bt_aid_join);
        mEtAid = findViewById(R.id.et_aid);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        View headerView = mNavigationView.getHeaderView(0);
        mTvTitleUsername = headerView.findViewById(R.id.tv_drawer_user_name);
        mTvTitleUserPhone = headerView.findViewById(R.id.tv_drawer_user_phone);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);

        //闹钟
        mEtHour = findViewById(R.id.et_hour);
        mEtMinute = findViewById(R.id.et_minute);
        mEtSecond = findViewById(R.id.et_second);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent("set Alarm");
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        intentFilter = new IntentFilter();
        intentFilter.addAction("set Alarm");
        alarmReceiver = new AlarmReceiver();
        registerReceiver(alarmReceiver, intentFilter);

        mBtAlarm = findViewById(R.id.bt_alarm);
        //发送通知
        NotificationChannel notificationChannel = null;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel("001", "channel_name", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        mBtAlarm.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                if (!flagAlarm[0]) {
                    //避免空时间定时
                    if (TextUtils.isEmpty(mEtHour.getText().toString().trim()) ||
                            TextUtils.isEmpty(mEtMinute.getText().toString().trim()) ||
                            TextUtils.isEmpty(mEtSecond.getText().toString().trim())) {
                        //Toast.makeText(MainActivity.this, "不能设置空的定时器", Toast.LENGTH_SHORT).show();
                        //return;
                        new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                mEtHour.setText(String.valueOf(hour).trim());
                                mEtMinute.setText(String.valueOf(minute).trim());
                                mEtSecond.setText("0".trim());
                            }
                        }, 0, 0, true).show();
                        return;
                    }
                    int mHour = Integer.parseInt(mEtHour.getText().toString().trim());
                    int mMinute = Integer.parseInt(mEtMinute.getText().toString().trim());
                    int mSeconds = Integer.parseInt(mEtSecond.getText().toString().trim());
                    //判断map里有没有该aid
                    if (!aid_time.containsKey(mEtAid.getText().toString())){
                        Toast.makeText(MainActivity.this, "不存在该Aid", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //检查输入时间格式
                    if (mHour > 23 || mMinute > 59 || mSeconds > 59) {
                        Toast.makeText(MainActivity.this, "时间格式错误!", Toast.LENGTH_SHORT).show();
                        mEtHour.setText("");
                        mEtMinute.setText("");
                        mEtSecond.setText("");
                        return;
                    }
                    //从输入框解析时间
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, mHour);
                    calendar.set(Calendar.MINUTE, mMinute);
                    calendar.set(Calendar.SECOND, mSeconds);

                    //设置为不能点击
                    mEtSecond.setEnabled(false);
                    mEtSecond.setFocusable(false);
                    mEtSecond.setFocusableInTouchMode(false);
                    mEtMinute.setEnabled(false);
                    mEtMinute.setFocusable(false);
                    mEtMinute.setFocusableInTouchMode(false);
                    mEtHour.setEnabled(false);
                    mEtHour.setFocusable(false);
                    mEtHour.setFocusableInTouchMode(false);
                    mEtAid.setEnabled(false);
                    mEtAid.setFocusable(false);
                    mEtAid.setFocusableInTouchMode(false);
                    //按钮变为取消
                    mBtAlarm.setText("取消定时");
                    flagAlarm[0] = true;
                    //执行定时器
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    //显示提示
                    Snackbar snackbar = Snackbar.make(view,
                            "已经设置了定时器",
                            Snackbar.LENGTH_LONG)
                            .setAction("确定", null);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(Color.DKGRAY);
                    snackbar.show();

                    String mHourString = String.valueOf(mHour);
                    String mMinuteString = String.valueOf(mMinute);
                    String mSecondString = String.valueOf(mSeconds);
                    if (mHour < 10) {mHourString = "0" + mHourString;}
                    if (mMinute < 10) {mMinuteString = "0" + mMinuteString;}
                    if (mSeconds < 10) {mSecondString = "0" + mSecondString;}
                    String notTime = mHourString + ":" + mMinuteString + ":" + mSecondString;
                    //Log.d("time", time);
                    Notification notification = new NotificationCompat.Builder(MainActivity.this, "001")
                            .setContentTitle("定时报名")
//                            .setContentText("活动：" + aids.get(mEtAid.getText().toString()) + "\n" +
//                                    "时间:  " + notTime)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(notTime + "\n" +
                                    aids.get(mEtAid.getText().toString())))
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setAutoCancel(false)
                            .setOngoing(true)
                            .build();
                    notificationManager.notify(1, notification);
                } else {
                    //取消定时器
                    alarmManager.cancel(pendingIntent);
                    //按钮变为定时
                    mBtAlarm.setText("设置定时");
                    flagAlarm[0] = false;
                    //设置为能输入
                    mEtSecond.setEnabled(true);
                    mEtSecond.setFocusable(true);
                    mEtSecond.setFocusableInTouchMode(true);
                    mEtMinute.setEnabled(true);
                    mEtMinute.setFocusable(true);
                    mEtMinute.setFocusableInTouchMode(true);
                    mEtHour.setEnabled(true);
                    mEtHour.setFocusable(true);
                    mEtHour.setFocusableInTouchMode(true);
                    mEtAid.setEnabled(true);
                    mEtAid.setFocusable(true);
                    mEtAid.setFocusableInTouchMode(true);
                    //显示提示
                    Snackbar snackbar = Snackbar.make(view,
                            "取消了定时器",
                            Snackbar.LENGTH_LONG)
                            .setAction("确定", null);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(Color.DKGRAY);
                    snackbar.show();

                    //取消通知
                    notificationManager.cancelAll();
                }

            }
        });
        //闹钟

        //密码错误退出
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                if (!login(str, pwd)) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("密码错误")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                    });
                }
            }
        }).start();

        //进入直接拉取列表
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("加载");
        progressDialog.setMessage("正在获取活动信息...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                if (login(userAcc, userPwd)) {
                    get_aid();
                    chiken();
                    schedule(userAcc, userPwd);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTvTitleUsername.setText(name);
                            mTvTitleUserPhone.setText(userAcc);
                            //关闭加载框
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //cardView
                                    showCardView();
                                }
                            });
                        }
                    });

                }
            }
        }).start();




        //悬浮按钮
        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout mLlAlarm = findViewById(ll_aid_and_alarm_layout);
                //如果当前Alarm没显示
                if (!flagAlarmShow[0]) {
                    mLlAlarm.setVisibility(View.VISIBLE);
                    flagAlarmShow[0] = true;
                }
                //Toast.makeText(MainActivity.this, "定时任务，待开发", Toast.LENGTH_SHORT).show();
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        mEtHour.setText(String.valueOf(hour));
                        mEtMinute.setText(String.valueOf(minute));
                        mEtSecond.setText("0");
                    }
                }, 0, 0, true);
                //拾取取消
                timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        mLlAlarm.setVisibility(View.GONE);
                        flagAlarmShow[0] = false;
                    }
                });
                //显示
                timePickerDialog.show();

//                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
//                progressDialog.setTitle("加载");
//                progressDialog.setMessage("正在获取活动信息...");
//                progressDialog.setCancelable(false);
//                progressDialog.show();
//                new Thread(new Runnable() {
//                    @RequiresApi(api = Build.VERSION_CODES.O)
//                    @Override
//                    public void run() {
//                        stringBuilder = new StringBuilder();
//                        if (login(userAcc, userPwd)) {
//                            get_aid();
//                            chiken();
//                            schedule(userAcc, userPwd);
//                            progressDialog.dismiss();
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    showCardView();
//                                }
//                            });
//
//                        }
//                    }
//                }).start();
            }
        });

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("加载");
                progressDialog.setMessage("正在获取活动信息...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        stringBuilder = new StringBuilder();
                        if (login(userAcc, userPwd)) {
                            get_aid();
                            chiken();
                            schedule(userAcc, userPwd);
                            progressDialog.dismiss();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //cardView
                                    showCardView();
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        //点击aid报名
        mBtAidJoin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                //aid存入String
                String inputAid = mEtAid.getText().toString();
                //列表为空不能使用
                if (aids.isEmpty() || aid_time.isEmpty() || time_aid.isEmpty()) {
                    Toast.makeText(MainActivity.this, "活动列表为空,不可报名", Toast.LENGTH_SHORT).show();
                    return;
                }
                //判断map里是否有这个aid
                if (!aid_time.containsKey(inputAid)){
                    Toast.makeText(MainActivity.this, "不存在该Aid", Toast.LENGTH_SHORT).show();
                    joinMinTime();
                    return;
                }
                //card关闭2
                //updataLog("--------------------");
                //updataLog("指定报名的活动为:" + aids.get(inputAid));
                new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        if (enter(inputAid)) {
                            //card关闭1
                            //updataLog("报名成功");
                            Log.d("Aid Button", "报名成功");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "报名成功", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            //card关闭1
                            //updataLog("报名失败（enter返回）");
                            Log.d("Aid Button", "报名失败");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "报名失败", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        //侧栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.category);
        }
        mNavigationView.setCheckedItem(R.id.nav_list);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.nav_vip:
                        Toast.makeText(MainActivity.this, "您为内测用户，已解锁所有功能！", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_list:
                        Toast.makeText(MainActivity.this, "您已处于活动列表", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_safe:
                        Toast.makeText(MainActivity.this, "安全功能待开发", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_setting:
                        Toast.makeText(MainActivity.this, "设置功能待开发", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_update:
                        Toast.makeText(MainActivity.this, "已经是最新版本: V" + getString(R.string.app_version), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_feedback:
                        Uri uri = Uri.parse("http://jiyehoo.com:9200");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        break;
                    case R.id.nav_quit:
                        Toast.makeText(MainActivity.this, "停止并退出了程序", Toast.LENGTH_SHORT).show();
                        if (alarmManager != null && pendingIntent != null) {
                            alarmManager.cancel(pendingIntent);
                        }
                        finish();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    //加载menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
    //menu点击
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        LinearLayout mLlAlarm = findViewById(ll_aid_and_alarm_layout);
        switch (item.getItemId()) {
            //侧栏展开按钮
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.menu_quick:
                //如果当前Alarm没显示
                if (!flagAlarmShow[0]) {
                    mLlAlarm.setVisibility(View.VISIBLE);
                    flagAlarmShow[0] = true;
                }
                joinMinTime();
                break;
            case R.id.menu_task:
                if (!flagAlarmShow[0]) {
                    mLlAlarm.setVisibility(View.VISIBLE);
                    flagAlarmShow[0] = true;
                } else {
                    mLlAlarm.setVisibility(View.GONE);
                    flagAlarmShow[0] = false;
                }
                break;
//            case R.id.menu_feedback:
//                Uri uri = Uri.parse("http://jiyehoo.com:9200");
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
//                //Toast.makeText(MainActivity.this, "内测阶段请在群内反馈", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.menu_clear:
                if (flagAlarm[0]) {
                    Toast.makeText(this, "当前有定时活动，无法清空", Toast.LENGTH_SHORT).show();
                } else {
                    mEtAid.setText("");
                    mEtHour.setText("");
                    mEtMinute.setText("");
                    mEtSecond.setText("");
                    Toast.makeText(MainActivity.this, "清空了载入的信息", Toast.LENGTH_SHORT).show();
                }
                break;
//            case R.id.menu_stop:
//                Toast.makeText(MainActivity.this, "停止并退出了程序", Toast.LENGTH_SHORT).show();
//                if (alarmManager != null && pendingIntent != null) {
//                    alarmManager.cancel(pendingIntent);
//                }
//                finish();
//                break;
            default:
                break;
        }
        return true;
    }

    //加载card
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showCardView() {
        //cardView
        //增加到list用于显示card
        actCardViewList.clear();
        aids.forEach((aid, name) -> {
            Log.d("card", name + aid + aid_time.get(aid));
            //Log.d("json放入卡片", aid_json.get(aid));
            actCardViewList.add(new ActCardView(name, aid, aid_time.get(aid), aid_img.get(aid), aid_json.get(aid)));
        });

        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CardAdapter(actCardViewList);
        recyclerView.setAdapter(adapter);
    }


    // 登录
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean login(String user, String passwd) {
        String acc = user;
        String pwd = passwd;
        // 修复bug防止因为token过期
        if (test_token()) {
            //updataLog("token登录成功");
            Log.d("login", "token登录成功");
            return true;
        } else if (get_token(acc, pwd)) {
            //updataLog("密码登录成功");
            Log.d("login", "密码登录成功");
            return true;
        } else {
            Log.d("login", "token过期");
            //updataLog("token过期请检查账号密码");
            return false;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean test_token() {
        if (Objects.isNull(this.token) || Objects.isNull(uid)) {
            return false;
        }
        try {
            JSONObject jsonObject = HttpClient.get_ids(token, Integer.valueOf(uid));
            if (Objects.nonNull(jsonObject)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            //card关闭1
            //updataLog("异常");
            Log.e("test_token", "异常");
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean get_token(String acc, String pwd) {
        JSONObject token_pho = HttpClient.get_token_pho(acc, pwd);
        if (Objects.nonNull(token_pho)) {
            this.token = token_pho.getString("token");
            this.name = token_pho.getString("name");
            this.uid = token_pho.getInteger("uid");
            return true;
        }
        return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void get_aid() {
        if (this.token == null || this.uid == null) {
            return;
        }
        JSONObject object = HttpClient.get_ids(this.token, this.uid);
       /* this.names = (List<String>) JSONPath.eval(object.get("list"), "$.name");
        this.ids =  (List<String>) JSONPath.eval(object.get("list"), "$.aid");*/
        /*   List<String> statuses =  (List<String>) JSONPath.eval(object.get("list"), "$.status");*/
        Log.d("####get_aid_JSON####", object.toString());
        JSONArray list = (JSONArray) object.get("list");
        Log.d("####get_aid_list####", list.toString());
        list.forEach(t -> {
            JSONObject t1 = (JSONObject) t;
            if (t1.getInteger("status") == 2 && !aids.containsKey(t1.getString("aid"))) {
                aids.put(t1.getString("aid"), t1.getString("name"));
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void chiken() {
        aids.forEach((aid, name) -> {
            JSONObject info = HttpClient.get_info(aid, this.token, this.uid);
            String jsonAll = info.toString();
            //System.out.println(info);
            if (Objects.isNull(info)) {
                //card关闭1
                //updataLog("查询失败");
                Log.e("chiken", "查询失败");
            } else {
                Log.d("####info####", info.toString());
                time_aid.put(info.getString("joindate"), aid);
                aid_time.put(aid, info.getString("joindate"));

                aid_json.put(aid, jsonAll);

                ActInfo actInfo = Utility.handleActInfo(jsonAll);
                setJsonToMap(actInfo);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean enter(String aid) {
        JSONObject join = HttpClient.join(aid, this.token, this.uid);
        if (Objects.nonNull(join)) {
            //card关闭1
            //updataLog(aids.get(aid) + "报名成功");
            Log.d("enter", "报名成功");
            return true;
        } else {
            //card关闭2
            //updataLog("报名失败(JSONObject返回)");
            Log.d("enter", "报名失败");
            return false;
        }

    }


    //显示在TexView
//    public void updataLog(String log) {
//        stringBuilder.append(log);
//        stringBuilder.append("\r\n");
//
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                tvLog.setText(stringBuilder.toString());
//
//            }
//        });
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void schedule(String user, String passwd) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            update(user, passwd);
        }, 0, 3600, TimeUnit.SECONDS);
        if (login(user, passwd)) {
            get_aid();
            chiken();
            //updataLog("更新成功");
            Log.d("schedule", "更新成功");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void update(String user, String passwd) {
        if (login(user, passwd)) {
            get_aid();
            chiken();
//            updataLog("更新成功");
            Log.d("updata", "更新成功");

            //清屏，显示活动列表
            stringBuilder = new StringBuilder();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(R.id.fab),
                            "你好，" + name + "！活动列表加载完成ㄟ(≧◇≦)ㄏ",
                            Snackbar.LENGTH_LONG)
                            .setAction("确定", null);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(Color.DKGRAY);
                    snackbar.show();
                }
            });
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void joinMinTime() {
        if (aids.isEmpty() || aid_time.isEmpty() || time_aid.isEmpty()) {
            Toast.makeText(MainActivity.this, "活动列表为空,不可报名", Toast.LENGTH_SHORT).show();
            return;
        }

        //获取第一个key（时间字符串放入obj）
        String timeKeyMin = null;
        for (Map.Entry<String, String> entry: time_aid.entrySet()) {
            timeKeyMin = entry.getKey();
            if (timeKeyMin != null) {
                break;
            }
        }

        //updataLog("第一个活动时间（key）:" + timeKeyMin);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        LocalDateTime dateTimeMin = LocalDateTime.parse(timeKeyMin.split("-")[0], formatter);
        //updataLog("格式化后的时间:" + dateTimeMin);

        //遍历得到最小时间
        for (Map.Entry<String, String> entry: time_aid.entrySet()) {
            String timeEachKey = entry.getKey();
            LocalDateTime dateTimeEach = LocalDateTime.parse(timeEachKey.split("-")[0], formatter);
            if (dateTimeEach.isBefore(dateTimeMin)) {
                timeKeyMin = timeEachKey;
                dateTimeMin = dateTimeEach;
            }
        }

        //updataLog("最小时间Key:" + timeKeyMin);
        //updataLog("最小时间格式化：" + dateTimeMin);
        String aidTimeMin = time_aid.get(timeKeyMin);
        //updataLog("最小时间活动aid：" + aidTimeMin);
        String nameTimeMin = aids.get(aidTimeMin);
        //updataLog("最小时间活动名：" + nameTimeMin);

        String strForShow = "活动名: " + nameTimeMin + "\n\n" +
                "活动aid: " + aidTimeMin + "\n\n" +
                "报名时间： " + dateTimeMin;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        //用于填入EditText
        int MinTimeHour = dateTimeMin.getHour();
        int MinTimeMinute = dateTimeMin.getMinute();
        int MinTimeSecond = dateTimeMin.getSecond();
        builder.setTitle("最近活动")
                .setMessage(strForShow)
                .setPositiveButton("载入", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //将最小时间Aid放入输入框

                        mEtAid.setText(aidTimeMin);
                        mEtHour.setText(String.valueOf(MinTimeHour));
                        mEtMinute.setText(String.valueOf(MinTimeMinute));
                        mEtSecond.setText(String.valueOf(MinTimeSecond));
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(R.id.fab),
                                "已获取最近的活动Aid，并自动填入",
                                Snackbar.LENGTH_LONG)
                                .setAction("确定", null);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(Color.DKGRAY);
                        snackbar.show();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //清空输入框
                        mEtAid.setText("");
                        mEtHour.setText("");
                        mEtMinute.setText("");
                        mEtSecond.setText("");
                        LinearLayout mLlAlarm = findViewById(ll_aid_and_alarm_layout);
                        mLlAlarm.setVisibility(View.GONE);
                        flagAlarmShow[0] = false;
                    }
                })
                .show();



        //执行enter(nameTimeMin)
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (enter(aidTimeMin)) {
//                    updataLog("报名成功");
//                } else {
//                    updataLog("报名失败");
//                }
//            }
//        }).start();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //闹钟
        unregisterReceiver(alarmReceiver);
    }



    //将内容加载到Map
    public void setJsonToMap(ActInfo actInfo) {
        //aid
        String activityId = String.valueOf(actInfo.getActivityId());
        //图片
        List<String> imgUrlList = actInfo.getActivityImgSet();
        aid_img.put(activityId, imgUrlList.get(0));
    }
}