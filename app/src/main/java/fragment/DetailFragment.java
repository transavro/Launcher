package fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.DetailsSupportFragment;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DetailsOverviewLogoPresenter;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnActionClickedListener;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.SparseArrayObjectAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.youtube.player.YouTubeIntents;

import model.MovieTile;
import presenter.MovieDetailsDescriptionPresenter;
import tv.cloudwalker.launcher.CloudwalkerApplication;
import tv.cloudwalker.launcher.R;

import static androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter.ALIGN_MODE_START;

public class DetailFragment extends DetailsSupportFragment implements OnItemViewClickedListener {

    public static final String TAG = DetailFragment.class.getSimpleName();
    private static final int PLAY_VIDEO = 0;
    private MovieTile movieTile;
    private ArrayObjectAdapter mAdapter;
    private DetailsOverviewRow mDetailsOverviewRow;

    private SimpleTarget<Drawable> mGlideDrawableSimpleTarget = new SimpleTarget<Drawable>() {
        @Override
        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
            if (mDetailsOverviewRow != null && resource != null) {
                mDetailsOverviewRow.setImageDrawable(resource.getCurrent());
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getActivity().getIntent().getParcelableExtra(MovieTile.class.getSimpleName());
        movieTile = bundle.getParcelable(MovieTile.class.getSimpleName());
        if (movieTile != null) {
            loadRows();
        }
    }

    private void loadRows() {
        setUpAdapter();
        setUpDetailsOverviewRow();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cleanUp();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void cleanUp() {
        movieTile = null;
        mDetailsOverviewRow = null;
        mAdapter = null;
        mGlideDrawableSimpleTarget = null;
    }

    private void setUpAdapter() {
        FullWidthDetailsOverviewRowPresenter mFullWidthMovieDetailsPresenter = new FullWidthDetailsOverviewRowPresenter(new MovieDetailsDescriptionPresenter(), new DetailsOverviewLogoPresenter());
        mFullWidthMovieDetailsPresenter.setActionsBackgroundColor(((CloudwalkerApplication)getActivity().getApplication()).getColor("detail_action_bg"));
        mFullWidthMovieDetailsPresenter.setBackgroundColor(((CloudwalkerApplication)getActivity().getApplication()).getColor("detail_bg"));
        mFullWidthMovieDetailsPresenter.setAlignmentMode(ALIGN_MODE_START);
        mFullWidthMovieDetailsPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
                int actionId = (int) action.getId();
                if (actionId == PLAY_VIDEO) {
                    ((CloudwalkerApplication)getActivity().getApplication()).setHeroMovieTile(movieTile);
                    handleTileClick(movieTile, getContext());
                }
            }
        });

        try {
            ClassPresenterSelector classPresenterSelector = new ClassPresenterSelector();
            classPresenterSelector.addClassPresenter(DetailsOverviewRow.class, mFullWidthMovieDetailsPresenter);
            ListRowPresenter listRowPresenter = new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_NONE, false);
            listRowPresenter.enableChildRoundedCorners(true);
            listRowPresenter.setKeepChildForeground(true);
            listRowPresenter.setShadowEnabled(false);
            listRowPresenter.setSelectEffectEnabled(false);
            classPresenterSelector.addClassPresenter(ListRow.class, listRowPresenter);
            mAdapter = new ArrayObjectAdapter(classPresenterSelector);
            setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpDetailsOverviewRow() {
        mDetailsOverviewRow = new DetailsOverviewRow(movieTile);
        mAdapter.add(mDetailsOverviewRow);
        mDetailsOverviewRow.setItem(this.movieTile);
        if (TextUtils.isEmpty(movieTile.getPortrait())) {
            loadImage(movieTile.getPoster());
        } else {
            loadImage(movieTile.getPortrait());
        }
        setUpActionItems();
        notifyDetailsChanged();
    }

    private void setUpActionItems() {
        SparseArrayObjectAdapter adapter = new SparseArrayObjectAdapter(new Presenter() {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_button_layout, parent, false);
                return new ViewHolder(v);
            }

            @Override
            public void onBindViewHolder(ViewHolder viewHolder, Object item) {
                Button button = (Button) viewHolder.view;
                Action action = (Action) item;
                button.setText(action.getLabel1());
            }

            @Override
            public void onUnbindViewHolder(ViewHolder viewHolder) {

            }
        });

        adapter.set(PLAY_VIDEO, new Action(PLAY_VIDEO, "PLAY", null, null));
        mDetailsOverviewRow.setActionsAdapter(adapter);
    }

    private void loadImage(String url) {
        Glide.with(getActivity())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.movie)
                .skipMemoryCache(true)
                .into(mGlideDrawableSimpleTarget);
    }


    private void notifyDetailsChanged() {
        mDetailsOverviewRow.setItem(this.movieTile);
        int index = mAdapter.indexOf(mDetailsOverviewRow);
        mAdapter.notifyArrayItemRangeChanged(index, 1);
    }


    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {

    }


    private void handleTileClick(MovieTile contentTile, Context context) {
        //check if the package is there or not
        if (contentTile.getPackageName().contains("youtube")) {
            contentTile.setPackageName("com.google.android.youtube.tv");
        }
        if (!isPackageInstalled(contentTile.getPackageName(), context.getPackageManager())) {
            Toast.makeText(context, contentTile.getSource() + " app is not installed. Opening CloudTV Appstore...", Toast.LENGTH_SHORT).show();
            Intent appStoreIntent = getActivity().getPackageManager().getLeanbackLaunchIntentForPackage("com.replete.cwappstore");
            if (appStoreIntent == null) return;
            appStoreIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(appStoreIntent);            return;
        }
        if (contentTile.getTarget().isEmpty()) {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(contentTile.getPackageName());
            if (intent == null) {
                intent = context.getPackageManager().getLeanbackLaunchIntentForPackage(contentTile.getPackageName());
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, 10);
            return;
        }

        //if package is installed
        //check its an youtube
        if (contentTile.getPackageName().contains("youtube")) {
            if (contentTile.getTarget().get(0).startsWith("PL")) {
                startYoutube("OPEN_PLAYLIST", context, contentTile.getTarget().get(0));
            } else if (contentTile.getTarget().get(0).startsWith("UC")) {
                startYoutube("OPEN_CHANNEL", context, contentTile.getTarget().get(0));
            } else {
                startYoutube("PLAY_VIDEO", context, contentTile.getTarget().get(0));
            }

        } else if (contentTile.getPackageName().contains("hotstar")) {
            // if hotstar
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contentTile.getTarget().get(0)));
                intent.setPackage(contentTile.getPackageName());
                startActivityForResult(intent, 10);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Intent intent = context.getPackageManager().getLeanbackLaunchIntentForPackage(contentTile.getPackageName());
                if (contentTile.getTarget().contains("https")) {
                    intent.setData(Uri.parse(contentTile.getTarget().get(0).replace("https://www.hotstar.com", "hotstar://content")));
                } else {
                    intent.setData(Uri.parse(contentTile.getTarget().get(0).replace("http://www.hotstar.com", "hotstar://content")));
                }
                startActivityForResult(intent, 0);
            }
        } else {
            // if other app
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contentTile.getTarget().get(0)));
            intent.setPackage(contentTile.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent , 10);
        }
    }

    private void startYoutube(String type, Context mActivity, String target) {
        if (type.compareToIgnoreCase("PLAY_VIDEO") == 0 || type.compareToIgnoreCase("CWYT_VIDEO") == 0) {
            Intent intent = YouTubeIntents.createPlayVideoIntentWithOptions(mActivity, target, true, true);
            intent.setPackage("com.google.android.youtube.tv");
            startActivityForResult(intent, 10);
        } else if (type.compareToIgnoreCase("OPEN_PLAYLIST") == 0) {
            Intent intent = YouTubeIntents.createOpenPlaylistIntent(mActivity, target);
            intent.setPackage("com.google.android.youtube.tv");
            intent.putExtra("finish_on_ended", true);
            startActivityForResult(intent, 10);
        } else if (type.compareToIgnoreCase("PLAY_PLAYLIST") == 0 || type.compareToIgnoreCase("CWYT_PLAYLIST") == 0) {
            Intent intent = YouTubeIntents.createPlayPlaylistIntent(mActivity, target);
            intent.setPackage("com.google.android.youtube.tv");
            intent.putExtra("finish_on_ended", true);
            startActivityForResult(intent, 10);
        } else if (type.compareToIgnoreCase("OPEN_CHANNEL") == 0) {
            Intent intent = YouTubeIntents.createChannelIntent(mActivity, target);
            intent.setPackage("com.google.android.youtube.tv");
            intent.putExtra("finish_on_ended", true);
            startActivityForResult(intent, 10);
        } else if (type.compareToIgnoreCase("OPEN_USER") == 0) {
            Intent intent = YouTubeIntents.createUserIntent(mActivity, target);
            startActivityForResult(intent, 10);
        } else if (type.compareToIgnoreCase("OPEN_SEARCH") == 0) {
            Intent intent = YouTubeIntents.createSearchIntent(mActivity, target);
            startActivityForResult(intent, 10);
        }
        return;
    }

    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
