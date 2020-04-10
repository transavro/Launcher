package tv.cloudwalker.launcher;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.sql.Time;

import model.MovieTile;

public class CloudwalkerApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private long begine;
    private MovieTile heroMovieTile;
    private Boolean mainResume = false;
    private Boolean mainPause = false ;
    private Boolean detailResume = false; 
    private Boolean detailPause = false;
    private static final String TAG = "CloudwalkerApplication";
    private ActivityManager manager;


    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterActivityLifecycleCallbacks(this);
    }

    public Drawable getDrawable(String resourceName) {
        return getAppResources().getDrawable(getAppResources().getIdentifier(resourceName, "drawable", "tv.cloudwalker.skin"));
    }

    public String getString(String resourceName) {
        return getAppResources().getString(getAppResources().getIdentifier(resourceName, "string", "tv.cloudwalker.skin"));
    }

    public int getColor(String resourceName) {
        return getAppResources().getColor(getAppResources().getIdentifier(resourceName, "color", "tv.cloudwalker.skin"));
    }

    public int getInteger(String resourceName) {
        return getAppResources().getInteger(getAppResources().getIdentifier(resourceName, "integer", "tv.cloudwalker.skin"));
    }

    public boolean getBool(String resourceName) {
        return getAppResources().getBoolean(getAppResources().getIdentifier(resourceName, "bool", "tv.cloudwalker.skin"));
    }



    public Resources getAppResources() {
        try {
            return getPackageManager().getResourcesForApplication("tv.cloudwalker.skin");
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }


    public void setHeroMovieTile(MovieTile heroMovieTile) {
        this.heroMovieTile = heroMovieTile;
    }

    private void calculatingRating(long timeDiff){
        // first check the run time if it is present or not in the tile
        if(heroMovieTile == null){
            return;
        }

        if(!heroMovieTile.getRuntime().isEmpty()){
            Log.d(TAG, "calculatingRating: "+Time.parse(heroMovieTile.getRuntime()));
        }
        else
        {
            //TODO need to think
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        loadTask();
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if(activity instanceof  MainActivity){
            mainResume = true;
            mainPause = false;
        }else if(activity instanceof DetailActivity){
            detailResume = true;
            detailPause = false;
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        if(activity instanceof  MainActivity){
            mainResume = false;
            mainPause = true;
        }else if(activity instanceof DetailActivity){
            detailResume = false;
            detailPause = true;
        }
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        loadTask();
        if(mainPause && detailPause){
            Log.d(TAG, "onActivityStopped: THIS IS THE CONTENT PLAYING STATE");
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    private void loadTask(){
        if(manager == null){
            return;
        }

//        for(ActivityManager.AppTask appTask : manager.getAppTasks()){
//            Log.d(TAG, "######loadTask: "+appTask.getTaskInfo().baseActivity.getPackageName());
//        }
    }
}
