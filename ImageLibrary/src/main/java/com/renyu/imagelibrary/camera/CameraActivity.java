package com.renyu.imagelibrary.camera;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.renyu.commonlibrary.annotation.NeedPermission;
import com.renyu.commonlibrary.annotation.PermissionDenied;
import com.renyu.commonlibrary.baseact.BaseActivity;
import com.renyu.commonlibrary.commonutils.BarUtils;
import com.renyu.commonlibrary.params.InitParams;
import com.renyu.imagelibrary.R;
import com.renyu.imagelibrary.commonutils.Utils;

public class CameraActivity extends BaseActivity {

    @Override
    public void initParams() {

    }

    @Override
    public int initViews() {
        return R.layout.activity_camera;
    }

    @Override
    public void loadData() {
        permissionApply();
    }

    @NeedPermission(permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
            deniedDesp = "为了您可以正常使用照相机，\n请点击\"设置\"-\"权限\"-打开 \"存储空间\"与\"相机\" 权限。\n最后点击两次后退按钮，即可返回。")
    public void permissionApply() {
        if (getSupportFragmentManager().getFragments()==null || getSupportFragmentManager().getFragments().size()==0) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CameraFragment()).commitAllowingStateLoss();
        }
    }

    @PermissionDenied(permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    public void permissionDenied() {
        finish();
    }

    @Override
    public int setStatusBarColor() {
        return 0;
    }

    @Override
    public int setStatusBarTranslucent() {
        return 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BarUtils.setDark(this);
        super.onCreate(savedInstanceState);
    }

    public void backTo(String filePath) {
        //刷新相册
        Utils.refreshAlbum(this, filePath, InitParams.IMAGE_PATH);
        //返回上一级目录
        Intent intent=getIntent();
        Bundle bundle=new Bundle();
        bundle.putString("path", filePath);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
