package utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import static utils.AppUtils.isPackageInstalled;


//***********passed
//zee5
//youtube
//hotstar

//*********failed
//sunnext
//yupptv
//sonylive failed partial


public class PlayOnTv {
    Context context;
    private static final String TAG = "PlayOnTv";
    private String[] cwPartner = {
            "com.zee5.aosp",
            "in.startv.hotstartvonly",
            "com.sonyliv",
            "com.cloudwalker.shemarootv" ,
            "com.jio.media.stb.ondemand",
            "com.eros.now",
            "com.hungama.movies.tv",
            "com.balaji.alt.partner",
            "com.yupptv.cloudwalker",
            "com.epic.docubay.fire",
            "com.epicchannel.epicon.cloudwalkerTv",
            "tv.gemplex.firetv",
            "com.watcho",
            "com.flickstree.tv",
            "com.suntv.sunnxt"};


    public PlayOnTv(Context context){
        this.context = context;
    }

    public String trigger(String packageName, String deeplink){
        if(context == null || packageName == null || deeplink == null) return "Not able to play.";

        //manupilate acoording to CW
        String[] result = manupilateCW(packageName, deeplink);
        packageName = result[0];
        deeplink = result[1];

        //check if the app is installed or not
        if(!isPackageInstalled(packageName, context.getPackageManager())){
            goToAppStore(packageName);
            return "Sending to App Store.";
        }

        //if app is installed
        return play(packageName, deeplink);
    }



    private String[] manupilateCW(String packageName, String deeplink) {
        Log.d(TAG,"CONTENT PLAY START ===>>>  "+ packageName + "      "+ deeplink);
        if (deeplink == null || deeplink.equals("null")) {
            deeplink = "";
        }

        if (packageName.contains("youtube")) {

            if(!packageName.contains(".tv"))
                packageName = packageName + ".tv";

            if (!deeplink.contains("https://")) {
                if (deeplink.startsWith("PL") || deeplink.startsWith("RD")) {
                    deeplink = "https://www.youtube.com/playlist?list="+deeplink;
                } else if (deeplink.startsWith("UC")) {
                    deeplink = "https://www.youtube.com/channel/"+deeplink;
                } else {
                    deeplink = "https://www.youtube.com/watch?v="+deeplink;
                }
            }
        } else if (packageName.contains("graymatrix")) {
            packageName = "com.zee5.aosp";
        } else if (packageName.contains("amazon")) {
            deeplink = deeplink.replaceFirst("https://www", "intent://app");
            String[] result = deeplink.split("\\?");
            if (result.length > 1) {
                deeplink = deeplink.replaceAll(result[1], "");
            }
            deeplink = deeplink + "&time=500";
        } else if (packageName.contains("hotstar")) {
            if(!packageName.equals("in.startv.hotstartvonly")){
                packageName = "in.startv.hotstartvonly";
            }
            deeplink = deeplink.replaceFirst("https://www.hotstar.com", "hotstar://content");
            deeplink = deeplink.replaceFirst("http://www.hotstar.com", "hotstar://content");
        } else if (packageName.contains("jio")) {
            packageName = "com.jio.media.stb.ondemand";
        }else if(packageName.equals("com.tru")){
            packageName = "com.yupptv.cloudwalker";
        }else if(packageName.equals("com.sonyliv")){
            deeplink = deeplink.replace( "https://www.sonyliv.com/details/full movie", "sony://player");
            String[] tmp = deeplink.split("/");
            deeplink = deeplink.replace(tmp[tmp.length - 1], "");
        }

        //playing...
        Log.d(TAG,"CONTENT PLAY END ===>>>  "+ packageName + "   "+ deeplink);
        return new String[]{packageName, deeplink};
    }

    private String play(String packageName, String deeplink){
        Intent playIntent = new Intent();
        if(deeplink.isEmpty()){
            playIntent = context.getPackageManager().getLeanbackLaunchIntentForPackage(packageName);
            if(playIntent == null){
                playIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            }
        }else{
            //making intent
            playIntent.setPackage(packageName);
            playIntent.setData(Uri.parse(deeplink));
            playIntent.setAction(Intent.ACTION_VIEW);
        }

        try{
            if(!packageName.contains("youtube"))
                playIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(playIntent);
            return "Playing...";
        }catch (Exception e){
            e.printStackTrace();
            return "Not able to play.";
        }
    }


    private void goToAppStore(String packageName){
        Log.d(TAG, "goToAppStore: "+packageName);
        Intent intent = new Intent();
        if (Arrays.asList(cwPartner).contains(packageName) && isPackageInstalled("tv.cloudwalker.market", context.getPackageManager())){
            //go to cloudwalker appstore
            String uri = "cwmarket://appstore?package=" + packageName;
            intent.setData(Uri.parse(uri));
            intent.setPackage("tv.cloudwalker.market");
            intent.setClassName( "tv.cloudwalker.market" , "tv.cloudwalker.market.activity.AppDetailsActivity" );


        }else if(isPackageInstalled("com.stark.store", context.getPackageManager())){
            //go to cvte Appstore
            String uri = "appstore://appDetail?package=" + packageName;
            intent.setData(Uri.parse(uri));
            intent.setPackage("com.stark.store");
            intent.setClassName( "com.stark.store" , "com.stark.store.ui.detail.AppDetailActivity" );
        }else {
            //is no app store trigger generic
            intent.setData(Uri.parse("market://details?id=" + packageName));
            final List<ResolveInfo> otherApps = context.getPackageManager().queryIntentActivities(intent, 0);
            for (ResolveInfo otherApp : otherApps) {
                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setComponent(componentName);
                break;
            }
        }
        try{
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }catch (Exception e){
            Log.e(TAG, "goToAppStore:Error while triggering AppStore ", e);
        }
    }

}