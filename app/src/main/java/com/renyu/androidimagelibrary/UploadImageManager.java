package com.renyu.androidimagelibrary;

import android.util.Log;

import com.renyu.commonlibrary.network.OKHttpHelper;
import com.renyu.commonlibrary.network.OKHttpUtils;
import com.renyu.commonlibrary.network.ProgressRequestBody;
import com.renyu.imagelibrary.bean.UploadTaskBean;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.Response;

/**
 * Created by renyu on 2017/12/7.
 */

public class UploadImageManager {

    ExecutorService uploadService;
    OKHttpUtils okHttpUtils;

    public interface UpdateCallBack {
        void updateMap(UploadTaskBean bean);
    }
    UpdateCallBack callBack;

    // 任务线程组
    ConcurrentHashMap<String, Future> tasks;
    // 任务状态组
    ConcurrentHashMap<String, UploadTaskBean> beans;

    public UploadImageManager() {
        tasks = new ConcurrentHashMap<>();
        beans = new ConcurrentHashMap<>();

        okHttpUtils=OKHttpHelper.getInstance().getOkHttpUtils();

        uploadService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    /**
     * 添加任务
     * @param filePath
     */
    public synchronized void addTask(String filePath, String url) {
        Runnable runnable = () -> {
            // 开始上传
            UploadTaskBean bean = beans.get(filePath);
            bean.setUrl("");
            bean.setProgress(0);
            bean.setStatue(UploadTaskBean.UploadState.UPLOADING);

            HashMap<String, File> fileHashMap=new HashMap<>();
            fileHashMap.put("fileData", new File(filePath));
            Response resp=okHttpUtils.syncUpload(url, new HashMap<>(), fileHashMap, (l, l1) -> {
                Log.d("UploadImageManager", "UploadImageManager " + l + " " + l1);
            });
            if (resp==null) {
                Log.d("UploadImageManager", filePath + "发布失败");
            }
            else if (resp.isSuccessful()) {
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(resp.body().string());
                    String picUrl=jsonObject.getJSONObject("data").getString("picUrl");
                    Log.d("UploadImageManager", filePath + "发布成功:" + picUrl);

                    bean.setProgress(100);
                    bean.setUrl(picUrl);
                    bean.setStatue(UploadTaskBean.UploadState.UPLOADSUCCESS);
                    if (callBack != null) {
                        callBack.updateMap(bean);
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("UploadImageManager", filePath + "发布失败");
                }
            }
            else {
                Log.d("UploadImageManager", filePath + "发布失败");
            }

            // 下载失败
            bean.setProgress(0);
            bean.setUrl("");
            bean.setStatue(UploadTaskBean.UploadState.UPLOADFAIL);
            if (callBack != null) {
                callBack.updateMap(bean);
            }
        };

        // 添加上传状态Map中
        UploadTaskBean bean = new UploadTaskBean();
        bean.setFilePath(filePath);
        bean.setProgress(0);
        bean.setStatue(UploadTaskBean.UploadState.UPLOADPREPARE);
        beans.put(filePath, bean);
        // 添加上传线程Map中
        tasks.put("aizuna_"+new File(filePath).getName().substring(0, new File(filePath).getName().indexOf(".")), uploadService.submit(runnable));
    }

    /**
     * 取消一个任务
     * @param tag
     */
    public synchronized void cancelTask(String tag) {
        if (tasks.containsKey(tag)) {
            tasks.remove(tag).cancel(true);
        }
        beans.remove(tag);
    }

    /**
     * 关闭全部任务
     */
    public synchronized void stopAllTask() {
        uploadService.shutdownNow();
        tasks.clear();
        beans.clear();
    }

    public void addListener(UpdateCallBack callBack) {
        this.callBack = callBack;
    }
}
