package cz.jksoftware.loboregister.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.jksoftware.loboregister.BuildConfig;
import cz.jksoftware.loboregister.api.model.ApiResultModel;
import cz.jksoftware.loboregister.api.util.ServerUrlBuilder;
import cz.jksoftware.loboregister.api.util.ServerUrlParam;
import cz.jksoftware.loboregister.infrastructure.Constants;

/**
 * Created by Koudy on 3/30/2018.
 * API calls implementation
 */

public class Api {

    @SuppressWarnings("unused")
    private static final String TAG = "Api";

    @SuppressWarnings("WeakerAccess")
    public static final int HTTP_CODE_FAILED = 600;
    public static final int HTTP_CODE_OFFLINE = 601;

    private static final int MAX_ATTEMPTS_TO_CONNECTION = 5;
    private static final int ATTEMPTS_SLEEP_INTERVAL = 1000; // v milisekundach

    private final static String API_URL_PATH = "/";

    //region --- Private methods ---

    private static String getServerApiBaseUrl() {
        switch (BuildConfig.API_SERVER_TARGET) {
            case 0:
                return Constants.API_BASE_ADDRESS_DEV;
            case 1:
                return Constants.API_BASE_ADDRESS_PROD;
        }
        return null;
    }

    private static String getApiUrl() {
        return getServerApiBaseUrl() + API_URL_PATH;
    }

    private static String getResponseString(int statusCode, HttpURLConnection httpConnection) throws IOException {
        String contentType = httpConnection.getHeaderField("Content-Type");
        String charset = "UTF-8";
        for (String param : contentType.replace(" ", "").split(";")) {
            if (param.startsWith("charset=")) {
                charset = param.split("=", 2)[1];
                break;
            }
        }

        StringBuilder builder = new StringBuilder();
        if (statusCode < HttpsURLConnection.HTTP_BAD_REQUEST) {
            InputStream gzipStream = new GZIPInputStream(httpConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(gzipStream, charset));
            for (String line; (line = reader.readLine()) != null; ) {
                builder.append(line);
            }
            return builder.toString();
        } else {
            InputStream inputStream = httpConnection.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
            for (String line; (line = reader.readLine()) != null; ) {
                builder.append(line);
            }
            return builder.toString();
        }
    }

    private static String getUrlString(String method) {
        return Api.getApiUrl() + method;
    }

    //endregion

    //region --- Public methods ---

    public static abstract class ResponseDelegate<T> {
        public abstract T onResponse(String url, String responseString) throws Exception;

        public abstract T onFailed(String url, int errorCode, String responseString);
    }

    @SuppressWarnings("SameParameterValue")
    public static <T extends ApiResultModel> T getApiGetResponse(Context context, String methodName, ServerUrlParam[] parameters, ResponseDelegate<T> delegate) {
        String urlString = null;
        try {
            urlString = ServerUrlBuilder.buildURIString(Api.getApiUrl(), methodName, parameters);

            for (int attempt = 1; attempt <= Api.MAX_ATTEMPTS_TO_CONNECTION; attempt++) {
                Log.d(TAG, "attempt: " + attempt);
                try {
                    HttpsURLConnection httpConnection = null;
                    try {
                        httpConnection = (HttpsURLConnection) new URL(urlString).openConnection();
                        httpConnection.setDoInput(true);
                        httpConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
                        int statusCode = httpConnection.getResponseCode();
                        if (statusCode == HttpsURLConnection.HTTP_OK) {
                            return delegate.onResponse(urlString, Api.getResponseString(statusCode, httpConnection));
                        } else {
                            return delegate.onFailed(urlString, statusCode, Api.getResponseString(statusCode, httpConnection));
                        }
                    } finally {
                        if (httpConnection != null) {
                            httpConnection.disconnect();
                        }
                    }
                } catch (InterruptedIOException e) {
                    Log.e(TAG, "attempt = " + attempt + "; url = " + urlString, e);
                } catch (IOException e) {
                    Log.e(TAG, "communication error; attempt = " + attempt + "; url = " + urlString, e);
                } catch (Exception e) {
                    Log.e(TAG, "some error; attempt = " + attempt + "; url = " + urlString, e);
                }
                if (attempt < Api.MAX_ATTEMPTS_TO_CONNECTION) {
                    SystemClock.sleep(Api.ATTEMPTS_SLEEP_INTERVAL);
                }
            }
        } catch (Exception exception) {
            Log.e(TAG, "getApiPostResponse failed", exception);
        }
        if (!isOnline(context)){
            return delegate.onFailed(urlString, HTTP_CODE_OFFLINE, null);
        }
        return delegate.onFailed(urlString, HTTP_CODE_FAILED, null);
    }

    @SuppressWarnings("unused")
    public static <T extends ApiResultModel> T getApiPostResponse(Context context, String methodName, ServerUrlParam[] parameters, ResponseDelegate<T> delegate) {
        String urlString = null;
        try {
            //TODO Vyhodit, pouze pro testovani
            Thread.sleep(1000);

            urlString = Api.getUrlString(methodName);
            String paramString = ServerUrlBuilder.buildPostParams(parameters);

            for (int attempt = 1; attempt <= Api.MAX_ATTEMPTS_TO_CONNECTION; attempt++) {
                Log.d(TAG, "attempt: " + attempt);
                try {
                    HttpsURLConnection httpConnection = null;
                    try {
                        httpConnection = (HttpsURLConnection) new URL(urlString).openConnection();
                        httpConnection.setDoInput(true);
                        httpConnection.setRequestProperty("Accept-Encoding", "identity");
                        httpConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
                        httpConnection.setDoOutput(true);
                        OutputStream output = httpConnection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
                        writer.write(paramString);
                        writer.flush();
                        writer.close();
                        output.close();
                        int statusCode = httpConnection.getResponseCode();
                        if (statusCode == HttpsURLConnection.HTTP_OK) {
                            return delegate.onResponse(urlString, Api.getResponseString(statusCode, httpConnection));
                        } else {
                            return delegate.onFailed(urlString, statusCode, Api.getResponseString(statusCode, httpConnection));
                        }
                    } finally {
                        if (httpConnection != null) {
                            httpConnection.disconnect();
                        }
                    }
                } catch (InterruptedIOException e) {
                    Log.e(TAG, "attempt = " + attempt + "; url = " + urlString, e);
                } catch (IOException e) {
                    Log.e(TAG, "communication error; attempt = " + attempt + "; url = " + urlString, e);
                } catch (Exception e) {
                    Log.e(TAG, "some error; attempt = " + attempt + "; url = " + urlString, e);
                }
                if (attempt < Api.MAX_ATTEMPTS_TO_CONNECTION) {
                    SystemClock.sleep(Api.ATTEMPTS_SLEEP_INTERVAL);
                }
            }
        } catch (Exception exception) {
            Log.e(TAG, "getApiPostResponse failed", exception);
        }
        return delegate.onFailed(urlString, HTTP_CODE_FAILED, null);
    }

    private static boolean isOnline(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return true;
    }

    //endregion

    // region --- SSL Certificate ignoring ---

    static {
        if (BuildConfig.DEBUG) {
            TrustManager[] trustAllCertificates = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null; // Not relevant.
                        }

                        @Override
                        @SuppressLint("TrustAllX509TrustManager")
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            // Do nothing. Just allow them all.
                        }

                        @Override
                        @SuppressLint("TrustAllX509TrustManager")
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            // Do nothing. Just allow them all.
                        }
                    }
            };

            HostnameVerifier trustAllHostNames = new HostnameVerifier() {
                @Override
                @SuppressLint("BadHostnameVerifier")
                public boolean verify(String hostname, SSLSession session) {
                    return true; // Just allow them all.
                }
            };

            try {
                System.setProperty("jsse.enableSNIExtension", "false");
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCertificates, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(trustAllHostNames);
            } catch (GeneralSecurityException e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }

    //endregion
}
