
package fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import tv.cloudwalker.launcher.R;
import utils.OttoBus;

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
        OttoBus.getBus().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        OttoBus.getBus().unregister(this);
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
                            OttoBus.getBus().post("refresh");
                        }else if(errorButtonText.compareToIgnoreCase("tv mode") == 0){
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.MAIN");
                            intent.addCategory("android.intent.category.TV_HOME");
                            intent.putExtra("isLauncherGoToTv", true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }else {
                            getFragmentManager().beginTransaction().remove(ErrorFragment.this).commit();
                        }
                    }
                });
        context = null;
    }
}
