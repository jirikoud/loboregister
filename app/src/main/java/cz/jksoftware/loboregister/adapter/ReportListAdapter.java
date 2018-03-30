package cz.jksoftware.loboregister.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
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

public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.ViewHolder> {

    @SuppressWarnings("unused")
    private static final String TAG = "ReportListAdapter";

    public interface ClickListener {
        void onAuthorItemClicked(String authorId);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View mViewContent;
        TextView mTextViewFullName;
        TextView mTextViewDate;
        TextView mTextViewBody;

        ViewHolder(View itemView) {
            super(itemView);
            mViewContent = itemView.findViewById(R.id.view_content);
            mTextViewFullName = itemView.findViewById(R.id.text_view_name);
            mTextViewDate = itemView.findViewById(R.id.text_view_date);
            mTextViewBody = itemView.findViewById(R.id.text_view_body);
        }

        void setModel(final ReportModel model) {
            mTextViewFullName.setText(model.getFullName());
            mTextViewDate.setText(mDateFormat.format(model.date));
            mTextViewBody.setText(model.body);
            mViewContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener.get() != null) {
                        mClickListener.get().onAuthorItemClicked(model.authorId);
                    }
                }
            });
        }
    }

    private LayoutInflater mInflater;
    private WeakReference<ClickListener> mClickListener;
    private List<ReportModel> mItemList;
    private DateFormat mDateFormat;

    public ReportListAdapter(Context context, ClickListener clickListener, List<ReportModel> itemList) {
        mInflater = LayoutInflater.from(context);
        mClickListener = new WeakReference<>(clickListener);
        mItemList = itemList;
        mDateFormat = SimpleDateFormat.getDateInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setModel(mItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }
}
