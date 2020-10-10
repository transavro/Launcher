package presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.leanback.widget.TitleViewAdapter;
import tv.cloudwalker.launcher.CloudwalkerApplication;
import tv.cloudwalker.launcher.R;

import static utils.AppUtils.goToSource;

public class MainCustomTitleView extends RelativeLayout implements TitleViewAdapter.Provider {

    public ImageView badge;
    public ImageView sourceOrb, settingsOrb, mSearchOrbView, kidsOrb, appOrb;

    private final TitleViewAdapter mTitleViewAdapter = new TitleViewAdapter() {
        @Override
        public View getSearchAffordanceView() {
            return mSearchOrbView;
        }


        @Override
        public void setBadgeDrawable(Drawable drawable) {
            MainCustomTitleView.this.setBadgeDrawable(drawable);
        }

        @Override
        public void setOnSearchClickedListener(OnClickListener listener) {
            mSearchOrbView.setOnClickListener(listener);
        }

        @Override
        public void updateComponentsVisibility(int flags) {
            int visibility = (flags & SEARCH_VIEW_VISIBLE) == SEARCH_VIEW_VISIBLE
                    ? View.VISIBLE : View.INVISIBLE;
            mSearchOrbView.setVisibility(visibility);
        }
    };

    public MainCustomTitleView(Context context) {
        this(context, null);
    }

    public MainCustomTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainCustomTitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View root = LayoutInflater.from(context).inflate(R.layout.main_custom_titleview, this);
        mSearchOrbView = root.findViewById(R.id.search_orb);

        sourceOrb = root.findViewById(R.id.source_orb);
        settingsOrb = root.findViewById(R.id.settings_orb);
        kidsOrb = root.findViewById(R.id.kids_orb);
        appOrb = root.findViewById(R.id.app_orb);

        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        badge = root.findViewById(R.id.brandIcon);

        //loading assets from skin
        loadImageAssetsFromSkin((CloudwalkerApplication) context.getApplicationContext());

        sourceOrb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = view.getContext().getPackageManager().getLaunchIntentForPackage("tv.cloudwalker.apiservice");
                if (startIntent != null) {
                    view.getContext().startActivity(startIntent);
                } else {
                    goToSource(view.getContext());
                }
            }
        });

        settingsOrb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchIntent = view.getContext().getPackageManager().getLaunchIntentForPackage("com.cvte.tv.androidsetting");
                if (launchIntent == null) {
                    launchIntent = view.getContext().getPackageManager().getLaunchIntentForPackage("com.cvte.settings");
                    if (launchIntent == null) {
                        launchIntent = new Intent(Settings.ACTION_SETTINGS);
                    }
                }
                view.getContext().startActivity(launchIntent);
            }
        });

        kidsOrb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post("kids");
            }
        });

        appOrb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    v.getContext().startActivity(v.getContext().getPackageManager().getLaunchIntentForPackage("tv.cloudwalker.apps"));
                } catch (Exception e) {
                    Toast.makeText(v.getContext(), "All Apps app not installed ", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    // present of subscribe annotation is mandatory for GreenBot lib, if u r registering n unregistering.
//    https://stackoverflow.com/questions/35874055/eventbus-subscriber-class-and-its-super-classes-have-no-public-methods-with-th/52758471
    @Subscribe(threadMode =  ThreadMode.MAIN)
    public void onMessageEvent(KeyEvent event){}


    private void loadImageAssetsFromSkin(CloudwalkerApplication application){
        try{

            sourceOrb.setImageDrawable(application.getDrawable("title_source"));
            sourceOrb.setBackground(application.getDrawable("orb_focuser"));

            settingsOrb.setImageDrawable(application.getDrawable("title_settings"));
            settingsOrb.setBackground(application.getDrawable("orb_focuser"));

            kidsOrb.setImageDrawable(application.getDrawable("title_kids"));
            kidsOrb.setBackground(application.getDrawable("orb_focuser"));

            appOrb.setImageDrawable(application.getDrawable("title_apps"));
            appOrb.setBackground(application.getDrawable("orb_focuser"));

            mSearchOrbView.setImageDrawable(application.getDrawable("title_search"));
            mSearchOrbView.setBackground(application.getDrawable("orb_focuser"));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setBadgeDrawable(Drawable drawable) {
        if (drawable != null) {
            badge.setImageDrawable(drawable);
        }
    }


    @Override
    public View focusSearch(View focused, int direction) {
        View nextFoundFocusableViewInLayout = null;
        int nextFoundFocusableViewInLayoutId = -1;
        switch (direction) {
            case View.FOCUS_LEFT:
                nextFoundFocusableViewInLayoutId = focused.getNextFocusLeftId();
                break;
            case View.FOCUS_RIGHT:
                nextFoundFocusableViewInLayoutId = focused.getNextFocusRightId();
                break;
            case View.FOCUS_DOWN:
                nextFoundFocusableViewInLayoutId = focused.getNextFocusDownId();
                break;
            case View.FOCUS_UP:
                nextFoundFocusableViewInLayoutId = focused.getNextFocusUpId();
                break;

        }
        if (nextFoundFocusableViewInLayoutId != -1) {
            nextFoundFocusableViewInLayout = findViewById(nextFoundFocusableViewInLayoutId);
        }
        if (nextFoundFocusableViewInLayout != null && nextFoundFocusableViewInLayout.isFocusable()) {
            return nextFoundFocusableViewInLayout;
        } else {
            return super.focusSearch(focused, direction);
        }
    }


    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        return mSearchOrbView.requestFocus() || super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
    }

    @Override
    public TitleViewAdapter getTitleViewAdapter() {
        return mTitleViewAdapter;
    }

}