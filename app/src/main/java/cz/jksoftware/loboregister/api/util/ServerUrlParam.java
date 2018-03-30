package cz.jksoftware.loboregister.api.util;

import java.util.List;

/**
 * Created by Jiří Koudelka on 27.02.2018.
 * Url parameter
 */

public class ServerUrlParam {

    static final int PARAM_TYPE_STRING = 0;
    static final int PARAM_TYPE_STRING_LIST = 1;
    static final int PARAM_TYPE_LONG = 2;

    private int mParamType;
    private String mParamName;
    private String mStringValue;
    private long mLongValue;
    private List<String> mStringListValue;

    public ServerUrlParam(String paramName, String stringValue) {
        this.mParamType = PARAM_TYPE_STRING;
        this.mParamName = paramName;
        this.mStringValue = stringValue;
    }

    public ServerUrlParam(String paramName, List<String> stringListValue) {
        this.mParamType = PARAM_TYPE_STRING_LIST;
        this.mParamName = paramName;
        this.mStringListValue = stringListValue;
    }

    public ServerUrlParam(String paramName, long longValue) {
        this.mParamType = PARAM_TYPE_LONG;
        this.mParamName = paramName;
        this.mLongValue = longValue;
    }

    int getParamType() {
        return mParamType;
    }

    String getParamName() {
        return mParamName;
    }

    String getStringValue() {
        return mStringValue;
    }

    long getLongValue() {
        return mLongValue;
    }

    List<String> getStringListValue() {
        return mStringListValue;
    }
}
