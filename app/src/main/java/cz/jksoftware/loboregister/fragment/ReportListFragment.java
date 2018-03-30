package cz.jksoftware.loboregister.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import cz.jksoftware.loboregister.R;
import cz.jksoftware.loboregister.adapter.ReportListAdapter;
import cz.jksoftware.loboregister.api.LoadStatus;
import cz.jksoftware.loboregister.api.model.ErrorModel;
import cz.jksoftware.loboregister.api.model.ReportPageModel;
import cz.jksoftware.loboregister.api.task.ReportListTask;
import cz.jksoftware.loboregister.api.util.FragmentApiUtils;
import cz.jksoftware.loboregister.interfaces.MainInterface;

/**
 * Created by Koudy on 3/30/2018.
 * List of lobbyist contacts
 */

public class ReportListFragment extends Fragment implements ReportListTask.ResultDelegate, ReportListAdapter.ClickListener {

    @SuppressWarnings("unused")
    private static final String TAG = "ReportListFragment";

    private MainInterface mMainInterface;
    private TextView mTextViewCount;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;

    @Override
    public void onReportListTaskFinished(LoadStatus loadStatus, ReportPageModel model, ErrorModel errorModel) {
        mProgressBar.setVisibility(View.GONE);
        boolean isDone = FragmentApiUtils.processTaskResponse(loadStatus, getActivity(), errorModel);
        if (isDone) {
            return;
        }
        if (loadStatus == LoadStatus.SUCCESS) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.setAdapter(new ReportListAdapter(getContext(), this, model.reportList));
            mTextViewCount.setText(String.format(getString(R.string.contact_list_total_format), model.totalCount));
        }
    }

    @Override
    public void onAuthorItemClicked(String authorId) {

    }

    //region --- Fragment Lifecycle ---

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report_list, container, false);
        mTextViewCount = rootView.findViewById(R.id.text_view_total);
        mProgressBar = rootView.findViewById(R.id.progress_bar);
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMainInterface = (MainInterface) getActivity();

        if (getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), RecyclerView.VERTICAL));
        mRecyclerView.setVisibility(View.GONE);
        new ReportListTask(this).execute(getContext());
    }

    //endregion

}
