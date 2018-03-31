package cz.jksoftware.loboregister.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import cz.jksoftware.loboregister.R;
import cz.jksoftware.loboregister.api.model.ReportModel;

/**
 * Created by Koudy on 3/30/2018.
 * Adapter for RecyclerView with Reports
 */

public class ReportListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @SuppressWarnings("unused")
    private static final String TAG = "ReportListAdapter";

    private static final int VIEW_TYPE_REPORT = 0;
    private static final int VIEW_TYPE_PROGRESS = 1;

    public interface EventListener {
        void onReportItemClicked(ReportModel reportModel);

        void onLoadMoreReports();
    }

    class ReportViewHolder extends RecyclerView.ViewHolder {

        View mViewContent;
        TextView mTextViewFullName;
        TextView mTextViewDate;
        TextView mTextViewBody;

        ReportViewHolder(View itemView) {
            super(itemView);
            mViewContent = itemView.findViewById(R.id.view_content);
            mTextViewFullName = itemView.findViewById(R.id.text_view_name);
            mTextViewDate = itemView.findViewById(R.id.text_view_date);
            mTextViewBody = itemView.findViewById(R.id.text_view_body);
        }

        void setModel(final ReportModel model) {
            mTextViewFullName.setText(model.getFullName());
            mTextViewDate.setText(mDateFormat.format(model.date));
            mTextViewBody.setText(model.getBodyShort());
            mViewContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mEventListener.get() != null) {
                        mEventListener.get().onReportItemClicked(model);
                    }
                }
            });
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        ProgressViewHolder(View itemView) {
            super(itemView);
        }
    }

    private LayoutInflater mInflater;
    private WeakReference<EventListener> mEventListener;
    private List<ReportModel> mItemList;
    private int mTotalCount;
    private DateFormat mDateFormat;
    private boolean mIsLoading;
    private int visibleThreshold = 5;

    public ReportListAdapter(Context context, EventListener eventListener, List<ReportModel> itemList, int totalCount, RecyclerView recyclerView) {
        mInflater = LayoutInflater.from(context);
        mEventListener = new WeakReference<>(eventListener);
        mItemList = itemList;
        mTotalCount = totalCount;
        mDateFormat = SimpleDateFormat.getDateInstance();
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!mIsLoading && mTotalCount > mItemList.size() && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (mEventListener.get() != null) {
                            mEventListener.get().onLoadMoreReports();
                        }
                        mIsLoading = true;
                    }
                }
            });
        }
    }

    public void addReports(List<ReportModel> reportList) {
        int startIndex = mItemList.size();
        mItemList.addAll(reportList);
        if (mItemList.size() != mTotalCount) {
            notifyItemRangeInserted(startIndex, reportList.size());
        } else {
            notifyDataSetChanged();
        }
        mIsLoading = false;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mItemList.size()){
            return VIEW_TYPE_REPORT;
        }
        return VIEW_TYPE_PROGRESS;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_REPORT) {
            View view = mInflater.inflate(R.layout.item_report, parent, false);
            return new ReportViewHolder(view);
        } else {
            View view = mInflater.inflate(R.layout.item_progress, parent, false);
            return new ProgressViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ReportViewHolder) {
            ((ReportViewHolder) holder).setModel(mItemList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mItemList.size() + (mItemList.size() == mTotalCount ? 0 : 1);
    }
}
