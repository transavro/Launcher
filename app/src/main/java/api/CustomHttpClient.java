package api;

import android.content.Context;
import android.webkit.URLUtil;

import org.jetbrains.annotations.NotNull;

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

import model.TvInfo;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import tv.cloudwalker.launcher.BuildConfig;
import tv.cloudwalker.launcher.CloudwalkerApplication;

import static tv.cloudwalker.launcher.CloudwalkerApplication.getSystemProperty;


public class CustomHttpClient {

    public static String[] userAgentList = {
            "ro.build.version.release",
            "ro.product.model",
            "ro.build.date.utc",
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
            assert trustManagers != null;
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

            builder.addInterceptor(new Interceptor() {
                @NotNull
                @Override
                public Response intercept(@NotNull Chain chain) throws IOException {
                   Request original = chain.request();


//                    TvInfo tvInfo = ((CloudwalkerApplication)context.getApplicationContext()).getTvInfo();
//                    Request request = original.newBuilder()
//                            .header("User-Agent",  (tvInfo.getBrand() != null ? tvInfo.getBrand() + "-" + tvUserAgent : tvUserAgent))
//                            .header("emac", tvInfo.getEmac())
//                            .header("mboard", tvInfo.getBoard())
//                            .header("panel", tvInfo.getPanel())
//                            .header("model", tvInfo.getModel())
//                            .header("cotaversion", tvInfo.getCota())
//                            .header("fotaversion", tvInfo.getFota())
//                            .header("lversion", BuildConfig.VERSION_NAME  + "-" + tvInfo.getBrand())
//                            .header("package", BuildConfig.APPLICATION_ID)
//                            .header("brand", tvInfo.getBrand())
//                            .method(original.method(), original.body())
//                            .build();

                    Request request = original.newBuilder()
                            .header("emac", "C0:8A:CD:C4:38:2A")
                            .header("mboard","T.HV553.81B")
                            .header("panel", "K650WDC2-LP330-A2")
                            .header("model", "CWTSSUA7")
                            .header("cotaversion", "20191206_160921")
                            .header("fotaversion", "20190830_014928")
                            .header("lversion", "2.1.0-41-geddca91-ss-com-smartscreen")
                            .header("package", "tv.cloudwalker.cwnxt.launcher.com")
                            .header("brand", "smartscreen")
                            .header("ui_version", "0")
                            .method(original.method(), original.body())
                            .build();


                    return chain.proceed(request);
                }
            });

            //adding logging if in DEBUG MODE
//            if (BuildConfig.DEBUG)
            {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(logging);
            }

            return builder.build();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static OkHttpClient getOkHttp(final Context context) {
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);
            builder.addInterceptor(new Interceptor() {
                @NotNull
                @Override
                public Response intercept(@NotNull Chain chain) throws IOException {
                    Request original = chain.request();
//                    TvInfo tvInfo = ((CloudwalkerApplication)context.getApplicationContext()).getTvInfo();
//                    Request request = original.newBuilder()
//                            .header("User-Agent",  (tvInfo.getBrand() != null ? tvInfo.getBrand() + "-" + tvUserAgent : tvUserAgent))
//                            .header("emac", tvInfo.getEmac())
//                            .header("mboard", tvInfo.getBoard())
//                            .header("panel", tvInfo.getPanel())
//                            .header("model", tvInfo.getModel())
//                            .header("cotaversion", tvInfo.getCota())
//                            .header("fotaversion", tvInfo.getFota())
//                            .header("lversion", BuildConfig.VERSION_NAME  + "-" + tvInfo.getBrand())
//                            .header("package", BuildConfig.APPLICATION_ID)
//                            .header("brand", tvInfo.getBrand())
//                            .method(original.method(), original.body())
//                            .build();

                    Request request = original.newBuilder()
                            .header("emac", "C0:8A:CD:C4:38:2A")
                            .header("mboard","T.HV553.81B")
                            .header("panel", "K650WDC2-LP330-A2")
                            .header("model", "CWTSSUA7")
                            .header("cotaversion", "20191206_160921")
                            .header("fotaversion", "20190830_014928")
                            .header("lversion", "2.1.0-41-geddca91-ss-com-smartscreen")
                            .header("package", "tv.cloudwalker.cwnxt.launcher.com")
                            .header("brand", "smartscreen")
                            .header("ui_version", "0")
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
