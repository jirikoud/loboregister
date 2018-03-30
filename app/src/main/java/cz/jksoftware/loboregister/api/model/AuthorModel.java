package cz.jksoftware.loboregister.api.model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Koudy on 3/30/2018.
 * Author API model
 */

public class AuthorModel {

    public String id;
    public String firstName;
    public String lastName;
    public boolean isColliding;
    public int totalReports;
    public String extra;

    private AuthorModel(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getString("id");
        this.firstName = jsonObject.getString("firstName");
        this.lastName = jsonObject.getString("lastName");
        this.isColliding = jsonObject.getBoolean("hasCollidingName");
        this.totalReports = jsonObject.getInt("totalReports");
        this.extra = jsonObject.getString("extra");
    }

    public static List<AuthorModel> parse(JSONArray jsonArray) throws JSONException {
        List<AuthorModel> modelList = new ArrayList<>();
        for (int index = 0; index < jsonArray.length(); index++) {
            JSONObject jsonObject = jsonArray.getJSONObject(index).getJSONObject("node");
            modelList.add(new AuthorModel(jsonObject));
        }
        return modelList;
    }

    public String getFullName(){
        return String.format("%s, %s", lastName, firstName);
    }

}
