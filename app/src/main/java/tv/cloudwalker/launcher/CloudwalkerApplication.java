package tv.cloudwalker.launcher;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CloudwalkerApplication extends Application {

    private FirebaseAnalytics mFirebaseAnalytics;
    private AnalyticsBr analyticsBr;
    private AppOprationBr appOprationBr;



    @Override
    public void onCreate() {
        super.onCreate();

        getAnalytics();
        setAnalyticsBr();
        setAppOprationBr();
    }


    public FirebaseAnalytics getAnalytics(){
        if(mFirebaseAnalytics == null){
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            mFirebaseAnalytics.setUserId(getEthMacAddress());
        }
        return mFirebaseAnalytics;
    }

    private void setAnalyticsBr(){
        if(analyticsBr == null)
            analyticsBr = new AnalyticsBr();

        IntentFilter intentFilter = new IntentFilter("tv.cloudwalker.profile.action.SET");
        intentFilter.addAction("tv.cloudwalker.cde.action.OPEN");
        intentFilter.addAction("tv.cloudwalker.cde.action.SEARCH");
        intentFilter.addAction("tv.cloudwalker.cde.action.CLICKED");
        intentFilter.addAction("tv.cloudwalker.apps.action.OPEN");
        intentFilter.addAction("tv.cloudwalker.apps.action.APP_OPEN");
        registerReceiver(analyticsBr, intentFilter);
    }

    private void setAppOprationBr(){
        if(appOprationBr == null){
            appOprationBr = new AppOprationBr();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_INSTALL_PACKAGE);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addDataScheme("package");
        registerReceiver(appOprationBr, intentFilter);
    }


    public String getEthMacAddress() {
        try {
            return loadFileAsString("/sys/class/net/eth0/address").toUpperCase().substring(0, 17);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String loadFileAsString(String filePath) throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }

    public static String getSystemProperty(String key) {
        String value = null;
        try {
            value = (String) Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class).invoke(null, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    @Override
    public void onTerminate() {
        if(analyticsBr != null)
            unregisterReceiver(analyticsBr);
        if(appOprationBr != null)
            unregisterReceiver(appOprationBr);

        super.onTerminate();
    }

    public Drawable getDrawable(String resourceName) {
        return getAppResources().getDrawable(getAppResources().getIdentifier(resourceName, "drawable", "tv.cloudwalker.skin"));
    }

    public String getString(String resourceName) {
        return getAppResources().getString(getAppResources().getIdentifier(resourceName, "string", "tv.cloudwalker.skin"));
    }

    public int getColor(String resourceName) {
        return getAppResources().getColor(getAppResources().getIdentifier(resourceName, "color", "tv.cloudwalker.skin"));
    }

    public int getInteger(String resourceName) {
        return getAppResources().getInteger(getAppResources().getIdentifier(resourceName, "integer", "tv.cloudwalker.skin"));
    }

    public boolean getBool(String resourceName) {
        return getAppResources().getBoolean(getAppResources().getIdentifier(resourceName, "bool", "tv.cloudwalker.skin"));
    }



    public Resources getAppResources() {
        try {
            return getPackageManager().getResourcesForApplication("tv.cloudwalker.skin");
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    class AppOprationBr extends BroadcastReceiver{

        public AppOprationBr(){}

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() == null) return;

            switch (intent.getAction())
            {
                case Intent.ACTION_PACKAGE_INSTALL:
                case Intent.ACTION_PACKAGE_ADDED:
                case Intent.ACTION_PACKAGE_CHANGED:
                {
                    String packageName = intent.getData().getEncodedSchemeSpecificPart();
                    Bundle bundle = new Bundle();
                    bundle.putString("packageName", packageName);
                    bundle.putLong("timeStamp", System.currentTimeMillis());
                    bundle.putString("tvEmac", getEthMacAddress());
                    try {
                        PackageInfo p = getPackageManager().getPackageInfo(packageName, 0);
                        bundle.putLong("versionCode", p.versionCode);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    getAnalytics().logEvent("APP_INSTALLED", bundle);
                }
                break;
                case Intent.ACTION_PACKAGE_FULLY_REMOVED:
                case Intent.ACTION_PACKAGE_REMOVED:
                {
                    String packageName = intent.getData().getEncodedSchemeSpecificPart();
                    Bundle bundle = new Bundle();
                    bundle.putString("packageName", packageName);
                    bundle.putLong("timeStamp", System.currentTimeMillis());
                    bundle.putString("tvEmac", getEthMacAddress());

                    getAnalytics().logEvent("APP_UNINSTALLED", bundle);
                }
                break;
            }

        }
    }

    class AnalyticsBr extends BroadcastReceiver{

        private final String profileAction = "tv.cloudwalker.profile.action.SET";
        private final String cdeOpenAction = "tv.cloudwalker.cde.action.OPEN";
        private final String cdeSearchAction = "tv.cloudwalker.cde.action.SEARCH";
        private final String cdeTileClickedAction = "tv.cloudwalker.cde.action.CLICKED";
        private final String appsOpenAction = "tv.cloudwalker.apps.action.APP_OPEN";
        private final String allAppsOpenAction = "tv.cloudwalker.apps.action.OPEN";
        private final String sourceChangedAction = "tv.cloudwalker.apiservice.action.SOURCE";

        public AnalyticsBr(){}

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() == null) return;

            Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();

            switch (intent.getAction())
            {
                case profileAction:
                {
                    Bundle bundle = intent.getBundleExtra("info");
                    getAnalytics().logEvent("USER_SET", bundle);
                }
                break;
                case cdeOpenAction:
                {
                    Bundle bundle = intent.getBundleExtra("info");
                    getAnalytics().logEvent("CDE_OPEN", bundle);
                }
                break;
                case cdeSearchAction:
                {
                    Bundle bundle = intent.getBundleExtra("info");
                    getAnalytics().logEvent("CDE_SEARCH_QUERY", bundle);
                }
                break;
                case cdeTileClickedAction:
                {
                    Bundle bundle = intent.getBundleExtra("info");
                    getAnalytics().logEvent("CDE_TILE_CLICKED", bundle);
                }
                break;
                case appsOpenAction :
                {
                    Bundle bundle = intent.getBundleExtra("info");
                    getAnalytics().logEvent("APP_OPEN", bundle);
                }
                break;
                case allAppsOpenAction:
                {
                    Bundle bundle = intent.getBundleExtra("info");
                    getAnalytics().logEvent("APP_DRAWER_OPEN", bundle);
                }
                break;
                case sourceChangedAction:
                {
                    Bundle bundle = intent.getBundleExtra("info");
                    getAnalytics().logEvent("SOURCE_CHANGED", bundle);
                }
                break;
            }
        }
    }

}





















































