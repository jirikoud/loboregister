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

import java.util.List;

import cz.jksoftware.loboregister.R;
import cz.jksoftware.loboregister.adapter.AuthorListAdapter;
import cz.jksoftware.loboregister.api.LoadStatus;
import cz.jksoftware.loboregister.api.model.AuthorModel;
import cz.jksoftware.loboregister.api.model.ErrorModel;
import cz.jksoftware.loboregister.api.task.AuthorListTask;
import cz.jksoftware.loboregister.api.util.FragmentApiUtils;
import cz.jksoftware.loboregister.interfaces.MainInterface;

/**
 * Created by Koudy on 3/30/2018.
 * List of register's authors
 */

public class AuthorListFragment extends Fragment implements AuthorListTask.ResultDelegate, AuthorListAdapter.ClickListener {

    @SuppressWarnings("unused")
    private static final String TAG = "AuthorListFragment";

    private MainInterface mMainInterface;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;

    @Override
    public void onAuthorListTaskFinished(LoadStatus loadStatus, List<AuthorModel> modelList, ErrorModel errorModel) {
        mProgressBar.setVisibility(View.GONE);
        boolean isDone = FragmentApiUtils.processTaskResponse(loadStatus, getActivity(), errorModel);
        if (isDone) {
            return;
        }
        if (loadStatus == LoadStatus.SUCCESS) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.setAdapter(new AuthorListAdapter(getContext(), this, modelList));
        }
    }

    @Override
    public void onAuthorItemClicked(AuthorModel model) {

    }

    //region --- Fragment Lifecycle ---

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_author_list, container, false);
        mProgressBar = rootView.findViewById(R.id.progress_bar);
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMainInterface = (MainInterface) getActivity();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), RecyclerView.VERTICAL));
        mRecyclerView.setVisibility(View.GONE);
        new AuthorListTask(this).execute(getContext());
    }

    //endregion

}
