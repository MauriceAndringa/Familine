package com.familine.activities;

import android.os.Bundle;
import android.view.View;

import com.core.utils.Toaster;
import com.familine.R;
import com.familine.adapters.OpponentsAdapter;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class InviteActivity extends BaseActivity {

    private QBUser currentUser;
    private QBUser opponentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opponents);
        showProgressDialog(R.string.dlg_processing_invite);

        int currentUserId = sharedPrefsHelper.getQbUser().getId();
        QBUsers.getUser(currentUserId).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                currentUser = qbUser;

                String newTag = getIntent().getData().getQueryParameter("tag");
                boolean updateUser = !userHasTag(currentUser, newTag);


                if (updateUser) {
                    addUserTag(currentUser, newTag);

                }
                String newId = getIntent().getData().getQueryParameter("id");
                Integer opponentId = Integer.parseInt(newId);

                QBUsers.getUser(opponentId).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {

                        opponentUser = qbUser;

                        StringifyArrayList<String> tags = currentUser.getTags();
                        boolean updateUser = !userHasTag(opponentUser, tags.get(0));

                        if (updateUser){
                            addUserTag(opponentUser, tags.get(0));
                        }


                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toaster.shortToast(R.string.invite_error);
                        hideProgressDialog();
                        OpponentsActivity.start(InviteActivity.this, false);
                        finish();
                    }
                });
                
                hideProgressDialog();
                OpponentsActivity.start(InviteActivity.this, false);
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                Toaster.shortToast(R.string.invite_error);
                hideProgressDialog();
                OpponentsActivity.start(InviteActivity.this, false);
                finish();
            }
        });




    }

    private boolean userHasTag(QBUser user, String tag) {
        boolean hasTag = false;
        StringifyArrayList<String> tagList = user.getTags();

        for (String tagItem : tagList) {
            if (tagItem.equals(tag)) {
                hasTag = true;
                break;
            }
        }

        return hasTag;
    }

    private void addUserTag(QBUser user, String newTag) {
        QBUser qbUser = new QBUser();

        if ( null == user.getId() ) {
            Toaster.shortToast(R.string.invite_error);
            return;
        }

        qbUser.setId(user.getId());

        StringifyArrayList<String> newTagList = user.getTags();
        newTagList.add(newTag);
        qbUser.setTags(newTagList);

        //set tag to user
        QBUsers.updateUser(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                Toaster.shortToast(R.string.invite_success);
            }

            @Override
            public void onError(QBResponseException e) {
                Toaster.shortToast(R.string.invite_error);
            }
        });
    }

    @Override
    protected View getSnackbarAnchorView() {
        return null;
    }
}
