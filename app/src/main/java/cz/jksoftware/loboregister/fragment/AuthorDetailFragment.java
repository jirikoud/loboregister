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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import cz.jksoftware.loboregister.R;
import cz.jksoftware.loboregister.adapter.ReportListAdapter;
import cz.jksoftware.loboregister.api.LoadStatus;
import cz.jksoftware.loboregister.api.model.AuthorModel;
import cz.jksoftware.loboregister.api.model.ErrorModel;
import cz.jksoftware.loboregister.api.model.ReportModel;
import cz.jksoftware.loboregister.api.task.AuthorDetailTask;
import cz.jksoftware.loboregister.api.util.FragmentApiUtils;
import cz.jksoftware.loboregister.infrastructure.StringUtils;
import cz.jksoftware.loboregister.interfaces.MainInterface;

/**
 * Created by Koudy on 3/31/2018.
 * Author detail and Reports
 */

public class AuthorDetailFragment extends Fragment implements AuthorDetailTask.ResultDelegate, ReportListAdapter.EventListener {

    @SuppressWarnings("unused")
    private static final String TAG = "ReportListFragment";

    public static final String EXTRA_AUTHOR_ID = "AuthorId";

    private MainInterface mMainInterface;

    private View mViewContent;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private ReportListAdapter mAdapter;
    private TextView mTextViewAuthor;
    private TextView mTextViewTotal;

    private AuthorModel mAuthorModel;
    private String mCursor;
    private ArrayList<ReportModel> mReportList;
    private int mTotalCount;

    @Override
    public void onAuthorDetailTaskFinished(LoadStatus loadStatus, String cursor, AuthorModel model, ErrorModel errorModel) {
        if (!StringUtils.isEqual(mCursor, cursor)) {
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
                mAuthorModel = model;
                mViewContent.setVisibility(View.VISIBLE);
                mAdapter = new ReportListAdapter(getContext(), this, model.reportList, mTotalCount, mRecyclerView);
                mRecyclerView.setAdapter(mAdapter);
                mTextViewAuthor.setText(model.getFullName());
                mTextViewTotal.setText(String.valueOf(model.totalReports));
            }
            else{
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
        new AuthorDetailTask(this, mAuthorModel.id, mCursor).execute(getContext());
    }

    //region --- Fragment Lifecycle ---

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_author_detail, container, false);
        mViewContent = rootView.findViewById(R.id.view_content);
        mProgressBar = rootView.findViewById(R.id.progress_bar);
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mTextViewAuthor = rootView.findViewById(R.id.text_view_author);
        mTextViewTotal = rootView.findViewById(R.id.text_view_total);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMainInterface = (MainInterface) getActivity();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), RecyclerView.VERTICAL));
        if (getArguments() != null) {
            String authorId = getArguments().getString(EXTRA_AUTHOR_ID);
            if (mAuthorModel == null) {
                mViewContent.setVisibility(View.GONE);
                new AuthorDetailTask(this, authorId, null).execute(getContext());
            } else {
                mProgressBar.setVisibility(View.GONE);
                mAdapter = new ReportListAdapter(getContext(), this, new ArrayList<>(mReportList), mTotalCount, mRecyclerView);
                mRecyclerView.setAdapter(mAdapter);
                mTextViewAuthor.setText(mAuthorModel.getFullName());
                mTextViewTotal.setText(String.valueOf(mAuthorModel.totalReports));
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMainInterface = null;
    }

    
    //endregion
}
