package fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import api.ApiClient;
import api.ApiInterface;
import model.MovieResponse;
import model.MovieTile;
import model.SettingsModel;
import okhttp3.ResponseBody;
import presenter.CharacterCardPresenter;
import presenter.CwCardPresenter;
import presenter.TvPresenter;
import receiver.NetworkChangeReceiver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tv.cloudwalker.launcher.CloudwalkerApplication;
import tv.cloudwalker.launcher.DetailActivity;
import tv.cloudwalker.launcher.MainActivity;
import utils.PlayOnTv;

public class MainFragment extends BrowseSupportFragment implements NetworkChangeReceiver.NetworkConnectivityInterface, OnItemViewClickedListener, OnItemViewSelectedListener {

    private PlayOnTv playOnTv;
    private NetworkChangeReceiver ncr;
    private TvPresenter tvPresenter;
    private static final String TAG = "MainFragment";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setupUIElements();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setupEventListeners();
        ncr = new NetworkChangeReceiver();
        Log.d(TAG, "onCreate: ");
        if (ncr != null) {
            ncr.addListener(this);
            Objects.requireNonNull(getActivity()).registerReceiver(ncr, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }




    private void setupUIElements() throws Exception {
        setBadgeDrawable(((CloudwalkerApplication) Objects.requireNonNull(getActivity()).getApplication()).getDrawable("title_logo"));
        setHeadersState(((CloudwalkerApplication) Objects.requireNonNull(getActivity()).getApplication()).getInteger("has_fastlane"));
        setHeadersTransitionOnBackEnabled(true);
        setBrandColor(((CloudwalkerApplication) Objects.requireNonNull(getActivity()).getApplication()).getColor("fastlane_color"));
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
        setOnItemViewSelectedListener(this);
        setOnItemViewClickedListener(this);
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

                        ((CloudwalkerApplication) Objects.requireNonNull(Objects.requireNonNull(getActivity())).getApplication()).getAnalytics().logEvent(eventName, fireBundle);
                        found = true;
                        break;
                    }
                }
            }
        }
    }

    @Subscribe(threadMode =  ThreadMode.MAIN)
    public void onMessageEvent(String trigger) {
        Log.d(TAG, "GetRefreshTrigger: ######### refresh");
        if (trigger.equals("refresh")) {
            Log.d(TAG, "GetRefreshTrigger: %%%%%%%%%%%% refresh");
            loadData(false);
        } else if (trigger.equals("kids")) {
            loadData(true);
        }
    }

    @Override
    public void onDestroy() {
        if(tvPresenter != null){
            tvPresenter.tearDownTvApi();
        }
        if (ncr != null) {
            ncr.removeListener(this);
            Objects.requireNonNull(getActivity()).unregisterReceiver(ncr);
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        try {
            BackgroundManager.getInstance(Objects.requireNonNull(getActivity())).setColor(((CloudwalkerApplication) Objects.requireNonNull(getActivity()).getApplication()).getColor("main_fragment_bg_color"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        if(tvPresenter != null && tvPresenter.isFocusedFlag){
            tvPresenter.unMuteAndPipPriviewWindow();
        }
        super.onResume();
    }


    @Override
    public void onPause() {
        if(tvPresenter != null){
            tvPresenter.mutePriviewWindow();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        super.onStop();
    }

    public PlayOnTv getPlayOnTv() {
        if(playOnTv == null){
            playOnTv = new PlayOnTv(getActivity());
        }
        return playOnTv;
    }


    private void loadData(boolean kidsafe) {

//       tmp
//        AppUtils appUtils = new AppUtils();
//        MovieResponse movieResponse = appUtils.readJSONFromAsset(getActivity(), "data.json");
//        //load Movie Response
//        loadMovieResponse(movieResponse);
//        return;


//        api call
        ApiClient
                .getClient(Objects.requireNonNull(getActivity()))
                .create(ApiInterface.class)
                .getHomeScreenData(kidsafe)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull final Response<ResponseBody> response) {
                        Log.d(TAG, "onResponse: "+response.code());
                        MovieResponse movieResponse;
                        if (response.code() != 200) {
                            //reading from cache
                            movieResponse = readCatsFromCache();
                        } else {
                            //writing to cache
                            movieResponse = writeCatsInCache(response.body());
                        }

                        //if not found in cache as well show error msg
                        if (movieResponse == null && (getActivity()) != null) {
                            ((MainActivity)getActivity()).loadErrorFragment("Some thing when wrong ==> " + response.code(), "Back");
                            return;
                        }
                        //load Movie Response
                        loadMovieResponse(movieResponse);
                    }

                    @Override
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull final Throwable t) {
                        if ((getActivity()) != null)
                            ((MainActivity) getActivity()).loadErrorFragment("Server error ==> " + t.getLocalizedMessage(), "Back");
                    }
                });
    }


    private void loadMovieResponse(MovieResponse movieResponse){
        try {

            CwCardPresenter cardPresenter = new CwCardPresenter();

            ListRowPresenter listRowPresenter =
                    new ListRowPresenter(((CloudwalkerApplication) Objects.requireNonNull(getActivity()).getApplication()).getInteger("zoom_factor"),
                            ((CloudwalkerApplication) Objects.requireNonNull(getActivity()).getApplication()).getBool("focusDimmer"));

            listRowPresenter.enableChildRoundedCorners(true);
            listRowPresenter.setKeepChildForeground(true);
            listRowPresenter.setShadowEnabled(false);
//            listRowPresenter.setRecycledPoolSize(cardPresenter, 20);
            listRowPresenter.setSelectEffectEnabled(false);
            ArrayObjectAdapter primeAdapter = new ArrayObjectAdapter(listRowPresenter);
            for(int i = 0 ; i < movieResponse.getRows().size() ; i++)
            {
                ArrayObjectAdapter rowAdapter;
//                if(i == 0) {
//                    Log.d(TAG, "loadMovieResponse: ");
//                    ClassPresenterSelector presenterSelector = new ClassPresenterSelector();
//                    if(tvPresenter != null){
//                        tvPresenter.tearDownTvApi();
//                    }
//                    tvPresenter = new TvPresenter(movieResponse.getRows().get(i));
//                    presenterSelector.addClassPresenter(TVSource.class, tvPresenter);
//                    presenterSelector.addClassPresenter(MovieTile.class, cardPresenter);
//                    rowAdapter = new ArrayObjectAdapter(presenterSelector);
//                    rowAdapter.add(new TVSource());
//                }else
                    {
                    rowAdapter = new ArrayObjectAdapter(cardPresenter);
                }
                for (MovieTile movieTile : movieResponse.getRows().get(i).getRowItems()) {
                    movieTile.setRowLayout(movieResponse.getRows().get(i).getRowLayout());
                    rowAdapter.add(movieTile);
                }
                HeaderItem header = new HeaderItem(movieResponse.getRows().get(i).getRowIndex(), movieResponse.getRows().get(i).getRowHeader());
                primeAdapter.add(new ListRow(header, rowAdapter));
            }
            if(getAdapter() != null){
                ((ArrayObjectAdapter)getAdapter()).clear();
                primeAdapter.notifyItemRangeChanged(0, primeAdapter.size());
            }
            primeAdapter.add(loadSettingsRow());
            setAdapter(primeAdapter);
            primeAdapter.notifyItemRangeChanged(0, primeAdapter.size());
        } catch (Exception e) {
            if((getActivity()) != null)
                ((MainActivity)getActivity()).loadErrorFragment("Error while loading data ==> " + e.getLocalizedMessage(), "Back");
        }
    }

    private ListRow loadSettingsRow(){
        List<SettingsModel> settingsList = new ArrayList<>();
        settingsList.add(new SettingsModel("About Us", "settings_about", "about_us"));
        settingsList.add(new SettingsModel("Terms & Conditions", "settings_term", "term_condition"));
        ArrayObjectAdapter settingsAdapter = new ArrayObjectAdapter(new CharacterCardPresenter());
        settingsAdapter.addAll(0, settingsList);
        HeaderItem header = new HeaderItem("Settings");
        return new ListRow(header, settingsAdapter);
    }

    public void goToTop(){
        setSelectedPosition(0, true);
    }


    private MovieResponse writeCatsInCache(ResponseBody response){
        if((getActivity()) == null){
            return null;
        }
        File catsFile = new File(getActivity().getFilesDir().getAbsolutePath() + "/cats.json");
        //deleting old copy
        if (catsFile.exists()) {
            boolean result = catsFile.delete();
            Log.i("DeleteResult", "writeCatsInCache: "+result);
        }
        try {
            InputStream input = response.byteStream();
            OutputStream output = new FileOutputStream(catsFile);
            byte[] data = new byte[1024];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
            output.flush();
            input.close();
            output.close();
            response.close();
            //reading and making MovieResponse
            BufferedReader br = new BufferedReader(new FileReader(catsFile));
            return  new Gson().fromJson(br, MovieResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }
    }

    private MovieResponse readCatsFromCache(){
        if(getActivity() == null)
            return null;
        File catsFile = new File(getActivity().getFilesDir().getAbsolutePath() + "/cats.json");
        if(!catsFile.exists())
            return null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(catsFile));
            return new Gson().fromJson(br, MovieResponse.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void networkConnected() {
        Log.d(TAG, "networkConnected: ");
        if(getAdapter() != null && getAdapter().size() > 4){
            return;
        }
        onMessageEvent("refresh");
    }

    @Override
    public void networkDisconnected() {
        Log.d(TAG, "networkDisconnected: ");
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
//                if(item instanceof TVSource){
//                    if(tvPresenter != null){
//                        tvPresenter.pipToFull();
//                    }
//                    goToSource();
//                }else



        Log.d(TAG, "onItemClicked: ############################");
        if(item instanceof SettingsModel){
            Log.d(TAG, "onItemClicked: Settings");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("tv.cloudwalker.skin");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(((SettingsModel) item).getAction().equals("about_us")){
                Log.d(TAG, "onItemClicked: aboutus");
                intent.setClassName("tv.cloudwalker.skin", "tv.cloudwalker.skin.AboutActivity");
                startActivity(intent);
            }else{
                Log.d(TAG, "onItemClicked: terms");
                intent.setClassName("tv.cloudwalker.skin", "tv.cloudwalker.skin.TermsActivity");
                intent.putExtra("tag", "terms");
                startActivity(intent);
            }
        }else if (item instanceof MovieTile) {
            if (((MovieTile) item).isDetailPage()) {
                //logi Analytics event
                logAnalyticsEvent( row.getHeaderItem().getName(), item, "TILE_CLICKED");

                // Detail Page Stuff
                Bundle bundle = new Bundle();
                bundle.putParcelable(MovieTile.class.getSimpleName(), (MovieTile) item);
                Intent intent = new Intent(Objects.requireNonNull(getActivity()), DetailActivity.class);
                intent.putExtra("tileID", ((MovieTile) item).getTid());
                intent.putExtra(MovieTile.class.getSimpleName(), bundle);
                intent.putExtra("background", ((MovieTile) item).getBackground());
                startActivityForResult(intent, 10);

            } else {
                String successMsg = getPlayOnTv().trigger(((MovieTile) item).getPackageName(), ((MovieTile) item).getTarget().get(0));
                Log.d(TAG, "onItemClicked: "+successMsg);
            }
        }
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        logAnalyticsEvent(row.getHeaderItem().getName(), item, "TILE_SELECTED");
//                if(item instanceof  TVSource){
//                    if(tvPresenter != null)
//                        tvPresenter.unMuteAndPipPriviewWindow();
//                }else if(tvPresenter !=null){
//                    tvPresenter.mutePriviewWindow();
//                }
    }
}