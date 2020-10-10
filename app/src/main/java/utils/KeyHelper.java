package utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;


public class KeyHelper {
    /**
     * @param key string like: SHA1, SHA256, MD5.
     */
    @SuppressLint("PackageManagerGetSignatures")
    public String get(Context context, String key, String packageName) {
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                final MessageDigest md = MessageDigest.getInstance(key);
                md.update(signature.toByteArray());

                final byte[] digest = md.digest();
                final StringBuilder toRet = new StringBuilder();
                for (int i = 0; i < digest.length; i++) {
                    if (i != 0) toRet.append(":");
                    int b = digest[i] & 0xff;
                    String hex = Integer.toHexString(b);
                    if (hex.length() == 1) toRet.append("0");
                    toRet.append(hex);
                }
                Log.e(KeyHelper.class.getSimpleName(), key + " " + toRet.toString());
                return toRet.toString();
            }
            return "";
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
            return "";
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
            return "";
        } catch (Exception e) {
            Log.e("exception", e.toString());
            return "";
        }
    }

    /**
     * @param packageName to get the cert for
     */
    private X509Certificate getX509Cert(Context context, String packageName){
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for (Signature sig : info.signatures) {
                final byte[] rawCert = sig.toByteArray();
                InputStream certStream = new ByteArrayInputStream(rawCert);
                CertificateFactory certFactory = CertificateFactory.getInstance("X509");
                return (X509Certificate) certFactory.generateCertificate(certStream);
            }
            return null;
        } catch (PackageManager.NameNotFoundException | CertificateException e) {
            e.printStackTrace();
            return null;
        }
    }
}