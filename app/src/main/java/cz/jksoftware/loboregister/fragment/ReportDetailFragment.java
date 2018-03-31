package cz.jksoftware.loboregister.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import cz.jksoftware.loboregister.R;
import cz.jksoftware.loboregister.api.model.ReportModel;
import cz.jksoftware.loboregister.infrastructure.StringUtils;
import cz.jksoftware.loboregister.interfaces.MainInterface;

/**
 * Created by Koudy on 3/31/2018.
 * Detail of Report
 */

public class ReportDetailFragment extends Fragment {

    @SuppressWarnings("unused")
    private static final String TAG = "ReportListFragment";

    public static final String EXTRA_REPORT = "Report";

    @SuppressWarnings("FieldCanBeLocal")
    private MainInterface mMainInterface;

    private TextView mTextViewAuthor;
    private TextView mTextViewDate;
    private TextView mTextViewPublished;
    private TextView mTextViewBody;
    private TextView mTextViewReceived;
    private TextView mTextViewProvided;

    //region --- Fragment Lifecycle ---

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report_detail, container, false);
        mTextViewAuthor = rootView.findViewById(R.id.text_view_author);
        mTextViewDate = rootView.findViewById(R.id.text_view_date);
        mTextViewPublished = rootView.findViewById(R.id.text_view_published);
        mTextViewBody = rootView.findViewById(R.id.text_view_body);
        mTextViewReceived = rootView.findViewById(R.id.text_view_received);
        mTextViewProvided = rootView.findViewById(R.id.text_view_provided);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMainInterface = (MainInterface) getActivity();
        if (getArguments() != null) {
            ReportModel reportModel = (ReportModel) getArguments().getSerializable(EXTRA_REPORT);
            if (reportModel != null) {
                DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
                DateFormat dateTimeFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
                mTextViewAuthor.setText(reportModel.getFullName());
                mTextViewDate.setText(StringUtils.formatDate(dateFormat, reportModel.date));
                mTextViewPublished.setText(StringUtils.formatDate(dateTimeFormat, reportModel.published));
                mTextViewBody.setText(reportModel.body);
                mTextViewReceived.setText(reportModel.received);
                mTextViewProvided.setText(reportModel.provided);
            }
        }
    }

    //endregion
}
