# PermissionGrant
android6.0 权限处理，批量申请权限封装，包含以下功能:
1. 权限检查，包括targetSdkVersion 大于23和小于23的情况
2. 批量申请权限处理
3. 不再提醒回调处理

**使用方式**
  1. 添加远程依赖
     在project build.gradle文件中添加远程库
     ```
      allprojects {
        repositories {
          ...
          maven { url 'https://www.jitpack.io' }
        }
     }
     ```
     然后在module build.gradle中添加依赖
     ```
     dependencies {
     	implementation 'com.github.YuanQiCan:PermissionGrant:v1.0.0'
     }
     ```
  2. 申请权限前，现在AndroidManifest.xml文件中添加权限，类似如下:
  	```
  	<uses-permission android:name="android.permission.CAMERA" />
  	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
  	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"></uses-permission>
  	```  
**检查权限**
	先实例化permissionGrantManager，然后调用checkPrimission函数检查权限，若有权限返回 true，若没有返回false  
	```
	permissionGrantManager.checkPrimission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
	```  
**申请权限**
申请权限时会先检查权限，已经拥有的权限，最后会回调到 accept函数的权限数组中； 没有的权限，需要弹框申请的会弹框申请
先创建PermissionGrantManager 实例，然后通过requestPermission请求需要的权限，最后通过getRequestResults 获取请求结果，操作如下：
    ```
    PermissionGrantManager permissionGrantManager = new PermissionGrantManager(MainActivity.this);
        permissionGetManager.requestPermission(2, new String[]{ Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
                .getRequestResults(new PermissionInterface() {
            @Override
            public void accept(String[] strings) {
                // 有权限以及获得的权限，都会进入该数组。 若没有获得的权限， 该数组为空
            }

            @Override
            public void denied(String[] strings) {
                // 没有有权限以及拒绝的权限，都会进入该数组。 否则， 该数组为空
            }

            @Override
            public void allDenied(String[] strings) {
                // 点了不再提醒后的权限，都会进入该数组。 否则， 该数组为空
            }
        });
	```
	在 activity的 onRequestPermissionsResult回调中，调用permissionGetManager.onRequestPermissionsResult，如下：
	```
	       @Override
	      public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		  super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		  permissionGetManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
		  }
	```

**说明**：
    在处理必须权限时，用户拒绝，需要弹出提醒框，可以在 denied回调函数中处理，然后接着调用permissionGetManager.requestPermission请求权限（此时不需要调用getRequestResults获取结果，结果会自动在当前PermissionInterface接口里回调）即可。
    
     
