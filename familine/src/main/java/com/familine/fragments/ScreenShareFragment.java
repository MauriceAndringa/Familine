package com.familine.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.familine.R;


public class ScreenShareFragment extends BaseToolBarFragment {

    public static final String TAG = ScreenShareFragment.class.getSimpleName();
    private OnSharingEvents onSharingEvents;
    private ImageButton hangUpVideoCall;
    private ConversationFragmentCallbackListener conversationFragmentCallbackListener;
    private View view;

    @Override
    int getFragmentLayout() {
        return R.layout.fragment_pager;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  super.onCreateView(inflater, container, savedInstanceState);

        MyAdapter adapter = new MyAdapter(getChildFragmentManager());

        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        hangUpVideoCall = (ImageButton) view.findViewById(R.id.button_hangup_call);
        initButtonsListener();
        pager.setAdapter(adapter);

        //toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            conversationFragmentCallbackListener = (ConversationFragmentCallbackListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ConversationFragmentCallbackListener");
        }
    }

    protected void initButtonsListener() {

        hangUpVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hangUpVideoCall.setEnabled(false);
                hangUpVideoCall.setActivated(false);

                conversationFragmentCallbackListener.onHangUpCurrentSession();
                Log.d(TAG, "Call is stopped");
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.screen_share_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button_hangup_call:
                Log.d(TAG, "stop_screen_share");
                onSharingEvents.onStopPreview();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onSharingEvents = (OnSharingEvents) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSharingEvents = null;
    }

    public interface OnSharingEvents{
        void onStopPreview();
    }

    public static ScreenShareFragment newIntstance() {
        return new ScreenShareFragment();
    }

    public static class MyAdapter extends FragmentPagerAdapter {
        private static final int NUM_ITEMS = 1;

        private int[] images = {R.drawable.pres_img};

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            return PreviewFragment.newInstance(images[position]);
        }
    }

}
