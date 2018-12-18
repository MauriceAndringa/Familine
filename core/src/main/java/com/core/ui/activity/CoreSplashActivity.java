package com.core.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.core.CoreApp;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.core.exception.QBResponseException;
import com.core.R;
import com.core.utils.ErrorUtils;
import com.core.utils.VersionUtils;

public abstract class CoreSplashActivity extends CoreBaseActivity {
    private static final int SPLASH_DELAY = 1500;

    private static Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_splash);
    }

    protected abstract String getAppName();

    protected abstract void proceedToTheNextActivity();

    protected boolean sampleConfigIsCorrect(){
        return CoreApp.getInstance().getQbConfigs() != null;
    }

    protected void proceedToTheNextActivityWithDelay() {
        mainThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                proceedToTheNextActivity();
            }
        }, SPLASH_DELAY);
    }

    protected boolean checkConfigsWithSnackbarError(){
        if (!sampleConfigIsCorrect()){
            showSnackbarErrorParsingConfigs();
            return false;
        }

        return true;
    }

    @Override
    protected void showSnackbarError(View rootLayout, @StringRes int resId, QBResponseException e, View.OnClickListener clickListener) {
        rootLayout = findViewById(R.id.layout_root);
        ErrorUtils.showSnackbar(rootLayout, resId, e, R.string.dlg_retry, clickListener);
    }

    protected void showSnackbarErrorParsingConfigs(){
        ErrorUtils.showSnackbar(findViewById(R.id.layout_root), R.string.error_parsing_configs, R.string.dlg_ok, null);
    }

    protected boolean checkSignIn() {
        return QBSessionManager.getInstance().getSessionParameters() != null;
    }
}
