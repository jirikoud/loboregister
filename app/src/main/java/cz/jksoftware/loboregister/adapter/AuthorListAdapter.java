package cz.jksoftware.loboregister.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import cz.jksoftware.loboregister.R;
import cz.jksoftware.loboregister.api.model.AuthorModel;

/**
 * Created by Koudy on 3/30/2018.
 * Adapter for RecyclerView with Authors
 */

public class AuthorListAdapter extends RecyclerView.Adapter<AuthorListAdapter.ViewHolder> {

    @SuppressWarnings("unused")
    private static final String TAG = "AuthorListAdapter";

    public interface ClickListener {
        void onAuthorItemClicked(AuthorModel model);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View mViewContent;
        TextView mTextViewFullName;
        TextView mTextViewTotal;

        ViewHolder(View itemView) {
            super(itemView);
            mViewContent = itemView.findViewById(R.id.view_content);
            mTextViewFullName = itemView.findViewById(R.id.text_view_name);
            mTextViewTotal = itemView.findViewById(R.id.text_view_total);
        }

        void setModel(final AuthorModel model) {
            mTextViewFullName.setText(model.getFullName());
            mTextViewTotal.setText(String.valueOf(model.totalReports));
            mViewContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener.get() != null) {
                        mClickListener.get().onAuthorItemClicked(model);
                    }
                }
            });
        }
    }

    private LayoutInflater mInflater;
    private WeakReference<ClickListener> mClickListener;
    private List<AuthorModel> mItemList;

    public AuthorListAdapter(Context context, ClickListener clickListener, List<AuthorModel> itemList) {
        mInflater = LayoutInflater.from(context);
        mClickListener = new WeakReference<>(clickListener);
        mItemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_author, parent, false);
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
