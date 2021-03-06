package cz.jksoftware.loboregister.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import java.util.ArrayList;

import cz.jksoftware.loboregister.R;
import cz.jksoftware.loboregister.adapter.ReportListAdapter;
import cz.jksoftware.loboregister.api.LoadStatus;
import cz.jksoftware.loboregister.api.model.ErrorModel;
import cz.jksoftware.loboregister.api.model.ReportModel;
import cz.jksoftware.loboregister.api.model.ReportPageModel;
import cz.jksoftware.loboregister.api.task.ReportListTask;
import cz.jksoftware.loboregister.api.util.FragmentApiUtils;
import cz.jksoftware.loboregister.dialog.SearchDialog;
import cz.jksoftware.loboregister.infrastructure.StringUtils;
import cz.jksoftware.loboregister.interfaces.MainInterface;

/**
 * Created by Koudy on 3/30/2018.
 * List of Reports
 */

public class ReportListFragment extends Fragment implements ReportListTask.ResultDelegate, ReportListAdapter.EventListener, SearchDialog.ResultListener {

    @SuppressWarnings("unused")
    private static final String TAG = "ReportListFragment";

    private MainInterface mMainInterface;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private ReportListAdapter mAdapter;

    private String mCursor;
    private String mQuery;
    private ArrayList<ReportModel> mReportList;
    private int mTotalCount;

    @Override
    public void onReportListTaskFinished(LoadStatus loadStatus, String query, String after, ReportPageModel model, ErrorModel errorModel) {
        if (!StringUtils.isEqual(mQuery, query) || !StringUtils.isEqual(mCursor, after)) {
            return;
        }
        mProgressBar.setVisibility(View.GONE);
        boolean isDone = FragmentApiUtils.processTaskResponse(loadStatus, getActivity(), errorModel);
        if (isDone) {
            return;
        }
        if (loadStatus == LoadStatus.SUCCESS) {
            if (mCursor == null) {
                mReportList = new ArrayList<>(model.reportList);
                mTotalCount = model.totalCount;
                mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter = new ReportListAdapter(getContext(), this, model.reportList, mTotalCount, mRecyclerView);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mReportList.addAll(model.reportList);
                mAdapter.addReports(model.reportList);
            }
            mCursor = model.cursor;
        }
    }

    @Override
    public void onReportItemClicked(ReportModel reportModel) {
        if (mMainInterface != null) {
            mMainInterface.onReportSelected(reportModel);
        }
    }

    @Override
    public void onLoadMoreReports() {
        new ReportListTask(this, mCursor, mQuery).execute(getContext());
    }

    @Override
    public void onSearchQueryEntered(String query) {
        mQuery = query;
        mCursor = null;
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        new ReportListTask(this, null, mQuery).execute(getContext());
    }

    //region --- Fragment Lifecycle ---

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report_list, container, false);
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
        if (mReportList == null) {
            mRecyclerView.setVisibility(View.GONE);
            new ReportListTask(this, null, null).execute(getContext());
        } else {
            mProgressBar.setVisibility(View.GONE);
            mAdapter = new ReportListAdapter(getContext(), this, new ArrayList<>(mReportList), mTotalCount, mRecyclerView);
            mRecyclerView.setAdapter(mAdapter);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.report_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search:
                SearchDialog dialog = SearchDialog.newInstance(this, mQuery);
                dialog.show(getChildFragmentManager(), "Search");
                break;
        }
        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMainInterface = null;
    }

    //endregion

}
