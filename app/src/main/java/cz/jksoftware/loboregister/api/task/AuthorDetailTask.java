package cz.jksoftware.loboregister.api.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cz.jksoftware.loboregister.R;
import cz.jksoftware.loboregister.api.Api;
import cz.jksoftware.loboregister.api.LoadStatus;
import cz.jksoftware.loboregister.api.model.ApiResultModel;
import cz.jksoftware.loboregister.api.model.AuthorModel;
import cz.jksoftware.loboregister.api.model.ErrorModel;
import cz.jksoftware.loboregister.api.util.ServerUrlParam;
import cz.jksoftware.loboregister.infrastructure.Constants;

/**
 * Created by Koudy on 3/31/2018.
 * API Call for Author detail and his Reports
 */

public class AuthorDetailTask extends AsyncTask<Context, Void, AuthorDetailTask.ResultModel> {

    @SuppressWarnings("unused")
    private static final String TAG = "AuthorDetailTask";

    public interface ResultDelegate {
        void onAuthorDetailTaskFinished(LoadStatus loadStatus, String cursor, AuthorModel model, ErrorModel errorModel);
    }

    class ResultModel extends ApiResultModel {
        private AuthorModel model;

        ResultModel(LoadStatus loadStatus, AuthorModel model, ErrorModel errorModel) {
            super(loadStatus, errorModel);
            this.model = model;
        }
    }

    private final WeakReference<ResultDelegate> mDelegate;
    private String mAuthorId;
    private String mCursor;

    public AuthorDetailTask(ResultDelegate delegate, String authorId, String after) {
        mDelegate = new WeakReference<>(delegate);
        mAuthorId = authorId;
        mCursor = after;
    }

    @Override
    protected ResultModel doInBackground(final Context... params) {
        JSONObject queryParams = new JSONObject();
        try {
            queryParams.put("id", mAuthorId);
            queryParams.put("first", Constants.REPORT_PAGE_SIZE);
            if (mCursor != null){
                queryParams.put("after", mCursor);
            }
        } catch (Exception exception) {
            Log.e(TAG, "Error creating GraphQL params", exception);
        }
        ServerUrlParam[] postParameters = new ServerUrlParam[]{
                new ServerUrlParam("query", params[0].getString(R.string.query_author_detail)),
                new ServerUrlParam("variables", queryParams.toString())
        };
        return (ResultModel) Api.getApiGetResponse(params[0], "graphql", postParameters, new Api.ResponseDelegate() {
            @Override
            public Object onResponse(String url, String responseString) throws Exception {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.has("data")){
                    JSONObject authorObject = jsonObject.getJSONObject("data").getJSONObject("node");
                    AuthorModel model = new AuthorModel(authorObject, true);
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
            mDelegate.get().onAuthorDetailTaskFinished(result.loadStatus, mCursor, result.model, result.errorModel);
        }
    }

    @Override
    protected void onCancelled() {
        if (mDelegate.get() != null) {
            mDelegate.get().onAuthorDetailTaskFinished(LoadStatus.CANCELED, mCursor, null, null);
        }
    }
}
