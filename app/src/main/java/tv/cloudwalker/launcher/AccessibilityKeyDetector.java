package tv.cloudwalker.launcher;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import utils.PlayOnTv;


//put secure enabled_accessibility_services tv.cloudwalker.cwnxt.launcher.com/tv.cloudwalker.cwnxt.launcher.tv.cloudwalker.cwnxt.launcher.AccessibilityKeyDetector

public class AccessibilityKeyDetector extends AccessibilityService {

    private int infoCounter = 0;
    private Timer timer;
    private long mLastClickedTime;
    private static final String TAG = "AccessibilityKeyDetecto";
    private PlayOnTv playOnTv;


    @Override
    public boolean onKeyEvent(KeyEvent event) {
        if( (event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() < 19 || event.getKeyCode() > 22) ) {
            Log.d(TAG, "onKeyEvent: "+event.getKeyCode());
            CloudwalkerApplication cwApplication = (CloudwalkerApplication) getApplicationContext();
            HashMap<String, String> keycodeMap = cwApplication.getKeycodeMap();
            if (keycodeMap.size() > 0 && keycodeMap.containsKey(String.valueOf(event.getKeyCode()))  && !cwApplication.checkIfTheClassIsInFront("com.cvte.tv")) {
                handelingKeyEvents(keycodeMap.get(String.valueOf(event.getKeyCode())));
            }
        }
        return super.onKeyEvent(event);
    }

    public PlayOnTv getPlayOnTv() {
        if(playOnTv == null){
            playOnTv = new PlayOnTv(getApplicationContext());
        }
        return playOnTv;
    }

    private void handelingKeyEvents(String value) {
        Log.d(TAG, "handelingKeyEvents: "+value);
        value = value.toLowerCase();
        switch (value) {
            case "cde": {
                try {
                    Intent intent = getPackageManager().getLeanbackLaunchIntentForPackage("tv.cloudwalker.search");
                    if(intent != null){
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case "info": {
                infoCounter++;
                if (infoCounter > 15) {
                    infoCounter = 0;
                    try {
                        Intent intent = new Intent();
                        intent.setClassName("tv.cloudwalker.skin", "tv.cloudwalker.skin.DeviceInfoActivity");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                if(timer != null && infoCounter == 10) {
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            infoCounter = 0;
                        }
                    }, 5000);
                }
            }
            break;
            case "allapps": {
                try {
                    Intent intent = getPackageManager().getLeanbackLaunchIntentForPackage("tv.cloudwalker.apps");
                    if(intent != null){
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case "help": {
                try {
                    Intent intent = new Intent();
                    intent.setClassName("tv.cloudwalker.skin", "tv.cloudwalker.skin.TermsActivity");
                    intent.putExtra("tag", "help");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case "profile": {
                //TODO
            }
            break;
            case "refresh": {
                long lastTimeClicked = mLastClickedTime;
                long now = System.currentTimeMillis();
                mLastClickedTime = now;
                long MIN_DELAY_MS = 10000;
                if (now - lastTimeClicked > MIN_DELAY_MS) {
                    Log.d(TAG, "handelingKeyEvents: REFRESHING **********************");
                    String refresh = "refresh";
                    EventBus.getDefault().post(refresh);
                }
            }
            break;
            case "source": {
                long lastTimeClicked = mLastClickedTime;
                long now = System.currentTimeMillis();
                mLastClickedTime = now;
                long MIN_DELAY_MS = 10000;
                if (now - lastTimeClicked > MIN_DELAY_MS) {
                    Intent startIntent = getPackageManager().getLaunchIntentForPackage("tv.cloudwalker.apiservice");
                    startActivity(startIntent);
                }
            }
            break;
            default: {
                getPlayOnTv().trigger(value, "");
            }
        }
    }


    // present of subscribe annotation is mandatory for GreenBot lib, if u r registering n unregistering.
//    https://stackoverflow.com/questions/35874055/eventbus-subscriber-class-and-its-super-classes-have-no-public-methods-with-th/52758471
    @Subscribe(threadMode =  ThreadMode.MAIN)
    public void onMessageEvent(KeyEvent event){}


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        return START_STICKY;
    }

    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "onServiceConnected: ");
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        timer = new Timer();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {}


    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt: ");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }
}
