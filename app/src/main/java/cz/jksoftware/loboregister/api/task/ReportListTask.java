package cz.jksoftware.loboregister.api.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cz.jksoftware.loboregister.R;
import cz.jksoftware.loboregister.api.Api;
import cz.jksoftware.loboregister.api.LoadStatus;
import cz.jksoftware.loboregister.api.model.ApiResultModel;
import cz.jksoftware.loboregister.api.model.ErrorModel;
import cz.jksoftware.loboregister.api.model.ReportModel;
import cz.jksoftware.loboregister.api.model.ReportPageModel;
import cz.jksoftware.loboregister.api.util.ServerUrlParam;
import cz.jksoftware.loboregister.infrastructure.Constants;

/**
 * Created by Koudy on 3/30/2018.
 * API Call for Report list
 */

public class ReportListTask extends AsyncTask<Context, Void, ReportListTask.ResultModel> {

    @SuppressWarnings("unused")
    private static final String TAG = "AuthorListTask";

    public interface ResultDelegate {
        void onReportListTaskFinished(LoadStatus loadStatus, String query, String after, ReportPageModel model, ErrorModel errorModel);
    }

    class ResultModel extends ApiResultModel {
        private ReportPageModel model;

        ResultModel(LoadStatus loadStatus, ReportPageModel model, ErrorModel errorModel) {
            super(loadStatus, errorModel);
            this.model = model;
        }
    }

    private final WeakReference<ResultDelegate> mDelegate;
    private final String mAfter;
    private final String mQuery;

    public ReportListTask(ResultDelegate delegate, String after, String query) {
        mDelegate = new WeakReference<>(delegate);
        mAfter = after;
        mQuery = query;
    }

    @Override
    protected ResultModel doInBackground(final Context... params) {
        JSONObject queryParams = new JSONObject();
        try {
            queryParams.put("first", Constants.REPORT_PAGE_SIZE);
            if (mAfter != null) {
                queryParams.put("after", mAfter);
            }
            if (mQuery != null) {
                queryParams.put("query", mQuery);
            }
        } catch (Exception exception) {
            Log.e(TAG, "Error creating GraphQL params", exception);
        }
        ServerUrlParam[] postParameters = new ServerUrlParam[]{
                new ServerUrlParam("query", params[0].getString(R.string.query_report_list)),
                new ServerUrlParam("variables", queryParams.toString())
        };
        return (ResultModel) Api.getApiGetResponse(params[0], "graphql", postParameters, new Api.ResponseDelegate() {
            @Override
            public Object onResponse(String url, String responseString) throws Exception {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.has("data")) {
                    ReportPageModel model = new ReportPageModel();
                    JSONObject reportsObject = jsonObject.getJSONObject("data").getJSONObject("searchReports");
                    JSONArray authorArray = reportsObject.getJSONArray("edges");
                    JSONObject pageObject = reportsObject.getJSONObject("pageInfo");
                    model.cursor = pageObject.getString("endCursor");
                    model.reportList = ReportModel.parse(authorArray);
                    model.totalCount = reportsObject.getInt("totalCount");
                    return new ResultModel(LoadStatus.SUCCESS, model, null);
                }
                return new ResultModel(LoadStatus.FAILED, null, null);
            }

            @Override
            public Object onFailed(String url, int errorCode, String errorMessage) {
                if (errorCode == Api.HTTP_CODE_OFFLINE) {
                    return new ResultModel(LoadStatus.OFFLINE, null, null);
                }
                return new ResultModel(LoadStatus.FAILED, null, new ErrorModel(errorMessage));
            }
        });
    }

    @Override
    protected void onPostExecute(final ResultModel result) {
        if (mDelegate.get() != null) {
            mDelegate.get().onReportListTaskFinished(result.loadStatus, mQuery, mAfter, result.model, result.errorModel);
        }
    }

    @Override
    protected void onCancelled() {
        if (mDelegate.get() != null) {
            mDelegate.get().onReportListTaskFinished(LoadStatus.CANCELED, mQuery, mAfter, null, null);
        }
    }
}
