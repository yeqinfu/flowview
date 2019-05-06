package com.example.flowviewtest;

import android.app.Application;

import com.example.flowviewtest.entity.AppConfig;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by xm on 17-8-16.
 */
public class MainApplication extends Application {

    public static MainApplication mainApplication = null;
    public static AppConfig appConfig = null;
  //  public static DownloadManager downloadManager = null;
    //public static WebServerManager webServerManager;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
//        EventBus.getDefault().register(this);
        onAppInit();
    }

    private void onAppInit(){
        mainApplication = this;
        appConfig = new AppConfig();
       // downloadManager = new DownloadManager();
       // webServerManager = new WebServerManager();
    }

   /* @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowToastMessageEvent(ShowToastMessageEvent showToastMessageEvent){
        Toast.makeText(MainApplication.mainApplication, showToastMessageEvent.getMessage(), Toast.LENGTH_SHORT).show();
    }

    public void startDownloadForegroundService(){
        Intent intent = new Intent(MainApplication.mainApplication,DownloadForegroundService.class);
        startService(intent);
    }

    public void stopDownloadForegroundService(){
        Intent intent = new Intent(MainApplication.mainApplication,DownloadForegroundService.class);
        stopService(intent);
    }*/



}
