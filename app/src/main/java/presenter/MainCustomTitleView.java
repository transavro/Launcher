package presenter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.leanback.widget.TitleViewAdapter;

import tv.cloudwalker.launcher.CloudwalkerApplication;
import tv.cloudwalker.launcher.R;
import utils.OttoBus;

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

        OttoBus.getBus().register(this);
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
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.MAIN");
                    intent.addCategory("android.intent.category.TV_HOME");
                    intent.putExtra("isLauncherGoToTv", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        view.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(view.getContext(), "Source App not installed", Toast.LENGTH_SHORT).show();
                    }
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
                OttoBus.getBus().post("kids");
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