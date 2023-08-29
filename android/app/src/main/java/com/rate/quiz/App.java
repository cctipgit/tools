package com.rate.quiz;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.annotation.NonNull;

import com.duxl.baselib.BaseApplication;
import com.duxl.baselib.http.GlobalHttpConfig;
import com.duxl.baselib.utils.AppManager;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.Utils;
import com.facebook.react.PackageList;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactNativeHost;
import com.facebook.soloader.SoLoader;
import com.rate.quiz.database.CurrencyDao;
import com.rate.quiz.database.DBHolder;
import com.rate.quiz.database.DBInit;
import com.rate.quiz.entity.CurrencyInfo;
import com.rate.quiz.http.AppGlobalHttpConfig;
import com.rate.quiz.http.SocketGlobalListener;
import com.rate.quiz.rnmodule.ToolModulePackage;
import com.rate.quiz.ui.activity.BaseActivity;
import com.rate.quiz.utils.AppsFlyerHelper;
import com.rate.quiz.utils.SoundPoolManager;
import com.rate.quiz.utils.UUIDUtils;
import com.zhangke.websocket.WebSocketHandler;
import com.zhangke.websocket.WebSocketManager;
import com.zhangke.websocket.WebSocketSetting;

import java.util.List;

public class App extends BaseApplication implements ReactApplication {

    private final String TAG = "App";

    private int mCurrentUIModel;
    private Handler loopConnectHandler = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.setApp(this);
//        initDatabase();
        DBHolder.init(this);
//        initWebSocket();
        SoundPoolManager.init(this);
        mCurrentUIModel = getResources().getConfiguration().uiMode;
        AppsFlyerHelper.init(this);
        UUIDUtils.init(this);
        SoLoader.init(this, false);
        if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
            // If you opted-in for the New Architecture, we load the native entry point for this app.
            DefaultNewArchitectureEntryPoint.load();
        }
//        ReactNativeFlipper.initializeFlipper(this, getReactNativeHost().getReactInstanceManager());

    }

    @Override
    public GlobalHttpConfig getGlobalHttpConfig() {
        return new AppGlobalHttpConfig();
    }

    private boolean isWebSocketInit = false;

    public void initWebSocket() {
        if (isWebSocketInit) return;
        isWebSocketInit = true;
        WebSocketSetting setting = new WebSocketSetting();
        // wss://echo.websocket.org
        //setting.setConnectUrl("wss://echo.websocket.org");
        setting.setConnectUrl(Constants.SOCKET_SERVER);

        //Timeout
        setting.setConnectTimeout(15 * 1000);

        //Setting LostTimeout
        setting.setConnectionLostTimeout(60);

        //setReconnectFrequency
        setting.setReconnectFrequency(60);


        setting.setReconnectWithNetworkChanged(true);


        WebSocketManager manager = WebSocketHandler.init(setting);

        manager.start();

        WebSocketHandler.registerNetworkChangedReceiver(this);

        WebSocketHandler.getDefault().addListener(new SocketGlobalListener());

        if (loopConnectHandler == null) {
            HandlerThread loopConnectHandlerThread = new HandlerThread("loopConnectHandlerThread");
            loopConnectHandlerThread.start();
            loopConnectHandler = new Handler(loopConnectHandlerThread.getLooper());
        }
        loopConnectHandler.post(loopCheckConnect);
    }

    private Runnable loopCheckConnect = new Runnable() {
        @Override
        public void run() {
            if (!WebSocketHandler.getDefault().isConnect()) {
                Log.i(TAG, "socket is disConnect, try reconnect");
                WebSocketHandler.getDefault().reconnect();
            } else {
                Log.i(TAG, "socket is connected");
            }
            loopConnectHandler.postDelayed(loopCheckConnect, WebSocketHandler.getDefault().isConnect() ? 1000 : 50);
        }
    };

    private void initDatabase() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                List<CurrencyInfo> currencyAll = new CurrencyDao().getCurrencyAll();
                if (EmptyUtils.isEmpty(currencyAll)) {
                    DBInit.init();
                }
            }
        }.start();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.uiMode != mCurrentUIModel) {
            mCurrentUIModel = newConfig.uiMode;
            List<Activity> activityList = AppManager.getAppManager().getActivityList();
            for (Activity activity : activityList) {
                if (activity instanceof BaseActivity) {
                    ((BaseActivity) activity).onUiChanged(newConfig);
                }
            }
        }
    }


    private final ReactNativeHost mReactNativeHost =
            new DefaultReactNativeHost(this) {
                @Override
                public boolean getUseDeveloperSupport() {
                    return BuildConfig.DEBUG;
                }

                @Override
                protected List<ReactPackage> getPackages() {
                    @SuppressWarnings("UnnecessaryLocalVariable")
                    List<ReactPackage> packages = new PackageList(this).getPackages();
                    // Packages that cannot be autolinked yet can be added manually here, for example:
                    // packages.add(new MyReactNativePackage());
                    packages.add(new ToolModulePackage());
                    return packages;
                }

                @Override
                protected String getJSMainModuleName() {
                    return "index";
                }

                @Override
                protected boolean isNewArchEnabled() {
                    return BuildConfig.IS_NEW_ARCHITECTURE_ENABLED;
                }

                @Override
                protected Boolean isHermesEnabled() {
                    return BuildConfig.IS_HERMES_ENABLED;
                }
            };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }
}
