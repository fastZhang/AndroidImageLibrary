package com.renyu.androidimagelibrary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import com.renyu.commonlibrary.params.InitParams;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Utils.takePicture(this, 100);

//        ArrayList<String> strings=new ArrayList<>();
//        strings.add(Environment.getExternalStorageDirectory().getPath()+"/1.jpg");
//        Intent intent=new Intent(this, ImagePreviewActivity.class);
//        Bundle bundle=new Bundle();
//        bundle.putBoolean("canDownload", false);
//        bundle.putInt("position", 0);
//        bundle.putBoolean("canEdit", true);
//        bundle.putStringArrayList("urls", strings);
//        bundle.putString("rightNav", "查看");
//        bundle.putParcelable("rightNavClick", new RightNavClick());
//        intent.putExtras(bundle);
//        startActivity(intent);

//        Utils.showLongPreview(this, "http://7b1g8u.com1.z0.glb.clouddn.com/ccc.jpg", Environment.getExternalStorageDirectory().getPath());

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/1.jpg", options);
        File cropFile = null;
        try {
            cropFile = new Compressor(getApplicationContext())
                    .setMaxWidth(options.outWidth/2)
                    .setMaxHeight(options.outHeight/2)
                    .setQuality(80)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setDestinationDirectoryPath(InitParams.CACHE_PATH)
                    .compressToFile(new File(Environment.getExternalStorageDirectory().getPath()+"/1.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {

        }
    }
}
