package utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import model.MovieResponse;

public class AppUtils {

    public MovieResponse readJSONFromAsset(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName); //shoudl include ".json" in it
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            return gson.fromJson(json, MovieResponse.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public static boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static String getSystemProperty(String key) {
        String value = null;
        try {
            value = (String) Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class).invoke(null, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


    public static String loadFileAsString(String filePath) throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }

    public static String getEthMacAddress() {
        try {
            return loadFileAsString("/sys/class/net/eth0/address")
                    .toUpperCase().substring(0, 17);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getWifiMacAddress(Context context) {

        String macAddress = "";
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            macAddress = getWifiMacForAndroid6x().toUpperCase();
        } else {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo;
            if (wifiManager != null) {
                wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo.getMacAddress() != null) {
                    macAddress = wifiInfo.getMacAddress().toUpperCase();
                }else{
                    macAddress = "02:00:00:00:00:00";
                }

            }
        }
        return macAddress;
    }

    public static String getWifiMacForAndroid6x() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString().replace(":", "");
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }
}
