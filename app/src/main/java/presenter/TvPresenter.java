package presenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cvte.tv.api.TvApiSDKManager;
import com.cvte.tv.api.aidl.ITVApiScreenWindowAidl;

import androidx.leanback.widget.Presenter;
import model.MovieRow;
import tv.cloudwalker.launcher.CloudwalkerApplication;

public class TvPresenter extends Presenter implements ServiceConnection {

    private static final String TAG = "TvPresenter";
    private int width = 352;
    private int height = 198;
    private MovieRow movieRow;
    private ITVApiScreenWindowAidl mScreenWindowApi;
    private Handler tvApihandler;
    private HandlerThread tvApihandlerThread;
    public boolean isFocusedFlag;


    private Runnable muteRunnable = new Runnable() {
        @Override
        public void run() {
            if(mScreenWindowApi != null){
                try {
                    Log.d(TAG, "muteRunnable: pip mute  ===> "+mScreenWindowApi.eventScreenWindowMute(true));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Runnable unmutePipRunnable = new Runnable() {
        @Override
        public void run() {
            if(mScreenWindowApi != null){
                try {
                    Log.d(TAG, "unmutePipRunnable: PIP  ======>>>>>  "+ mScreenWindowApi.eventScreenWindowSetPipValue(50, 110, width, height));
                    Log.d(TAG, "unmutePipRunnable: pip unmute status ====>  "+mScreenWindowApi.eventScreenWindowMute(false));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    private Runnable pipToFullRunnable = new Runnable() {
        @Override
        public void run() {
            if(mScreenWindowApi != null){
                try {
                    Log.d(TAG, "pipToFullRunnable: pip full screen");
                    mScreenWindowApi.eventScreenWindowSetFull();
                    Log.d(TAG, "pipToFullRunnable: pip unmute    ====>  "+mScreenWindowApi.eventScreenWindowMute(false));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };



    public TvPresenter(MovieRow movieRow){
        this.movieRow = movieRow;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        preparingHandlerThread();
        initTvAPI(parent.getContext());
        try {
            height = getHeightForPreview(parent.getContext(), movieRow);
        } catch (Exception e) {
            e.printStackTrace();
        }
        width=(16* height) / 9;
        SurfaceView surfaceView = new SurfaceView(parent.getContext());
        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(width, height);
        layout.gravity = Gravity.CENTER;
        surfaceView.setLayoutParams(layout);
        surfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        surfaceView.setFocusable(true);
        surfaceView.setFocusableInTouchMode(true);
        try {
            surfaceView.setBackground(((CloudwalkerApplication)parent.getContext().getApplicationContext()).getDrawable("focus_on_select_bg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ViewHolder(surfaceView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        viewHolder.view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                isFocusedFlag = hasFocus;
                Log.d(TAG, "%%%%%%%%%%%%%%%%%%onFocusChange: "+hasFocus);
                if(hasFocus){
                    unMuteAndPipPriviewWindow();
                }else {
                    mutePriviewWindow();
                }
            }
        });
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {}


    private void initTvAPI(Context context){
        Intent intent = new Intent("com.cvte.tv.api.TV_API_SERVICE");
        intent.setPackage("com.cvte.tv.api.impl");
        context.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    private void preparingHandlerThread(){
        tvApihandlerThread = new HandlerThread("tvApiHandlerThread");
        tvApihandlerThread.start();
        Looper looper = tvApihandlerThread.getLooper();
        tvApihandler = new Handler(looper);
    }

    public void mutePriviewWindow() {
        if(tvApihandler == null) return;
        tvApihandler.removeCallbacks(unmutePipRunnable);
        tvApihandler.post(muteRunnable);
    }

    public void unMuteAndPipPriviewWindow() {
        if(tvApihandler == null) return;
        tvApihandler.removeCallbacks(muteRunnable);
        tvApihandler.post(unmutePipRunnable);
    }


    public void tearDownTvApi() {
        tvApihandler.post(pipToFullRunnable);
        if(tvApihandler != null){
            tvApihandler.removeCallbacks(muteRunnable);
            tvApihandler.removeCallbacks(unmutePipRunnable);
        }
        mScreenWindowApi = null;
        if(tvApihandlerThread != null)
            tvApihandlerThread.quit();

        tvApihandler = null;
        tvApihandlerThread = null;
        TvApiSDKManager.destroy();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "onServiceConnected: %%%%%%%%%%%%%%%%%%%");
//        ITvApiManager iTvApiManager = ITvApiManager.Stub.asInterface(service);
//        try {
//            mScreenWindowApi = iTvApiManager.getTVApiScreenWindow();
//            Log.d(TAG, "tvInitRunnable: PIP  ======>>>>>  "+ mScreenWindowApi.eventScreenWindowSetPipValue(50, 110, width, height));
//            Log.d(TAG, "tvInitRunnable: pip unMute  "+mScreenWindowApi.eventScreenWindowMute(false));
//        } catch (RemoteException e) {
//            Log.e(TAG, "#############Get TvApi error.");
//        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }



    private int getHeightForPreview(Context context, MovieRow movieRow) throws Exception {
        int height;
        if (movieRow.getRowItems().get(0).getTileHeight() != null) {
            height =  Integer.parseInt(movieRow.getRowItems().get(0).getTileHeight());
        } else {
            switch (movieRow.getRowLayout()) {
                case "landscape": {
                    height =  dpToPx(context, ((CloudwalkerApplication) context.getApplicationContext()).getInteger("tileLandScapeHeight"));
                }
                break;
                case "square": {
                    height = dpToPx(context,  ((CloudwalkerApplication) context.getApplicationContext()).getInteger("tileSquareHeight"));
                }
                break;
                case "portrait": {
                    height = dpToPx(context, ((CloudwalkerApplication) context.getApplicationContext()).getInteger("tilePotraitHeight"));
                }
                break;
                default: {
                    height = 201;
                }
            }
        }
        return  height;
    }

    private int dpToPx(Context ctx , int dp) {
        float density = ctx.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
}
