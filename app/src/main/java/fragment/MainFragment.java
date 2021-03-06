package fragment;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import com.google.android.youtube.player.YouTubeIntents;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import model.MovieResponse;
import model.MovieRow;
import model.MovieTile;
import presenter.CardPresenter;
import tv.cloudwalker.launcher.CloudwalkerApplication;
import tv.cloudwalker.launcher.DetailActivity;
import tv.cloudwalker.launcher.MainActivity;
import utils.NetworkUtils;
import utils.OttoBus;

public class MainFragment extends BrowseSupportFragment {

    private static final String TAG = "MainFragment";
    private BroadcastReceiver refreshBR = new RefreshBR();
    private IntentFilter mIntentFilter = new IntentFilter("tv.cloudwalker.launcher.REFRESH");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BackgroundManager.getInstance(getActivity()).attach(getActivity().getWindow());
        setupUIElements();
        setupEventListeners();
        loadRows();
    }

    private void setupUIElements() {
        setBadgeDrawable(((CloudwalkerApplication) getActivity().getApplication()).getDrawable("logo"));
        setHeadersState(((CloudwalkerApplication) getActivity().getApplication()).getInteger("has_fastlane"));
        setHeadersTransitionOnBackEnabled(true);



        setBrandColor(((CloudwalkerApplication) getActivity().getApplication()).getColor("fastlane_color"));
    }

    private void setupEventListeners() {
        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    view.getContext().startActivity(view.getContext().getPackageManager().getLeanbackLaunchIntentForPackage("tv.cloudwalker.search"));
                } catch (Exception e) {
                    Toast.makeText(view.getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        setOnItemViewSelectedListener(new OnItemViewSelectedListener() {
            @Override
            public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                logAnalyticsEvent(row.getHeaderItem().getName(), item, "TILE_SELECTED");
            }
        });


        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                if (item instanceof MovieTile) {
                    if (((MovieTile) item).isDetailPage()) {

                        //logi Analytics event
                        logAnalyticsEvent( row.getHeaderItem().getName(), item, "TILE_CLICKED");

                        // Detail Page Stuff
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(MovieTile.class.getSimpleName(), (MovieTile) item);
                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        intent.putExtra("tileID", ((MovieTile) item).getTid());
                        intent.putExtra(MovieTile.class.getSimpleName(), bundle);
                        intent.putExtra("background", ((MovieTile) item).getBackground());
                        startActivityForResult(intent, 10);

                    } else {
                        handleTileClick((MovieTile) item, itemViewHolder.view.getContext());
                    }
                }
                return;
            }
        });
    }

    private void logAnalyticsEvent(String rowName, Object item , String eventName) {
        if(item instanceof MovieTile)
        {
            boolean found = false;
            for(int rowIndex  = 0 ; rowIndex < getAdapter().size() ; rowIndex++ )
            {
                if(found) break;

                ArrayObjectAdapter rowAdap = (ArrayObjectAdapter) ((ListRow) getAdapter().get(rowIndex)).getAdapter();
                for(int tileIndex = 0; tileIndex < rowAdap.size() ; tileIndex++ )
                {
                    MovieTile movieTile = (MovieTile) rowAdap.get(tileIndex);
                    if(movieTile.getTitle().equals(((MovieTile) item).getTitle()))
                    {
                        //FireBase Analytics Stuff
                        Bundle fireBundle = new Bundle();
                        fireBundle.putString("TILE_ID", ((MovieTile) item).getTid());
                        fireBundle.putLong("ROW_INDEX", rowIndex);
                        fireBundle.putLong("TILE_INDEX", tileIndex);
                        fireBundle.putString("TILE_TITLE", ((MovieTile) item).getTitle());
                        fireBundle.putString("TILE_SOURCE", ((MovieTile) item).getSource());
                        fireBundle.putString("TILE_ROW_NAME", rowName);
                        if (((MovieTile) item).getYear() != null && !((MovieTile) item).getYear().isEmpty())
                            fireBundle.putString("TILE_YEAR", ((MovieTile) item).getYear());

                        if (((MovieTile) item).getCast() != null && ((MovieTile) item).getCast().size() > 1)
                            fireBundle.putString("TILE_CAST",android.text.TextUtils.join(",", ((MovieTile) item).getCast()));

                        if (((MovieTile) item).getDirector() != null && ((MovieTile) item).getDirector().size() > 1)
                            fireBundle.putString("TILE_DIRECTOR", android.text.TextUtils.join(",", ((MovieTile) item).getDirector()));

                        if (((MovieTile) item).getGenre() != null && ((MovieTile) item).getGenre().size() > 1)
                            fireBundle.putString("TILE_GENRE", android.text.TextUtils.join(",", ((MovieTile) item).getGenre()));

                        ((CloudwalkerApplication) Objects.requireNonNull(getActivity()).getApplication()).getAnalytics().logEvent(eventName, fireBundle);
                        fireBundle = null;
                        found = true;
                        break;
                    }
                }
            }
        }
    }

    @Subscribe
    public void GetRefreshTrigger(String trigger) {
        if (trigger.equals("refresh")) {
            loadData();
        }
    }


    @Override
    public void onStart() {
        if (refreshBR != null && mIntentFilter != null)
            getActivity().registerReceiver(refreshBR, mIntentFilter);
        super.onStart();
    }

    @Override
    public void onStop() {
        if (refreshBR != null)
            getActivity().unregisterReceiver(refreshBR);
        super.onStop();
    }


    @Override
    public void onResume() {
        BackgroundManager.getInstance(getActivity()).setColor(((CloudwalkerApplication) getActivity().getApplication()).getColor("main_fragment_bg_color"));
        OttoBus.getBus().register(this);
        super.onResume();
    }


    @Override
    public void onPause() {
        OttoBus.getBus().unregister(this);
        super.onPause();
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
            startActivity(appStoreIntent);
            return;
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
            startActivityForResult(intent, 10);
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

    private void loadData() {

        try {
            CardPresenter cardPresenter = new CardPresenter();
            ListRowPresenter listRowPresenter = new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_NONE, false);
            listRowPresenter.enableChildRoundedCorners(true);
            listRowPresenter.setKeepChildForeground(true);
            listRowPresenter.setShadowEnabled(false);
            listRowPresenter.setRecycledPoolSize(cardPresenter, 20);
            listRowPresenter.setSelectEffectEnabled(false);
            ArrayObjectAdapter primeAdapter = new ArrayObjectAdapter(listRowPresenter);
            for (MovieRow movieRow : readJSONFromAsset().getRows()) {
                ArrayObjectAdapter rowAdapter = new ArrayObjectAdapter(cardPresenter);
                for (MovieTile movieTile : movieRow.getRowItems()) {
                    movieTile.setRowLayout(movieRow.getRowLayout());
                    rowAdapter.add(movieTile);
                }
                HeaderItem header = new HeaderItem(movieRow.getRowIndex(), movieRow.getRowHeader());
                primeAdapter.add(new ListRow(header, rowAdapter));
            }
            setAdapter(primeAdapter);
            return;
        } catch (Exception e) {
            ((MainActivity) getActivity()).loadErrorFragment("Error while loading data ==> " + e.getLocalizedMessage(), "Back");
            return;
        }


//        ApiClient
//                .getClient(getActivity())
//                .create(ApiInterface.class)
//                .getHomeScreenData()
//                .enqueue(new Callback<MovieResponse>() {
//                    @Override
//                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
//                        if (response.code() != 200) {
//                            loadErrorFragment("Server response code ==> " + response.code(), "Back");
//                            return;
//                        }
//
//                        try {
//                            CardPresenter cardPresenter = new CardPresenter();
//                            ListRowPresenter listRowPresenter = new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_NONE, false);
//                            listRowPresenter.enableChildRoundedCorners(true);
//                            listRowPresenter.setKeepChildForeground(true);
//                            listRowPresenter.setShadowEnabled(false);
//                            listRowPresenter.setRecycledPoolSize(cardPresenter, 20);
//                            listRowPresenter.setSelectEffectEnabled(false);
//                            ArrayObjectAdapter primeAdapter = new ArrayObjectAdapter(listRowPresenter);
//                            for (MovieRow movieRow : response.body().getRows()) {
//                                ArrayObjectAdapter rowAdapter = new ArrayObjectAdapter(cardPresenter);
//                                for (MovieTile movieTile : movieRow.getRowItems()) {
//                                    movieTile.setRowLayout(movieRow.getRowLayout());
//                                    rowAdapter.add(movieTile);
//                                }
//                                HeaderItem header = new HeaderItem(movieRow.getRowIndex(), movieRow.getRowHeader());
//                                primeAdapter.add(new ListRow(header, rowAdapter));
//                            }
//                            setAdapter(primeAdapter);
//                            return;
//                        } catch (Exception e) {
//                            loadErrorFragment("Error while loading data ==> " + e.getLocalizedMessage(), "Back");
//                            return;
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<MovieResponse> call, Throwable t) {
//                        loadErrorFragment("Server error ==> " + t.getLocalizedMessage(), "Back");
//                    }
//                });

    }


    private MovieResponse readJSONFromAsset() {
        try {
            InputStream is = getActivity().getAssets().open("banner.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            Gson gson = new Gson();
            return gson.fromJson(json, MovieResponse.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void loadRows() {
        if (NetworkUtils.getConnectivityStatus(getActivity()) == NetworkUtils.TYPE_NOT_CONNECTED) {
            ((MainActivity) getActivity()).loadErrorFragment("Not Connected to Internet", "Refresh");
            return;
        }
        loadData();
    }


    private class RefreshBR extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            GetRefreshTrigger("refresh");
        }
    }
}
