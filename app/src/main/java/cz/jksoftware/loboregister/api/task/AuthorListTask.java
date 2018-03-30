package cz.jksoftware.loboregister.api.task;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

import cz.jksoftware.loboregister.R;
import cz.jksoftware.loboregister.api.Api;
import cz.jksoftware.loboregister.api.LoadStatus;
import cz.jksoftware.loboregister.api.model.ApiResultModel;
import cz.jksoftware.loboregister.api.model.AuthorModel;
import cz.jksoftware.loboregister.api.model.ErrorModel;
import cz.jksoftware.loboregister.api.util.ServerUrlParam;

/**
 * Created by Koudy on 3/29/2018.
 * API Call for Author list
 */

public class AuthorListTask extends AsyncTask<Context, Void, AuthorListTask.ResultModel> {

    @SuppressWarnings("unused")
    private static final String TAG = "AuthorListTask";

    public interface ResultDelegate {
        void onAuthorListTaskFinished(LoadStatus loadStatus, List<AuthorModel> modelList, ErrorModel errorModel);
    }

    class ResultModel extends ApiResultModel {
        private List<AuthorModel> modelList;

        ResultModel(LoadStatus loadStatus, List<AuthorModel> modelList, ErrorModel errorModel) {
            super(loadStatus, errorModel);
            this.modelList = modelList;
        }
    }

    private final WeakReference<ResultDelegate> mDelegate;

    public AuthorListTask(ResultDelegate delegate) {
        mDelegate = new WeakReference<>(delegate);
    }

    @Override
    protected ResultModel doInBackground(final Context... params) {
        ServerUrlParam[] postParameters = new ServerUrlParam[]{
                new ServerUrlParam("query", params[0].getString(R.string.query_author_list))
        };
        return (ResultModel) Api.getApiGetResponse(params[0], "graphql", postParameters, new Api.ResponseDelegate() {
            @Override
            public Object onResponse(String url, String responseString) throws Exception {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.has("data")){
                    JSONArray authorArray = jsonObject.getJSONObject("data").getJSONObject("authors").getJSONArray("edges");
                    List<AuthorModel> modelList = AuthorModel.parse(authorArray);
                    return new ResultModel(LoadStatus.SUCCESS, modelList, null);
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
            mDelegate.get().onAuthorListTaskFinished(result.loadStatus, result.modelList, result.errorModel);
        }
    }

    @Override
    protected void onCancelled() {
        if (mDelegate.get() != null) {
            mDelegate.get().onAuthorListTaskFinished(LoadStatus.CANCELED, null, null);
        }
    }
}
