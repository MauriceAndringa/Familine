package com.familine.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.core.utils.SharedPrefsHelper;
import com.core.utils.Toaster;
import com.crashlytics.android.Crashlytics;
import com.familine.R;
import com.familine.adapters.OpponentsAdapter;
import com.familine.db.QbUsersDbManager;
import com.familine.services.CallService;
import com.familine.utils.CollectionsUtils;
import com.familine.utils.Consts;
import com.familine.utils.PermissionsChecker;
import com.familine.utils.PushNotificationSender;
import com.familine.utils.UsersUtils;
import com.familine.utils.WebRtcSessionManager;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.services.SubscribeService;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;

/**
 * QuickBlox team
 */
public class OpponentsActivity extends BaseActivity {
    private static final String TAG = OpponentsActivity.class.getSimpleName();

    private static final long ON_ITEM_CLICK_DELAY = TimeUnit.SECONDS.toMillis(10);

    private OpponentsAdapter opponentsAdapter;
    private ListView opponentsListView;
    private QBUser currentUser;
    private ArrayList<QBUser> currentOpponentsList;
    private QbUsersDbManager dbManager;
    private boolean isRunForCall;
    private WebRtcSessionManager webRtcSessionManager;

    private PermissionsChecker checker;

    public static void start(Context context, boolean isRunForCall) {
        Intent intent = new Intent(context, OpponentsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(Consts.EXTRA_IS_STARTED_FOR_CALL, isRunForCall);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_opponents);

        initFields();
        initDefaultActionBar();
        initUi();
        startLoadUsers();

        if (isRunForCall && webRtcSessionManager.getCurrentSession() != null) {
            CallActivity.start(OpponentsActivity.this, true);
        }

        checker = new PermissionsChecker(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUi();
        initUsersList();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null) {
            isRunForCall = intent.getExtras().getBoolean(Consts.EXTRA_IS_STARTED_FOR_CALL);
            if (isRunForCall && webRtcSessionManager.getCurrentSession() != null) {
                CallActivity.start(OpponentsActivity.this, true);
            }
        }
    }

    @Override
    protected View getSnackbarAnchorView() {
        return findViewById(R.id.list_opponents);
    }

    private void startPermissionsActivity(boolean checkOnlyAudio) {
        PermissionsActivity.startActivity(this, checkOnlyAudio, Consts.PERMISSIONS);
    }

    private void initFields() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isRunForCall = extras.getBoolean(Consts.EXTRA_IS_STARTED_FOR_CALL);
        }

        currentUser = sharedPrefsHelper.getQbUser();
        dbManager = QbUsersDbManager.getInstance(getApplicationContext());
        webRtcSessionManager = WebRtcSessionManager.getInstance(getApplicationContext());
    }

    private void startLoadUsers() {
        showProgressDialog(R.string.dlg_loading_opponents);
        String currentRoomName = currentUser.getTags().get(0);
        requestExecutor.loadUsersByTag(currentRoomName, new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> result, Bundle params) {
                hideProgressDialog();
                dbManager.saveAllUsers(result, true);
                initUsersList();
            }

            @Override
            public void onError(QBResponseException responseException) {
                hideProgressDialog();
                showErrorSnackbar(R.string.loading_users_error, responseException, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startLoadUsers();
                    }
                });
            }
        });
    }

    private void initUi() {
        opponentsListView = (ListView) findViewById(R.id.list_opponents);
        FloatingActionButton fab = findViewById(R.id.fab);

        // Show/hide floating action button based on role
        String userRole = currentUser.getExternalId();
        if (userRole == null) return;

        if (userRole.equals("0")) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareLink();
                }
            });
        } else {
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            fab.setLayoutParams(p);
            fab.setVisibility(View.GONE);
        }
    }

    private void updateUi() {
        opponentsListView = (ListView) findViewById(R.id.list_opponents);
        FloatingActionButton fab = findViewById(R.id.fab);

        // Show/hide floating action button based on role
        String userRole = sharedPrefsHelper.getQbUser().getExternalId();
        if (userRole == null) return;

        if (userRole.equals("0")) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareLink();
                }
            });
        } else {
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            fab.setLayoutParams(p);
            fab.setVisibility(View.GONE);
        }
    }

    private boolean isCurrentOpponentsListActual(ArrayList<QBUser> actualCurrentOpponentsList) {
        boolean equalActual = actualCurrentOpponentsList.retainAll(currentOpponentsList);
        boolean equalCurrent = currentOpponentsList.retainAll(actualCurrentOpponentsList);
        return !equalActual && !equalCurrent;
    }

    private void initUsersList() {
        if (currentOpponentsList != null) {
            ArrayList<QBUser> actualCurrentOpponentsList = dbManager.getAllUsers();
            actualCurrentOpponentsList.remove(sharedPrefsHelper.getQbUser());
            if (isCurrentOpponentsListActual(actualCurrentOpponentsList)) {
                return;
            }
        }
        proceedInitUsersList();
    }

    private void proceedInitUsersList() {
        currentOpponentsList = dbManager.getAllUsers();
        currentOpponentsList.remove(sharedPrefsHelper.getQbUser());

        opponentsAdapter = new OpponentsAdapter(this, currentOpponentsList);
        opponentsAdapter.setSelectedItemsCountsChangedListener(new OpponentsAdapter.SelectedItemsCountsChangedListener() {
            @Override
            public void onClick(int position) {
                boolean isEmpty = opponentsAdapter.getSelectedItems().isEmpty();

                if (!isEmpty) {
                    return;
                }

                callUser(position);
            }

            @Override
            public void onCountSelectedItemsChanged() {
                boolean hasItems = opponentsAdapter.getSelectedItems().isEmpty();
                updateActionBar(hasItems);
            }
        });

        opponentsListView.setAdapter(opponentsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (opponentsAdapter != null && !opponentsAdapter.getSelectedItems().isEmpty()) {
            getMenuInflater().inflate(R.menu.activity_selected_opponents, menu);
        } else {
            getMenuInflater().inflate(R.menu.activity_opponents, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.update_opponents_list:
                startLoadUsers();
                return true;

            case R.id.settings:
                showSettings();
                return true;

            case R.id.delete_user:
                deleteUser();
                return true;

            case R.id.log_out:
                logOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isLoggedInChat() {
        if (!QBChatService.getInstance().isLoggedIn()) {
            Toaster.shortToast(R.string.dlg_signal_error);
            tryReLoginToChat();
            return false;
        }
        return true;
    }

    private void tryReLoginToChat() {
        if (sharedPrefsHelper.hasQbUser()) {
            QBUser qbUser = sharedPrefsHelper.getQbUser();
            CallService.start(this, qbUser);
        }
    }

    private void showSettings() {
        SettingsActivity.start(this);
    }

    //Creates the link for adding contacts
    private void shareLink() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/url");
        StringifyArrayList<String> tags = currentUser.getTags();
        String shareUrl = "Can you help me with my smartphone? if you click on this link I can call you for help! \n https://app.familine.com/?tag=" + tags.get(0);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareUrl);
        startActivity(Intent.createChooser(sharingIntent, "Invite helper by link"));
    }

    private void startCall(Integer userId) {
        ArrayList<Integer> opponentsList = new ArrayList<>();
        opponentsList.add(userId);

        QBRTCTypes.QBConferenceType conferenceType = QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;
        QBRTCClient qbrtcClient = QBRTCClient.getInstance(getApplicationContext());
        QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);
        WebRtcSessionManager.getInstance(this).setCurrentSession(newQbRtcSession);

        CallActivity.start(this, false);
        opponentsAdapter.clearSelection();
    }

    private void updateActionBar(boolean isUserSelected) {
        initActionBar(isUserSelected);
        invalidateOptionsMenu();
    }

    private void initActionBar(boolean isUserSelected) {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar == null) {
            return;
        }

        actionBar.setTitle(getTitle());

        if (isUserSelected) {
            actionBar.setDisplayShowTitleEnabled(true);
        } else {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void callUser(int position) {
        if (isLoggedInChat()) {
            QBUser user = opponentsAdapter.getItem(position);
            startCall(user.getId());
        }
        if (checker.lacksPermissions(Consts.PERMISSIONS)) {
            startPermissionsActivity(false);
        }
    }

    private void deleteUser() {
        Object[] selectedUsers = opponentsAdapter.getSelectedItems().toArray();
        QBUser user = (QBUser) selectedUsers[0];
        StringifyArrayList<String> tagList = user.getTags();
        String userTag = sharedPrefsHelper.getQbUser().getTags().get(0);

        if ( !tagList.contains(userTag) ) {
            Toaster.shortToast(R.string.delete_error);
            return;
        }

        QBUser qbUser = new QBUser();
        qbUser.setId( user.getId() );
        tagList.remove(userTag);
        qbUser.setTags(tagList);

        QBUsers.updateUser(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                Toaster.shortToast(R.string.delete_success);
                opponentsAdapter.clearSelection();
                updateActionBar(true);
            }

            @Override
            public void onError(QBResponseException e) {
                Toaster.shortToast(R.string.delete_error);
                opponentsAdapter.clearSelection();
                updateActionBar(true);
            }
        });
    }

    private void logOut() {
        unsubscribeFromPushes();
        startLogoutCommand();
        removeAllUserData();
        startLoginActivity();
    }

    private void startLogoutCommand() {
        CallService.logout(this);
    }

    private void unsubscribeFromPushes() {
        SubscribeService.unSubscribeFromPushes(this);
    }

    private void removeAllUserData() {
        UsersUtils.removeUserData(getApplicationContext());
        requestExecutor.deleteCurrentUser(currentUser.getId(), new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                Log.d(TAG, "Current user was deleted from QB");
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "Current user wasn't deleted from QB " + e);
            }
        });
    }

    private void startLoginActivity() {
        LoginActivity.start(this);
        finish();
    }
}