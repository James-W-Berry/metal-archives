package com.android.metal_archives;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Author: James Berry
 * Description: fragment view for notification screen
 */

public class NotificationFragment extends Fragment {
    public static NotificationFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt("PAGE_NUM", page);
        args.putString("PAGE_NAME", "Notification");

        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_fragment, container, false);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getActivity().setTitle("Notifications");
            getActivity().findViewById(R.id.search_text).setVisibility(View.GONE);
            getActivity().findViewById(R.id.clear_search).setVisibility(View.GONE);
            getActivity().findViewById(R.id.search_layout).setVisibility(View.GONE);

        }
    }
}
