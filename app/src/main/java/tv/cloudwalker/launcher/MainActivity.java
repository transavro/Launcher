package tv.cloudwalker.launcher;

import android.content.ComponentCallbacks2;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;

import com.bumptech.glide.Glide;

import androidx.fragment.app.FragmentActivity;
import fragment.ErrorFragment;
import fragment.MainFragment;

import static utils.AppUtils.isPackageInstalled;

public class MainActivity extends FragmentActivity {

    private ErrorFragment mErrorFragment;
    private MainFragment mainFragment;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainFragment = new MainFragment();
        startKeyDetector();
        loadMainFragment();
//        if(checkIfCWSuitIsSetup()){
//            loadMainFragment();
//        }else {
//            loadErrorFragment("Cloudwalker Suit is incomplete. Make sure you have CloudTV Skin, Apps, Updater, Market, Source, Search apps installed.", "TV Mode");
//        }
    }



    @Override
    public void onLowMemory() {
        try {
            Log.d(TAG, "onLowMemory: ");
            Glide.get(this).onTrimMemory(ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL);
        }catch (Exception e){
            Log.e(TAG, "onTrimMemory: ",e);
        }
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        try {
            Log.d(TAG, "onTrimMemory: ");
            Glide.get(this).onTrimMemory(ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL);
        }catch (Exception e){
            Log.e(TAG, "onTrimMemory: ",e);
        }
        super.onTrimMemory(level);
    }

    @Override
    protected void onResume() {
        Drawable bg = null;
        try {
            bg = ((CloudwalkerApplication) getApplication()).getDrawable("launcher_bg_gradient");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bg != null)
            findViewById(R.id.main_browse_fragment).setBackground(bg);
        super.onResume();
    }


    @Override
    protected void onPause() {
        try {
            Glide.get(this).onTrimMemory(ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN);
        }catch (Exception e){
            Log.e(TAG, "onPause: ",e);
        }
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if(((CloudwalkerApplication)getApplicationContext()).getTvInfo().getBoard().contains("ATM30") &&
                    ((event.getFlags() & KeyEvent.FLAG_LONG_PRESS) == KeyEvent.FLAG_LONG_PRESS) ){
                return true;
            }
            return super.onKeyDown(keyCode, event);
        } catch (Exception e) {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void startKeyDetector(){
        //settings accesssible service
        String value = BuildConfig.APPLICATION_ID+"/"+BuildConfig.APPLICATION_ID +"."+ AccessibilityKeyDetector.class.getSimpleName();
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, value);
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, "1");
    }

    private void loadMainFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_browse_fragment, mainFragment, MainFragment.class.getSimpleName())
                .commit();
    }

    private boolean checkIfCWSuitIsSetup() {
        boolean reason;
        reason = isPackageInstalled("tv.cloudwalker.apps", getPackageManager());
        if (!reason) {
            return reason;
        }
        reason = isPackageInstalled("tv.cloudwalker.search", getPackageManager());
        if (!reason) {
            return reason;
        }
        reason = isPackageInstalled("tv.cloudwalker.skin", getPackageManager());
        if (!reason) {
            return reason;
        }
        reason = isPackageInstalled("tv.cloudwalker.market", getPackageManager());
        if (!reason) {
            return reason;
        }
        reason = isPackageInstalled("tv.cloudwalker.apiservice", getPackageManager());
        if (!reason) {
            return reason;
        }
        reason = isPackageInstalled("tv.cloudwalker.updater", getPackageManager());
        if (!reason) {
            return reason;
        }
        return reason;
    }

    public void loadErrorFragment(String reason, String btnText) {
        if (mErrorFragment == null) {
            mErrorFragment = new ErrorFragment();
            mErrorFragment.setErrorContent(reason, btnText, this);
        }
        getSupportFragmentManager().beginTransaction().add(R.id.main_browse_fragment, mErrorFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if(mainFragment != null){
            mainFragment.goToTop();
        }
    }
}
