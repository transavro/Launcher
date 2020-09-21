package tv.cloudwalker.launcher;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import fragment.ErrorFragment;
import fragment.MainFragment;

import static utils.AppUtils.isPackageInstalled;

public class MainActivity extends FragmentActivity {

    private ErrorFragment mErrorFragment;
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainFragment = new MainFragment();
        loadMainFragment();


//        if(checkIfCWSuitIsSetup()){
//            loadMainFragment();
//        }else {
//            loadErrorFragment("Cloudwalker Suit is incomplete.", "TV Mode");
//        }

    }

    private void loadMainFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_browse_fragment, mainFragment, MainFragment.class.getSimpleName())
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
        reason = isPackageInstalled("tv.cloudwalker.market", getPackageManager());
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

    }
}
