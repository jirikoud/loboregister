package cz.jksoftware.loboregister.api.model;

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
    public List<ReportModel> reportList;
    public int totalCount;
    public String cursor;

    public AuthorModel(JSONObject jsonObject, boolean isParseReports) throws JSONException {
        this.id = jsonObject.getString("id");
        this.firstName = jsonObject.getString("firstName");
        this.lastName = jsonObject.getString("lastName");
        this.isColliding = jsonObject.getBoolean("hasCollidingName");
        this.totalReports = jsonObject.getInt("totalReports");
        this.extra = jsonObject.getString("extra");

        if (isParseReports) {
            JSONObject reportsObject = jsonObject.getJSONObject("reports");
            totalCount = reportsObject.getInt("totalCount");
            JSONObject pageObject = reportsObject.getJSONObject("pageInfo");
            cursor = pageObject.getString("endCursor");
            reportList = ReportModel.parse(reportsObject.getJSONArray("edges"));
        }
    }

    public static List<AuthorModel> parse(JSONArray jsonArray) throws JSONException {
        List<AuthorModel> modelList = new ArrayList<>();
        for (int index = 0; index < jsonArray.length(); index++) {
            JSONObject jsonObject = jsonArray.getJSONObject(index).getJSONObject("node");
            modelList.add(new AuthorModel(jsonObject, false));
        }
        return modelList;
    }

    public String getFullName(){
        return String.format("%s, %s", lastName, firstName);
    }

}
