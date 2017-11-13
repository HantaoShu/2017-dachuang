package com.stone.pile.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import com.stone.pile.R;
import com.stone.pile.entity.ItemEntity;
import com.stone.pile.libs.PileLayout;
import com.stone.pile.util.Utils;
import com.stone.pile.widget.FadeTransitionImageView;
import com.stone.pile.widget.HorizontalTransitionLayout;
import com.stone.pile.widget.VerticalTransitionLayout;
import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.NameList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private View positionView;
    private PileLayout pileLayout;
    private List<ItemEntity> dataList;
    SerializableHashMap data;
    private String type;
    private int lastDisplay = -1;
    private ObjectAnimator transitionAnimator;
    private float transitionValue;
    private HorizontalTransitionLayout EmotionView, FileNameView;
    private VerticalTransitionLayout TypeView;
    private FadeTransitionImageView OrignImageView;
    private Animator.AnimatorListener animatorListener;
    private TextView descriptionView;
    private ResideMenu resideMenu;
    private Button menuButton;
    private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        data = (SerializableHashMap) intent.getSerializableExtra("data");
        type = intent.getStringExtra("type");
        if(data==null || type==null){
            data=new SerializableHashMap();
            data.setMap(new HashMap<String, jsonbean>());
            type = "";
        }

        menuButton = (Button) findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
        img = (ImageView) findViewById(R.id.imageView2);
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);
        // create menu items;
        String titles[] = { "Album", "Take_Photo","Select Result" };
        int icon[] = { R.drawable.album, R.drawable.camera, R.drawable.find_res};
        final Class cls[] = {MainActivity2.class,MainActivity3.class,MainActivity.class};
        for (int i = 0; i < titles.length; i++) {
            ResideMenuItem item = new ResideMenuItem(this, icon[i], titles[i]);
            final int index = i;
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (index == 1) {
                        RxPermissions rxPermissions = new RxPermissions(MainActivity.this);
                        rxPermissions.requestEach(Manifest.permission.CAMERA).subscribe(new DefaultSubscriber<Permission>() {
                            @Override
                            public void onNext(Permission permission) {
                                if (permission.granted) {
                                    startActivity(new Intent(MainActivity.this, MainActivity3.class));
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
                                    builder.setMessage("您未授权相机权限,将无法拍照,请在权限管理中开启相机权限")
                                            .setTitle("提示").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Uri packageURI = Uri.parse("package:" + getPackageName());
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                            startActivity(intent);
                                        }
                                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.create().show();
                                }
                            }
                        });
                    } else if(index==0) {
                        startActivity(new Intent(MainActivity.this, cls[index]));
                    }
                    else {
                        resideMenu.closeMenu();
                        resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
                    }
                }
            });
            resideMenu.addMenuItem(item,  ResideMenu.DIRECTION_LEFT); // or  ResideMenu.DIRECTION_RIGHT
        }
        addSelect();
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

        EmotionView = (HorizontalTransitionLayout) findViewById(R.id.emotionView);
        pileLayout = (PileLayout) findViewById(R.id.pileLayout);
        TypeView = (VerticalTransitionLayout) findViewById(R.id.typeView);
        descriptionView = (TextView) findViewById(R.id.descriptionView);
        FileNameView = (HorizontalTransitionLayout) findViewById(R.id.FileNameView);
        OrignImageView = (FadeTransitionImageView) findViewById(R.id.OrignImageView);

        // 1. 状态栏侵入
        boolean adjustStatusHeight = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            adjustStatusHeight = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }

        // 2. 状态栏占位View的高度调整
        String brand = Build.BRAND;
        if (brand.contains("Xiaomi")) {
            Utils.setXiaomiDarkMode(this);
        } else if (brand.contains("Meizu")) {
            Utils.setMeizuDarkMode(this);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            adjustStatusHeight = false;
        }
        if (adjustStatusHeight) {
            adjustStatusBarHeight(); // 调整状态栏高度
        }

        animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                FileNameView.onAnimationEnd();
                EmotionView.onAnimationEnd();
                TypeView.onAnimationEnd();
                OrignImageView.onAnimationEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        initDataList(data.getMap());
        pileLayout.setAdapter(new PileLayout.Adapter() {
            @Override
            public int getLayoutId() {
                return R.layout.item_layout;
            }

            @Override
            public void bindView(View view, int position) {
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                if (viewHolder == null) {
                    viewHolder = new ViewHolder();
                    viewHolder.imageView = (ImageView) view.findViewById(R.id.imageView);
                    view.setTag(viewHolder);
                }
                Log.d("path", dataList.get(position).getPicPath());
                Glide.with(MainActivity.this).load(dataList.get(position).getPicPath()).into(viewHolder.imageView);
            }

            @Override
            public int getItemCount() {
                return dataList.size();
            }

            @Override
            public void displaying(int position) {
                int people = 0;
                descriptionView.setText("It's the "+ (position+1)+"picture."+dataList.get(position).getDescription()) ;
                if (lastDisplay < 0) {
                    initSecene(position);
                    lastDisplay = 0;
                } else if (lastDisplay != position) {
                    transitionSecene(position);
                    lastDisplay = position;
                }
            }

            @Override
            public void onItemClick(View view, int position) {
                super.onItemClick(view, position);
            }
        });
    }
    private void addSelect(){
        ResideMenuItem HappyItem = new ResideMenuItem(MainActivity.this,R.drawable.happy,"happy");
        ResideMenuItem unHappyItem = new ResideMenuItem(MainActivity.this,R.drawable.unhappy,"unhappy");
        ResideMenuItem NoramalItem = new ResideMenuItem(MainActivity.this,R.drawable.normal_happy,"normal");
        ResideMenuItem AllItem = new ResideMenuItem(MainActivity.this,R.drawable.select_all,"all");
        resideMenu.addMenuItem(HappyItem,ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(unHappyItem,ResideMenu.DIRECTION_RIGHT);
        resideMenu.addMenuItem(NoramalItem,ResideMenu.DIRECTION_RIGHT);
        HappyItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,jsonbean> res = data.getMap();
                Iterator<Map.Entry<String,jsonbean>> iterator=res.entrySet().iterator();
                Log.d("select",""+(res.size()));
                ArrayList<String> needremove = new ArrayList<String>();
                while(iterator.hasNext()){
                    Map.Entry<String,jsonbean>entry=iterator.next();
                    Log.d("select",entry.getKey());
                    String k = entry.getKey();
                    jsonbean va = entry.getValue();
                    if(va.happy==0){
                        Log.d("select","111");
                        needremove.add(k);
                        Log.d("select","112");
                    }
                    Log.d("select","222");
                }
                for(String m : needremove){
                    res.remove(m);
                }
                resideMenu.closeMenu();
                if (res.size()!=0) {
                    Log.d("select", "333");
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtra("type", "select");
                    SerializableHashMap m = new SerializableHashMap();
                    m.setMap(res);
                    intent.putExtra("data", m);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this,"No such picture",Toast.LENGTH_SHORT).show();
                }
            }
        });
        unHappyItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,jsonbean> res = data.getMap();
                Iterator<Map.Entry<String,jsonbean>> iterator=res.entrySet().iterator();
                Log.d("select",""+(res.size()));
                ArrayList<String> needremove = new ArrayList<String>();
                while(iterator.hasNext()){
                    Map.Entry<String,jsonbean>entry=iterator.next();
                    Log.d("select",entry.getKey());
                    String k = entry.getKey();
                    jsonbean va = entry.getValue();
                    if(va.sad==0&&va.angry==0&&va.fear==0&&va.disgust==0&&va.surprise==0){
                        Log.d("select","111");
                        needremove.add(k);
                        Log.d("select","112");
                    }
                    Log.d("select","222");
                }
                for(String m : needremove){
                    res.remove(m);
                }
                resideMenu.closeMenu();
                if (res.size()!=0) {
                    Log.d("select", "333");
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtra("type", "select");
                    SerializableHashMap m = new SerializableHashMap();
                    m.setMap(res);
                    intent.putExtra("data", m);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this,"No such picture",Toast.LENGTH_SHORT).show();
                }
            }
        });
        NoramalItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,jsonbean> res = data.getMap();
                Iterator<Map.Entry<String,jsonbean>> iterator=res.entrySet().iterator();
                Log.d("select",""+(res.size()));
                ArrayList<String> needremove = new ArrayList<String>();
                while(iterator.hasNext()){
                    Map.Entry<String,jsonbean>entry=iterator.next();
                    Log.d("select",entry.getKey());
                    String k = entry.getKey();
                    jsonbean va = entry.getValue();
                    if(va.neutral==0){
                        Log.d("select","111");
                        needremove.add(k);
                        Log.d("select","112");
                    }
                    Log.d("select","222");
                }
                for(String m : needremove){
                    res.remove(m);
                }
                resideMenu.closeMenu();
                if (res.size()!=0) {
                    Log.d("select", "333");
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtra("type", "select");
                    SerializableHashMap m = new SerializableHashMap();
                    m.setMap(res);
                    intent.putExtra("data", m);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this,"No such picture",Toast.LENGTH_SHORT).show();
                }
            }
        });
//        AllItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                initDataList(data.getMap());
//            }
//        });
    }
    private void initSecene(int position) {
        TypeView.firstInit(dataList.get(position).getType());
        img.setImageResource(dataList.get(position).getEmotion());
        OrignImageView.firstInit(dataList.get(position).getPicPath());
        FileNameView.firstInit(dataList.get(position).getPicPath().
                substring(dataList.get(position).getPicPath().lastIndexOf('/')+1));

    }

    private void transitionSecene(int position) {
        if (transitionAnimator != null) {
            transitionAnimator.cancel();
        }
        img.setImageResource(dataList.get(position).getEmotion());
        OrignImageView.saveNextPosition(position, dataList.get(position).getOrignImagePath());
        FileNameView.firstInit(dataList.get(position).getPicPath().
                substring(dataList.get(position).getPicPath().lastIndexOf('/')+1));
        TypeView.saveNextPosition(position, dataList.get(position).getType());
        transitionAnimator = ObjectAnimator.ofFloat(this, "transitionValue", 0.0f, 1.0f);
        transitionAnimator.setDuration(300);
        transitionAnimator.start();
        transitionAnimator.addListener(animatorListener);

    }

    /**
     * 调整沉浸状态栏
     */
    private void adjustStatusBarHeight() {
        int statusBarHeight = Utils.getStatusBarHeight(this);
        ViewGroup.LayoutParams lp = positionView.getLayoutParams();
        lp.height = statusBarHeight;
        positionView.setLayoutParams(lp);
    }


    /**
     * 从asset读取文件json数据
     */
    private void initDataList(HashMap<String,jsonbean> res) {
        dataList = new ArrayList<>();
        System.out.println("init"+res.values());
        for (Map.Entry<String, jsonbean> entry : res.entrySet()) {
            jsonbean val = entry.getValue();
            dataList.add(new ItemEntity(val,type));

        }
    }

    /**
     * 属性动画
     */
    public void setTransitionValue(float transitionValue) {
        this.transitionValue = transitionValue;
        TypeView.duringAnimation(transitionValue);
        FileNameView.duringAnimation(transitionValue);
        EmotionView.duringAnimation(transitionValue);
        OrignImageView.duringAnimation(transitionValue);
    }

    public float getTransitionValue() {
        return transitionValue;
    }

    class ViewHolder {
        ImageView imageView;
    }

}