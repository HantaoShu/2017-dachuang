package com.stone.pile.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import com.stone.pile.R;
import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;

public class IndexActivity extends AppCompatActivity {
    ResideMenu resideMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);
        // create menu items;
        String titles[] = { "Album", "Take_Photo","Find Result" };
        int icon[] = { R.drawable.album, R.drawable.camera, R.drawable.find_res};
        final Class cls[] = {MainActivity2.class,MainActivity3.class,MainActivity.class};
        for ( int i = 0; i < titles.length; i++){
            ResideMenuItem item = new ResideMenuItem(this, icon[i], titles[i]);
            final int index = i;
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (index==1){
                        RxPermissions rxPermissions = new RxPermissions(IndexActivity.this);
                        rxPermissions.requestEach(Manifest.permission.CAMERA).subscribe(new DefaultSubscriber<Permission>() {
                            @Override
                            public void onNext(Permission permission) {
                                if (permission.granted) {
                                    startActivity(new Intent(IndexActivity.this, MainActivity3.class));
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
                    }
                    else if(index==2){
                        Toast.makeText(IndexActivity.this,"You didn't choose any picture(s)",Toast.LENGTH_SHORT).show();
                    }
                    else
                        startActivity(new Intent(IndexActivity.this,cls[index]));
//                    Toast.makeText(IndexActivity.this,"aaa",Toast.LENGTH_SHORT).show();
                }
            });
            resideMenu.addMenuItem(item,  ResideMenu.DIRECTION_LEFT); // or  ResideMenu.DIRECTION_RIGHT
        }
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);

    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }
}
