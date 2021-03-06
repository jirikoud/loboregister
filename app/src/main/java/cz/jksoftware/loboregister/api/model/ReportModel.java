package cz.jksoftware.loboregister.api.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.jksoftware.loboregister.infrastructure.StringUtils;

/**
 * Created by Koudy on 3/30/2018.
 * Report API model
 */

public class ReportModel implements Serializable {

    public String id;
    public String authorId;
    public String authorFirstName;
    public String authorLastName;
    public Date date;
    public Date published;
    public String body;
    public String received;
    public String provided;

    private ReportModel(JSONObject jsonObject, DateFormat dateFormat) throws JSONException {
        this.id = jsonObject.getString("id");
        this.body = jsonObject.getString("body");
        this.date = StringUtils.parseDate(dateFormat, jsonObject.getString("date"));
        this.published = StringUtils.parseDate(dateFormat, jsonObject.getString("published"));
        this.received = jsonObject.getString("receivedBenefit");
        this.provided = jsonObject.getString("providedBenefit");
        JSONObject authorObject = jsonObject.getJSONObject("author");
        this.authorId = authorObject.getString("id");
        this.authorFirstName = authorObject.getString("firstName");
        this.authorLastName = authorObject.getString("lastName");
    }

    public static List<ReportModel> parse(JSONArray jsonArray) throws JSONException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ssZ", Locale.US);
        List<ReportModel> modelList = new ArrayList<>();
        for (int index = 0; index < jsonArray.length(); index++) {
            JSONObject jsonObject = jsonArray.getJSONObject(index).getJSONObject("node");
            modelList.add(new ReportModel(jsonObject, dateFormat));
        }
        return modelList;
    }

    public String getFullName(){
        return String.format("%s %s", authorFirstName, authorLastName);
    }

    public String getBodyShort(){
        if (body == null){
            return null;
        }
        return body.substring(0, Math.min(body.length(), 200));
    }
}
