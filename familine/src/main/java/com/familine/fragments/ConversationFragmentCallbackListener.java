package com.familine.fragments;

import com.familine.activities.CallActivity;
import com.quickblox.videochat.webrtc.callbacks.QBRTCSessionEventsCallback;
import com.quickblox.videochat.webrtc.callbacks.QBRTCSessionStateCallback;

/**
 * Created by tereha on 23.05.16.
 */
public interface ConversationFragmentCallbackListener {

    void addTCClientConnectionCallback(QBRTCSessionStateCallback clientConnectionCallbacks);
    void removeRTCClientConnectionCallback(QBRTCSessionStateCallback clientConnectionCallbacks);

    void addRTCSessionEventsCallback(QBRTCSessionEventsCallback eventsCallback);
    void removeRTCSessionEventsCallback(QBRTCSessionEventsCallback eventsCallback);

    void addCurrentCallStateCallback (CallActivity.CurrentCallStateCallback currentCallStateCallback);
    void removeCurrentCallStateCallback (CallActivity.CurrentCallStateCallback currentCallStateCallback);

    void addOnChangeAudioDeviceCallback(CallActivity.OnChangeAudioDevice onChangeDynamicCallback);
    void removeOnChangeAudioDeviceCallback(CallActivity.OnChangeAudioDevice onChangeDynamicCallback);

    void onSetAudioEnabled(boolean isAudioEnabled);

    void onSwitchAudio();

    void onHangUpCurrentSession();

    void onStartScreenSharing();
}
