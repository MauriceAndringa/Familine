package com.familine.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.familine.utils.CollectionsUtils;
import com.familine.utils.Consts;
import com.familine.utils.UsersUtils;
import com.familine.utils.WebRtcSessionManager;
import com.quickblox.chat.QBChatService;
import com.familine.R;
import com.familine.activities.CallActivity;
import com.familine.db.QbUsersDbManager;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import java.util.ArrayList;

public abstract class BaseConversationFragment extends BaseToolBarFragment implements CallActivity.CurrentCallStateCallback {

    private static final String TAG = BaseConversationFragment.class.getSimpleName();
    protected QbUsersDbManager dbManager;
    protected WebRtcSessionManager sessionManager;
    private boolean isIncomingCall;
    protected QBRTCSession currentSession;
    protected ArrayList<QBUser> opponents;
    private QBRTCTypes.QBConferenceType qbConferenceType;

    private ImageButton hangUpVideoCall;
    protected ConversationFragmentCallbackListener conversationFragmentCallbackListener;
    protected Chronometer timerChronometer;
    private boolean isMessageProcessed;
    protected boolean isStarted;
    protected View outgoingOpponentsRelativeLayout;
    protected TextView allOpponentsTextView;
    protected TextView ringingTextView;
    protected QBUser currentUser;

    public static BaseConversationFragment newInstance(BaseConversationFragment baseConversationFragment, boolean isIncomingCall) {
        Log.d(TAG, "isIncomingCall =  " + isIncomingCall);
        Bundle args = new Bundle();
        args.putBoolean(Consts.EXTRA_IS_INCOMING_CALL, isIncomingCall);

        baseConversationFragment.setArguments(args);

        return baseConversationFragment;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conversationFragmentCallbackListener.addCurrentCallStateCallback(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        sessionManager = WebRtcSessionManager.getInstance(getActivity());
        currentSession = sessionManager.getCurrentSession();
        if (currentSession == null) {
            Log.d(TAG, "currentSession = null onCreateView");
            return view;
        }
        initFields();
        initViews(view);
        initActionBar();
        initButtonsListener();
        prepareAndShowOutgoingScreen();

        return view;
    }

    private void prepareAndShowOutgoingScreen() {
        configureOutgoingScreen();
        allOpponentsTextView.setText(CollectionsUtils.makeStringFromUsersFullNames(opponents));
    }

    protected abstract void configureOutgoingScreen();

    private void initActionBar() {
        configureToolbar();
        configureActionBar();
    }

    protected abstract void configureActionBar();

    protected abstract void configureToolbar();

    protected void initFields() {
        currentUser = QBChatService.getInstance().getUser();
        dbManager = QbUsersDbManager.getInstance(getActivity().getApplicationContext());
        sessionManager = WebRtcSessionManager.getInstance(getActivity());
        currentSession = sessionManager.getCurrentSession();

        if (getArguments() != null) {
            isIncomingCall = getArguments().getBoolean(Consts.EXTRA_IS_INCOMING_CALL);
        }

        initOpponentsList();

        qbConferenceType = currentSession.getConferenceType();

        Log.d(TAG, "opponents: " + opponents.toString());
        Log.d(TAG, "currentSession " + currentSession.toString());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (currentSession == null) {
            Log.d(TAG, "currentSession = null onStart");
            return;

        }

        if (currentSession.getState() != QBRTCSession.QBRTCSessionState.QB_RTC_SESSION_CONNECTED) {
            if (isIncomingCall) {
                currentSession.acceptCall(null);
            } else {
                currentSession.startCall(null);
            }
            isMessageProcessed = true;
        }
    }

    @Override
    public void onDestroy() {
        conversationFragmentCallbackListener.removeCurrentCallStateCallback(this);
        super.onDestroy();
    }

    protected void initViews(View view) {
        hangUpVideoCall = (ImageButton) view.findViewById(R.id.button_hangup_call);
        outgoingOpponentsRelativeLayout = view.findViewById(R.id.layout_background_outgoing_screen);
        allOpponentsTextView = (TextView) view.findViewById(R.id.text_outgoing_opponents_names);
        ringingTextView = (TextView) view.findViewById(R.id.text_ringing);

        if (isIncomingCall) {
            hideOutgoingScreen();
        }
    }

    protected void initButtonsListener() {

        hangUpVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    hangUpVideoCall.setEnabled(true);
                    hangUpVideoCall.setActivated(true);

                    conversationFragmentCallbackListener.onHangUpCurrentSession();
                    Log.d(TAG, "Call is stopped");
            }
        });
    }

    private void startTimer() {
        if (!isStarted) {
            timerChronometer.setVisibility(View.VISIBLE);
            timerChronometer.setBase(SystemClock.elapsedRealtime());
            timerChronometer.start();
            isStarted = true;
        }
    }

    private void stopTimer() {
        if (timerChronometer != null) {
            timerChronometer.stop();
            isStarted = false;
        }
    }

    private void hideOutgoingScreen() {
        outgoingOpponentsRelativeLayout.setVisibility(View.GONE);
    }

    @Override
    public void onCallStarted() {
        hideOutgoingScreen();
        startTimer();
    }

    @Override
    public void onCallStopped() {
        if (currentSession == null) {
            Log.d(TAG, "currentSession = null onCallStopped");
            return;
        }
        stopTimer();
    }

    @Override
    public void onOpponentsListUpdated(ArrayList<QBUser> newUsers) {
        initOpponentsList();
    }

    private void initOpponentsList() {
        Log.v("UPDATE_USERS", "super initOpponentsList()");
        ArrayList<QBUser> usersFromDb = dbManager.getUsersByIds(currentSession.getOpponents());
        opponents = UsersUtils.getListAllUsersFromIds(usersFromDb, currentSession.getOpponents());

        QBUser caller = dbManager.getUserById(currentSession.getCallerID());
        if (caller == null) {
            caller = new QBUser(currentSession.getCallerID());
            caller.setFullName(String.valueOf(currentSession.getCallerID()));
        }

        if (isIncomingCall) {
            opponents.add(caller);
            opponents.remove(QBChatService.getInstance().getUser());
        }
    }

}