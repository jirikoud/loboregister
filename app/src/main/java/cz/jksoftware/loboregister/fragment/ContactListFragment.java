package cz.jksoftware.loboregister.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.jksoftware.loboregister.R;
import cz.jksoftware.loboregister.interfaces.MainInterface;

/**
 * Created by Koudy on 3/30/2018.
 * List of lobbyist contacts
 */

public class ContactListFragment extends Fragment {

    @SuppressWarnings("unused")
    private static final String TAG = "ContactListFragment";

    private MainInterface mMainInterface;

    //region --- Fragment Lifecycle ---

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_list, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMainInterface = (MainInterface)getActivity();
    }

    //endregion

}
