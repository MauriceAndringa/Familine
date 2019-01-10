package com.familine.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.core.utils.Toaster;
import com.familine.R;
import com.familine.adapters.OpponentsAdapter;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

/**
 * Familine Team:
 *
 * Andringa,    Maurice
 * Chen,        Eric
 * Dons,        Henrik
 * Vallentgoed, Timon
 * Verhoek,     Karen
 *
 * Original Source : Quickblox
 * Code is commented by Familine team, Not commented part are self explanatory
 */

public class InviteActivity extends BaseActivity {
    private QBUser currentUser;

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

                finishActivity();
            }

            @Override
            public void onError(QBResponseException e) {
                Toaster.shortToast(R.string.invite_error);
                finishActivity();
            }
        });
    }

    //check if tag exists in tag list
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

    //adding tag to user
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

    private void finishActivity() {
        hideProgressDialog();
        OpponentsActivity.start(InviteActivity.this, false);
        finish();
    }

    @Override
    protected View getSnackbarAnchorView() {
        return null;
    }
}
