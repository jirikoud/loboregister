package cz.jksoftware.loboregister.api.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.jksoftware.loboregister.infrastructure.StringUtils;

/**
 * Created by Jiří Koudelka on 27.03.2018.
 * Model for API failed response
 */

public class ErrorModel {

    @SuppressWarnings("unused")
    private static final String TAG = "ErrorModel";

    public List<String> errorList;

    public ErrorModel(String response) {
        try {
            if (!StringUtils.isNullOrWhiteSpace(response)) {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("errors")) {
                    errorList = new ArrayList<>();
                    JSONArray jsonArray = jsonObject.getJSONArray("errors");
                    for (int index = 0; index < jsonArray.length(); index++) {
                        JSONObject errorObject = jsonArray.getJSONObject(index);
                        errorList.add(errorObject.getString("message"));
                    }
                }
            }
        } catch (Exception exception) {
            Log.e(TAG, "Error parsing response", exception);
        }
    }
}
