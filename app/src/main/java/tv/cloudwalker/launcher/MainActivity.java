package tv.cloudwalker.launcher;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import fragment.ErrorFragment;
import fragment.MainFragment;

public class MainActivity extends FragmentActivity {

    private ErrorFragment mErrorFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(checkIfCWSuitIsSetup()){
            loadMainFragment();
        }else {
            loadErrorFragment("Cloudwalker Suit is incomplete.", "TV Mode");
        }
    }

    private void loadMainFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_browse_fragment, new MainFragment(), MainFragment.class.getSimpleName())
                .commit();
    }

    private boolean checkIfCWSuitIsSetup() {
        boolean reason = false;
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
        reason = isPackageInstalled("com.replete.cwappstore", getPackageManager());
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

    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public String getSystemProperty(String key) {
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
    public void onBackPressed() {

    }
}
