package com.arixo.arixochat;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.qq.e.comm.managers.GDTAdSdk;
import com.qq.e.comm.managers.setting.GlobalSetting;
import com.tencent.bugly.crashreport.CrashReport;

public class ArixoChatApplication extends MultiDexApplication {

    protected static Application sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = this;
        config(this);
    }

    protected void config(Context context) {
        CrashReport.initCrashReport(getApplicationContext(), "5a3215ef7a", false);


        // 建议在初始化 SDK 前进行此设置
        GlobalSetting.setEnableCollectAppInstallStatus(true);
        // 通过调用此方法初始化 SDK。如果需要在多个进程拉取广告，每个进程都需要初始化 SDK。
        GDTAdSdk.init(context, Constants.APPID);
        GlobalSetting.setChannel(1);
        GlobalSetting.setEnableMediationTool(true);
        String packageName = context.getPackageName();
        //Get all activity classes in the AndroidManifest.xml

    }

    public static Application getAppContext() {
        return sAppContext;
    }
}
