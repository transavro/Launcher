package tv.cloudwalker.launcher;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preparingData();
    }

    private void preparingData(){
        SharedPreferences sharedPreferences = getSharedPreferences("tvinfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("tvEmac", getEthMacAddress());
        editor.putString("tvBoard", getSystemProperty("ro.cvte.boardname"));
        editor.putString("tvPanel", getSystemProperty("ro.cvte.panelname"));
        editor.putString("tvModel", getSystemProperty("ro.product.model"));
        editor.apply();
        editor.commit();
    }

    public String getSystemProperty(String key) {
        String value = null;
        try {
            value = (String) Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class).invoke(null, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public String getEthMacAddress() {
        try {
            return loadFileAsString("/sys/class/net/eth0/address").toUpperCase().substring(0, 17);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String loadFileAsString(String filePath) throws java.io.IOException {
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


    @Override
    public void onBackPressed() {

    }
}
