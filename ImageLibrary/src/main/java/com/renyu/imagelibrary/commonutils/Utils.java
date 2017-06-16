package com.renyu.imagelibrary.commonutils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.renyu.commonlibrary.params.InitParams;
import com.renyu.imagelibrary.camera.CameraActivity;
import com.renyu.imagelibrary.crop.UCrop;
import com.renyu.imagelibrary.photopicker.PhotoPickerActivity;
import com.renyu.imagelibrary.preview.ImagePreviewActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by renyu on 2017/1/3.
 */

public class Utils {

    /**
     * 选择调用系统相册
     */
    public static void chooseImage(Activity activity, int requestCode) {
        //调用调用系统相册
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 照相
     */
    public static void takePicture(Activity activity, int requestCode) {
        Intent intent=new Intent(activity, CameraActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * UCrop剪裁
     * @param sourcePath
     * @param activity
     * @param requestCode
     * @param ratio 宽/高
     */
    public static void cropImage(String sourcePath, Activity activity, int requestCode, float ratio) {
        String destinationPath=InitParams.IMAGE_PATH+"/"+System.currentTimeMillis()+".jpg";
        UCrop uCrop = UCrop.of(Uri.fromFile(new File(sourcePath)), Uri.fromFile(new File(destinationPath)));
        UCrop.Options options = new UCrop.Options();
        if (ratio!=0) {
            options.withAspectRatio(ratio, 1);
        }
        else {
            options.setFreeStyleCropEnabled(true);
        }
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(80);
        options.setHideBottomControls(true);
        uCrop.withOptions(options);
        uCrop.start(activity, requestCode);
    }

    /**
     * 选择图片
     */
    public static void choicePic(Activity activity, int maxNum, int requestCode) {
        Intent intent=new Intent(activity, PhotoPickerActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("maxNum", maxNum);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 剪裁图片文件
     * @param filePath
     * @param ratio 宽/高
     */
    public static void cropFile(String filePath, String newFilePath, float ratio) {
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(filePath, options);
        float bmpWidth=options.outWidth;
        float bmpHeight=options.outHeight;
        int realWidth= (int) bmpWidth;
        int realHeight= (int) bmpHeight;
        int startX=0;
        int startY=0;
        // 宽度过大
        if (bmpWidth/bmpHeight>ratio) {
            realWidth= (int) (bmpHeight*ratio);
            realHeight= (int) bmpHeight;
            startX= (int) ((bmpWidth-realWidth)/2);
            startY= 0;
        }
        // 高度过大
        else if (bmpWidth/bmpHeight<ratio) {
            realWidth= (int) bmpWidth;
            realHeight= (int) (bmpWidth/ratio);
            startX= 0;
            startY= (int) ((bmpHeight-realHeight)/2);
        }
        BitmapFactory.Options newOpts=new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap=BitmapFactory.decodeFile(filePath, newOpts);
        bitmap=Bitmap.createBitmap(bitmap, startX, startY, realWidth, realHeight);
        //生成新图片
        try {
            FileOutputStream fos = new FileOutputStream(newFilePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
            fos.flush();
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拍照后刷新系统相册
     * @param context
     * @param photoFile
     */
    public static void displayToGallery(Context context, File photoFile) {
        if (photoFile == null || !photoFile.exists()) {
            return;
        }
        String photoPath = photoFile.getAbsolutePath();
        String photoName = photoFile.getName();
        // 其次把文件插入到系统图库
        try {
            ContentResolver contentResolver = context.getContentResolver();
            MediaStore.Images.Media.insertImage(contentResolver, photoPath, photoName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + photoPath)));
    }

    /**
     * 相册预览
     * @param context
     * @param canDownload
     * @param position
     * @param canEdit
     * @param urls
     */
    public static void showPreiew(Context context, boolean canDownload, int position, boolean canEdit, ArrayList<String> urls) {
        Intent intent=new Intent(context, ImagePreviewActivity.class);
        Bundle bundle=new Bundle();
        bundle.putBoolean("canDownload", canDownload);
        bundle.putInt("position", position);
        bundle.putBoolean("canEdit", canEdit);
        bundle.putStringArrayList("urls", urls);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
