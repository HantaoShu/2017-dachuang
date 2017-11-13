package com.stone.pile.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.stone.pile.R;

import static java.lang.Thread.sleep;

public class MainActivity2 extends AppCompatActivity {
    RxPermissions rxPermissions;
    private static final int REQUEST_CODE = 0;
    private ImageView photo;
    private static final int output_X = 480;
    private static final int output_Y = 480;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Multiselect();
    }

    public void Multiselect() {
        ImgSelConfig config = new ImgSelConfig.Builder(this, loader)
                .multiSelect(true)
                .rememberSelected(false)
                .statusBarColor(Color.parseColor("#3F51B5")).build();
        ImgSelActivity.startActivity(this, config, REQUEST_CODE);
    }

    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            final ArrayList<String> namelist = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            Handler mHandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case 0:
                            //完成主界面更新,拿到数据
                            HashMap<String,jsonbean> res = (HashMap<String,jsonbean>)msg.obj;
                            System.out.println("msg"+res.values());
                            SerializableHashMap data = new SerializableHashMap();
                            data.setMap((HashMap<String, jsonbean>) msg.obj);
                            System.out.println("msg2"+data.getMap().values());
                            Intent intent = new Intent(MainActivity2.this,MainActivity.class);
                            intent.putExtra("type","album");
                            intent.putExtra("data",data);
                            startActivity(intent);
                        default:
                            break;
                    }
                }

            };
            final HttpUtil h = new HttpUtil(mHandler);
            h.processdata(namelist);
            Toast.makeText(MainActivity2.this,"wait for a moment",Toast.LENGTH_SHORT).show();
                        Log.d("finish","finish");
        }

    }

}
