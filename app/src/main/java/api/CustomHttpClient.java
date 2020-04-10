package api;

import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.URLUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import tv.cloudwalker.launcher.BuildConfig;

/**
 * Created by cognoscis on 8/3/18.
 */

public class CustomHttpClient {

    public static String[] userAgentList = {
            "ro.build.version.release",
            "ro.product.model",
            "ro.cvte.ota.version",
            "ro.cloudwalker.cota.version"
    };

    public static boolean isUrlHTTPS(String url) {
        return URLUtil.isHttpsUrl(url);
    }

    static final String tvUserAgent = getUserAgentString("cloudtv");

    public static String getUserAgentString(String brandName) {
        String userAgentStr = "";
        for (String userAgentKey : userAgentList) {
            if (!getSystemProperty(userAgentKey).isEmpty()) {
                userAgentStr = brandName + "-" + getSystemProperty(userAgentKey) + "/";
            }
        }
        userAgentStr = userAgentStr + BuildConfig.VERSION_NAME;
        return userAgentStr;
    }

    private static OkHttpClient getOkHttps(final Context context) {
        SSLContext sslContext;
        TrustManager[] trustManagers;
        try {
            trustManagers = getTrustManagers(context);
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, null);

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);
            builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0]);


            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession sslSession) {
                    HostnameVerifier hv =
                            HttpsURLConnection.getDefaultHostnameVerifier();
                    boolean verified = hv.verify("*.cloudwalker.tv", sslSession);
                    return true;
                }
            });

            final SharedPreferences sharedPreferences = context.getSharedPreferences("tvInfo", Context.MODE_PRIVATE);

            builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {

                    Request original = chain.request();

                    Request request = original.newBuilder()
//                            .header("emac", sharedPreferences.getString("tvEmac", "N0:N0:N0:N0:N0:N0"))
                            .header("emac", "70:2E:D9:55:44:33")
//                            .header("mboard", sharedPreferences.getString("tvBoard", ""))
                            .header("mboard", "BD_TP_MS358_PB802")
//                            .header("panel", sharedPreferences.getString("tvPanel", ""))
                            .header("panel", "SamsungLSC320ANO9")
//                            .header("model", sharedPreferences.getString("tvModel", ""))
                            .header("model", "VTX32")
                            .header("cotaversion", getSystemProperty("ro.cloudwalker.cota.version"))
//                            .header("fotaversion", getSystemProperty("ro.cvte.ota.version"))
                            .header("fotaversion", "20190626_164023")
//                            .header("lversion", BuildConfig.VERSION_NAME + "-" + getSystemProperty("ro.cloudwalker.brand"))
                            .header("lversion", "1.5.0-55-gbfc3a69-com-generic")
                            .header("package", BuildConfig.APPLICATION_ID)
                            .header("brand", getSystemProperty("ro.cloudwalker.brand"))
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });

            return builder.build();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getSystemProperty(String key) {
        String value = null;
        try {
            value = (String) Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class).invoke(null, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private static OkHttpClient getOkHttp(Context context) {
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);

            final SharedPreferences sharedPreferences = context.getSharedPreferences("tvInfo", Context.MODE_PRIVATE);

            builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            .header("emac", sharedPreferences.getString("tvEmac", "N0:N0:N0:N0:N0:N0"))
                            .header("mboard", sharedPreferences.getString("tvBoard", ""))
                            .header("panel", sharedPreferences.getString("tvPanel", ""))
                            .header("model", sharedPreferences.getString("tvModel", ""))
                            .header("cotaversion", getSystemProperty("ro.cloudwalker.cota.version"))
                            .header("fotaversion", getSystemProperty("ro.cvte.ota.version"))
                            .header("lversion", BuildConfig.VERSION_NAME + "-" + getSystemProperty("ro.cloudwalker.brand"))
                            .header("package", BuildConfig.APPLICATION_ID)
                            .header("brand", getSystemProperty("ro.cloudwalker.brand"))
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });

            return builder.build();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static OkHttpClient getHttpClient(Context context, String url) {
        if (isUrlHTTPS(url)) {
            return getOkHttps(context);
        } else {
            return getOkHttp(context);
        }
    }

    private static TrustManager[] getTrustManagers(Context context) {
        TrustManager[] trustManagers;
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            InputStream certInputStream = context.getAssets().open("server.crt");
            BufferedInputStream bis = new BufferedInputStream(certInputStream);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            while (bis.available() > 0) {
                Certificate cert = certificateFactory.generateCertificate(bis);
                keyStore.setCertificateEntry("ca", cert);
            }
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            trustManagers = trustManagerFactory.getTrustManagers();
            return trustManagers;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
