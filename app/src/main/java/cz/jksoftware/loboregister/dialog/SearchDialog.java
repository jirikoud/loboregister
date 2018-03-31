package cz.jksoftware.loboregister.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.lang.ref.WeakReference;

import cz.jksoftware.loboregister.R;
import cz.jksoftware.loboregister.infrastructure.ViewUtils;

/**
 * Created by Koudy on 3/31/2018.
 * Dialog for entering search query
 */

public class SearchDialog extends DialogFragment {

    public interface ResultListener {
        void onSearchQueryEntered(String query);
    }

    private WeakReference<ResultListener> mListener;
    private String mQuery;
    private EditText mTextEditQuery;
    private Button mButtonConfirm;
    private Button mButtonCancel;

    public SearchDialog() {
        // Empty constructor required for dialog fragment.
    }

    public static SearchDialog newInstance(ResultListener listener, String query) {
        SearchDialog dialog = new SearchDialog();
        dialog.initialize(listener, query);
        return dialog;
    }

    protected void initialize(ResultListener listener, String query) {
        mListener = new WeakReference<>(listener);
        mQuery = query;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_search, container, false);

        mTextEditQuery = view.findViewById(R.id.text_edit_search);
        mButtonConfirm = view.findViewById(R.id.button_confirm);
        mButtonCancel = view.findViewById(R.id.button_cancel);

        mTextEditQuery.setText(mQuery);
        mButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtils.hideKeyboard(getContext(), mTextEditQuery);
                if (mListener.get() != null) {
                    mListener.get().onSearchQueryEntered(mTextEditQuery.getText().toString());
                }
                dismiss();
            }
        });
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtils.hideKeyboard(getContext(), mTextEditQuery);
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
