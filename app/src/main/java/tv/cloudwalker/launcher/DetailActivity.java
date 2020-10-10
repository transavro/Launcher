package tv.cloudwalker.launcher;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.leanback.app.BackgroundManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

public class DetailActivity extends FragmentActivity {

    private Drawable detailDrawable;
    private String backgroundUrl;
    private static final String TAG = "DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        BackgroundManager.getInstance(this).attach(this.getWindow());
        backgroundUrl = getIntent().getStringExtra("background");
    }

    @Override
    protected void onStart() {
        // Initialize here to display the background when coming back
        if (detailDrawable == null) {
            if (backgroundUrl != null) {
                loadBackground(backgroundUrl);
            } else {
                BackgroundManager.getInstance(this).setDrawable(ContextCompat.getDrawable(this, R.drawable.movie));
            }
        } else {
            BackgroundManager.getInstance(this).setDrawable(detailDrawable);
        }
        super.onStart();
    }

    @Override
    public void onTrimMemory(int level) {
        try {
            Glide.get(this).onTrimMemory(level);
        }catch (Exception e){
            Log.e(TAG, "onTrimMemory: ",e);
        }
        super.onTrimMemory(level);
    }

  

    private void loadBackground(String backgroundUrl) {
        Glide.with(this)
                .load(backgroundUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        detailDrawable = resource.getCurrent();
                        BackgroundManager.getInstance(DetailActivity.this).setDrawable(detailDrawable);
                    }
                });
    }

    @Override
    protected void onPause() {
        Glide.with(this).pauseRequests();
        BackgroundManager.getInstance(this).clearDrawable();
        super.onPause();
    }

    @Override
    protected void onStop() {
        BackgroundManager.getInstance(this).release();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
