
package fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import tv.cloudwalker.launcher.R;

public class ErrorFragment extends androidx.leanback.app.ErrorSupportFragment {
    private static final String TAG = "ErrorFragment";
    private static final boolean TRANSLUCENT = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.app_name));
    }

    @Override
    public void onResume() {
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        super.onResume();
    }

    // present of subscribe annotation is mandatory for GreenBot lib, if u r registering n unregistering.
//    https://stackoverflow.com/questions/35874055/eventbus-subscriber-class-and-its-super-classes-have-no-public-methods-with-th/52758471
    @Subscribe(threadMode =  ThreadMode.MAIN)
    public void onMessageEvent(KeyEvent event){}

    @Override
    public void onPause() {
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        super.onPause();
    }



    public void setErrorContent(String error_fragment_message, final String errorButtonText, Context context) {
        setImageDrawable(context.getResources().getDrawable(R.drawable.lb_ic_sad_cloud));
        setMessage(error_fragment_message);
        setDefaultBackground(TRANSLUCENT);

        setButtonText(errorButtonText);
        setButtonClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if(errorButtonText.compareToIgnoreCase("refresh") == 0){
                            EventBus.getDefault().post("refresh");
                            getFragmentManager().beginTransaction().remove(ErrorFragment.this).commit();
                        }else if(errorButtonText.compareToIgnoreCase("tv mode") == 0){
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.MAIN");
                            intent.addCategory("android.intent.category.TV_HOME");
                            intent.putExtra("isLauncherGoToTv", true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            try {
                                startActivity(intent);
                                getFragmentManager().beginTransaction().remove(ErrorFragment.this).commit();
                            }catch (Exception e){
                                Toast.makeText(arg0.getContext(), "Unable to open Tv Mode.", Toast.LENGTH_SHORT).show();
//                                Intent appsIntent = arg0.getContext().getPackageManager().getLaunchIntentForPackage("tv.cloudwalker.apps");
//                                if(appsIntent == null){
//                                    appsIntent = arg0.getContext().getPackageManager().getLeanbackLaunchIntentForPackage("tv.cloudwalker.apps");
//                                }
//                                if(appsIntent != null)
//                                    startActivity(intent);
                            }
                        }
                    }
                });
        context = null;
    }
}
