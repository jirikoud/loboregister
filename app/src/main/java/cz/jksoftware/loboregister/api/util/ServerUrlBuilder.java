package cz.jksoftware.loboregister.api.util;

import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jiří Koudelka on 27.02.2018.
 * Utility to create API call parameter string
 */

public class ServerUrlBuilder {

    @SuppressWarnings("unused")
    private static final String TAG = "ServerUrlBuilder";

    private static String buildPostParams(List<ServerUrlParam> paramList) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        String connector = "";
        for (ServerUrlParam entry : paramList) {
            switch (entry.getParamType()) {
                case ServerUrlParam.PARAM_TYPE_STRING:
                    builder.append(String.format("%s%s=%s",
                            connector,
                            entry.getParamName(),
                            entry.getStringValue() != null ? URLEncoder.encode(entry.getStringValue(), "UTF-8") : "")
                    );
                    break;
                case ServerUrlParam.PARAM_TYPE_STRING_LIST:
                    for (String paramString : entry.getStringListValue()) {
                        builder.append(String.format("%s%s[]=%s",
                                connector,
                                entry.getParamName(),
                                paramString != null ? URLEncoder.encode(paramString, "UTF-8") : "")
                        );
                        connector = "&";
                    }
                    break;
                case ServerUrlParam.PARAM_TYPE_LONG:
                    builder.append(String.format("%s%s=%s",
                            connector,
                            entry.getParamName(),
                            String.valueOf(entry.getLongValue()))
                    );
                    break;
            }
            connector = "&";
        }
        return builder.toString();
    }

    //region --- Public methods ---

    public static String buildURIString(String url, String methodName, ServerUrlParam... params) throws UnsupportedEncodingException {
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendEncodedPath(methodName);
        String uriString = builder.build().toString();
        if (params.length > 0){
            uriString += "?" + buildPostParams(params);
        }
        return uriString;
    }

    public static String buildPostParams(ServerUrlParam... params) throws UnsupportedEncodingException {
        List<ServerUrlParam> paramList = new ArrayList<>();
        paramList.addAll(Arrays.asList(params));
        return buildPostParams(paramList);
    }

    //endregion
}
