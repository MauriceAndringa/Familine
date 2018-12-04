package com.familine.services.gcm;

import android.os.Bundle;
import android.util.Log;

import com.familine.services.CallService;
import com.google.android.gms.gcm.GcmListenerService;
import com.core.utils.SharedPrefsHelper;
import com.core.utils.constant.GcmConsts;
import com.quickblox.users.model.QBUser;

/**
 * Created by tereha on 13.05.16.
 */
public class GcmPushListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();

        if (sharedPrefsHelper.hasQbUser()) {
            QBUser qbUser = sharedPrefsHelper.getQbUser();
            startLoginService(qbUser);
        }
    }

    private void startLoginService(QBUser qbUser){
        CallService.start(this, qbUser);
    }
}