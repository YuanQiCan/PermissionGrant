package com.primeton.permissiongrant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;

import java.util.ArrayList;

public class PermissionGrantManager {
    private Context context;
    private PermissionInterface permissionInterface; //权限接口，用来回调请求结果
    private ArrayList<String> mPermissionRequestList = new ArrayList<>(); //保存需要申请的权限
    ArrayList<String> mGrantList = new ArrayList<>();
    private ArrayList<String> mAllDeniedList = new ArrayList<>();
    private int mRequestCode;
    private int targetSdkVersion = 0;
    private DialogButtonInterface dialogButtonInterface;

    public PermissionGrantManager(Context context)
    {
        this.context = context;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            //
        }
    }

    /**
     * 请求权限
     * @param requestCode 请求码
     * @param permissions 权限数组
     * @return PermissionGetManager对象
     */
    public PermissionGrantManager requestPermission(int requestCode, String []permissions)
    {
        mRequestCode = requestCode;
        mPermissionRequestList.clear();
        mGrantList.clear();
        mAllDeniedList.clear();
        for (int i = 0 ; i < permissions.length; i++) {
            if(!checkPrimission(context,permissions[i])) {//无权限
                mPermissionRequestList.add(permissions[i]);
            }else {
                mGrantList.add(permissions[i]);
            }
        }

        if (targetSdkVersion < 23) //当小于23时，即使用户在设置页面关闭了权限，申请权限也不会弹出权限请求框，因此直接引导用户去设置页面开启
        {
            mAllDeniedList.addAll(mPermissionRequestList);
        }
        if (!mPermissionRequestList.isEmpty()) //当请求的权限都已经授权，就不需要申请
        {
            //请求权限
            String [] PermissionRequestArray = (String [])mPermissionRequestList.toArray(new String[0]);
            ActivityCompat.requestPermissions((Activity)context, PermissionRequestArray, requestCode);
        }
        return this;
    }

    /**请求获取权限后的回调
     * @param requestCode  请求码
     * @param permissions  申请的权限数组
     * @param grantResults  返回的结果数组
     */
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults)
    {
        ArrayList<String> deniedList = new ArrayList<>();
        if (mRequestCode == requestCode) {
            for (int i = 0 ; i< permissions.length;i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) { //获得权限
                    mGrantList.add(permissions[i]);
                }else{ //权限申请被拒绝
                    boolean bFlag = ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissions[i]);
                    if (!bFlag) {//点了不在提醒后的申请权限
                        mAllDeniedList.add(permissions[i]);
                    }else {
                        deniedList.add(permissions[i]); //拒绝的权限
                    }
                }
            }
        }
        String []grantArray = (String [])mGrantList.toArray(new String[0]);
        String []deniedArray = (String [])deniedList.toArray(new String[0]);
        String []allDeniedArray = (String [])mAllDeniedList.toArray(new String[0]);
        permissionInterface.accept(grantArray); //回调接受列表
        permissionInterface.denied(deniedArray);//回调拒绝列表
        permissionInterface.allDenied(allDeniedArray);
    }



    /**
     * 判断是否有权限 包含 sdk23以上版本和23以下版本
     * @param context
     * @param primission
     * @return
     */
    private  boolean checkPrimission(Context context, String primission)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { //当前手机版本小 23时，不需要处理权限
            return  true; //默认有权限
        }
        boolean result = false;
        if (targetSdkVersion >= Build.VERSION_CODES.M) {  //targetSdkVersion >= 23
            if (ContextCompat.checkSelfPermission(context, primission)
                    != PackageManager.PERMISSION_GRANTED) {
                result = false;
            } else {
                result = true;
            }
        }else{ //targetSdkVersion < 23
            if (PermissionChecker.checkSelfPermission(context, primission) != PermissionChecker.PERMISSION_GRANTED) {
                result = false;
            } else {
                result = true;
            }
        }
        return result;
    }


    /**
     * 获取 请求权限后的结果
     * @param permissionInterface
     */
    public void getRequestResults(PermissionInterface permissionInterface)
    {
        this.permissionInterface = permissionInterface;
        //当请求的权限都有权限时，直接返回 整个权限数组
        if (mPermissionRequestList.isEmpty()) {
            String []grantArray = (String [])mGrantList.toArray(new String[0]);
            permissionInterface.accept(grantArray);
        }
    }

    /**
     * 不再提醒后，默认弹框,若不满足用户需求，用户可以自己定义dialog
     * @param title  标题
     * @param msg   显示内容
     * @param positiveStr     确定按钮显示文本
     * @param negativeStr     取消按钮显示文本
     * @param dialogButtonInterface  回调接口
     */
    public void showDialogForNoPermession(String title, String msg, String positiveStr,String negativeStr,final DialogButtonInterface dialogButtonInterface)
    {
        new AlertDialog.Builder(context).setTitle(title)//设置对话框标题  //设置显示的内容
            .setMessage(msg) //设置显示的内容
            .setPositiveButton(positiveStr, new DialogInterface.OnClickListener() {//添加确定按钮
                @Override
                public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                dialogButtonInterface.positiveButtonClick(dialog);
            }}) //
            .setNegativeButton(negativeStr, new DialogInterface.OnClickListener() {//添加返回按钮
                @Override
                public void onClick(DialogInterface dialog, int which) {//响应事件
                dialogButtonInterface.negativeButtonClick(dialog);
                }
            }) //
            .show();
    }

    /**
     * 跳转到 应用设置页面
     */
    public void goToSettingPage()
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    public interface DialogButtonInterface{
        void positiveButtonClick(DialogInterface dialog);
        void negativeButtonClick(DialogInterface dialog);
    }

}
