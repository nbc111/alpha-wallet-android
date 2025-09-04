package com.alphawallet.app;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import android.app.Activity;
import android.app.Application;
import android.app.UiModeManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.alphawallet.app.util.ShiplyLogger;
import com.alphawallet.app.util.TimberInit;
import com.alphawallet.app.walletconnect.AWWalletConnectClient;
import com.tencent.rdelivery.DependencyInjector;
import com.tencent.rdelivery.RDelivery;
import com.tencent.rdelivery.RDeliverySetting;
import com.tencent.rdelivery.dependencyimpl.HandlerTask;
import com.tencent.rdelivery.dependencyimpl.HttpsURLConnectionNetwork;
import com.tencent.rdelivery.dependencyimpl.MmkvStorage;
import com.tencent.rdelivery.dependencyimpl.SystemLog;
import com.tencent.upgrade.bean.UpgradeConfig;
import com.tencent.upgrade.core.DefaultUpgradeStrategyRequestCallback;
import com.tencent.upgrade.core.UpgradeManager;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;
import io.reactivex.plugins.RxJavaPlugins;
import io.realm.Realm;
import timber.log.Timber;

@HiltAndroidApp
public class App extends Application
{
    @Inject
    AWWalletConnectClient awWalletConnectClient;

    private static App mInstance;
    private final Stack<Activity> activityStack = new Stack<>();

    public static App getInstance()
    {
        return mInstance;
    }

    public Activity getTopActivity()
    {
        try
        {
            return activityStack.peek();
        }
        catch (EmptyStackException e)
        {
            //
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate()
    {
        super.onCreate();
        mInstance = this;
        Realm.init(this);
        TimberInit.configTimber();

        int defaultTheme = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt("theme", C.THEME_AUTO);

        if (defaultTheme == C.THEME_LIGHT)
        {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
        }
        else if (defaultTheme == C.THEME_DARK)
        {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
        }
        else
        {
            UiModeManager uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
            int mode = uiModeManager.getNightMode();
            if (mode == UiModeManager.MODE_NIGHT_YES)
            {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
            }
            else if (mode == UiModeManager.MODE_NIGHT_NO)
            {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
            }
        }

        RxJavaPlugins.setErrorHandler(Timber::e);

        try
        {
            awWalletConnectClient.init(this);
        }
        catch (Exception e)
        {
            Timber.tag("WalletConnect").e(e);
        }

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks()
        {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState)
            {
            }

            @Override
            public void onActivityDestroyed(Activity activity)
            {
            }

            @Override
            public void onActivityStarted(Activity activity)
            {
            }

            @Override
            public void onActivityResumed(Activity activity)
            {
                activityStack.push(activity);
            }

            @Override
            public void onActivityPaused(Activity activity)
            {
                pop();
            }

            @Override
            public void onActivityStopped(Activity activity)
            {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState)
            {
            }
        });

//        CrashReport.initCrashReport(getApplicationContext(), "b2f36fce1a", false);


        initShiply();
        autoDetectUpgrade();
    }

    private void initShiply() {
        /*UpgradeConfig.Builder builder = new UpgradeConfig.Builder();
        UpgradeConfig config = builder.appId("4df21e2e14").appKey("6d1e874d-e771-4b97-8eda-58084f9b9bb7").build();
        UpgradeManager.getInstance().init(this, config);*/

        Map<String, String> map = new HashMap<>();
        map.put("UserGender", "Male");
        UpgradeConfig.Builder builder = new UpgradeConfig.Builder();
        builder.appId("4df21e2e14") // 项目 appid
                .appKey("6d1e874d-e771-4b97-8eda-58084f9b9bb7") // 项目 appkey
                .systemVersion(String.valueOf(Build.VERSION.SDK_INT))   // 用户手机系统版本，用于匹配shiply前端创建任务时设置的系统版本下发条件
                .customParams(map)                                      // 自定义属性键值对，用于匹配shiply前端创建任务时设置的自定义下发条件
                .cacheExpireTime(1000 * 60 * 60 * 6)                  // 灰度策略的缓存时长（ms），如果不设置，默认缓存时长为1天
                .internalInitMMKVForRDelivery(true)            // 是否由sdk内部初始化mmkv(调用MMKV.initialize()),业务方如果已经初始化过mmkv可以设置为false
                .userId("0287774636035272")                          // 用户Id,用于匹配shiply前端创建的任务中的体验名单以及下发条件中的用户号码包
                .customLogger(new ShiplyLogger());                      // 日志实现接口，建议对接到业务方的日志接口，方便排查问题
        UpgradeManager.getInstance().init(this, builder.build());
    }

    private void autoDetectUpgrade() {
        UpgradeManager.getInstance().checkUpgrade(false, null, new DefaultUpgradeStrategyRequestCallback());
    }

    @Override
    public void onTrimMemory(int level)
    {
        super.onTrimMemory(level);
        if (awWalletConnectClient != null)
        {
            awWalletConnectClient.shutdown();
        }
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();
        activityStack.clear();
        if (awWalletConnectClient != null)
        {
            awWalletConnectClient.shutdown();
        }
    }

    private void pop()
    {
        activityStack.pop();
    }
}
