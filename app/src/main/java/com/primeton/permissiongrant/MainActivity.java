package com.primeton.permissiongrant;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button  btn1;
    Button btn2;
    Button btn3;
    PermissionGrantManager permissionGetManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = (Button) findViewById(R.id.btn_one);
        btn2 = (Button) findViewById(R.id.btn_two);
        btn3 = (Button) findViewById(R.id.btn_three);

        permissionGetManager = new PermissionGrantManager(this);
        // 检查权限，若有权限返回 true 没有就返回 false。   请
       //permissionGetManager.checkPermission(this,Manifest.permission.CAMERA);
        //请求一个权限
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求权限里面包含了权限检查。
                permissionGetManager.requestPermission(1,new String[]{Manifest.permission.CAMERA})
                        .getRequestResults(new PermissionInterface() {
                            @Override
                            public void accept(String[] permissions) {
                                String[] aa = permissions;
                            }

                            @Override
                            public void denied(String[] permissions) {
                                String[] hh = permissions;
                            }

                            @Override
                            public void allDenied(String[] permissions) {String[] ff = permissions;
                            }
                        });
            }
        });

        //请求多个权限
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //请求权限
                String []permisssions = {Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION};
                permissionGetManager.requestPermission(2,permisssions)
                        //获取权限请求结果
                        .getRequestResults(new PermissionInterface() {
                            @Override
                            public void accept(String[] permissions) {
                                //自己处理逻辑
                            }

                            @Override
                            public void denied(String[] permissions) {
                                //自己处理逻辑
                            }

                            @Override
                            public void allDenied(String[] permissions) {
                                // 自己处理逻辑，可以提示用户到 设置页面打开设置。
                                // permissionGetManager.showDialogForNoPermession 弹出提示对话框
                                // permissionGetManager.goToSettingPage; 跳转设置页面
                            }
                        });
            }
        });
        //申请 必要权限，并给与弹框提示
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String []permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                permissionGetManager.requestPermission(3,permission)
                        .getRequestResults(new PermissionInterface() {
                            @Override
                            public void accept(String[] strings) {
                                if (strings.length>0)
                                Toast.makeText(MainActivity.this, "权限申请成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void denied(String[] strings) {//被拒绝的权限都会进入该 权限数组，若没有，则该数组为空
                                if (strings.length >0) {
                                    // 可以自己校验当前权限是否是自己要处理的权限
                                    String title = "请打开权限";
                                    String msg = "应用使用需要存储权限，否则无法运行，请点击确定申请并通过！";
                                    String positive = "确定";
                                    String negative = "退出";
                                    permissionGetManager.showDialogForNoPermession(title, msg, positive, negative,
                                            new PermissionGrantManager.DialogButtonInterface() {
                                                @Override
                                                public void positiveButtonClick(DialogInterface dialogInterface) {
                                                    //确定处理 继续申请当前 权限
                                                    permissionGetManager.requestPermission(3, permission);
                                                    //无须获取结果，结果会回调到当前 PermissionInterface中
                                                }

                                                @Override
                                                public void negativeButtonClick(DialogInterface dialogInterface) {
                                                    //退出处理
                                                    System.exit(0);
                                                }
                                            });
                                }
                            }

                            @Override
                            public void allDenied(String[] strings) {
                                //点了不再提醒并拒绝
                                if (strings.length > 0)
                                {
                                    String title = "请打开权限";
                                    String msg = "应用使用需要存储权限，否则无法运行，请点击确定到设置页面打开权限！";
                                    String positive = "确定";
                                    String negative = "退出";
                                    permissionGetManager.showDialogForNoPermession(title, msg, positive, negative,
                                            new PermissionGrantManager.DialogButtonInterface() {
                                                @Override
                                                public void positiveButtonClick(DialogInterface dialogInterface) {
                                                    //确定处理  进入到设置页面
                                                    permissionGetManager.goToSettingPage();
                                                }

                                                @Override
                                                public void negativeButtonClick(DialogInterface dialogInterface) {
                                                    //退出处理
                                                    System.exit(0);
                                                }
                                            });
                                }
                            }
                        });
            }
        });

    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionGetManager.onRequestPermissionsResult(requestCode,permissions,grantResults);


    }
}
