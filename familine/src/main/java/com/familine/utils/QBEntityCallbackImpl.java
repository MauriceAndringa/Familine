package com.familine.utils;

import android.os.Bundle;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

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

public class QBEntityCallbackImpl <T> implements QBEntityCallback<T> {

    @Override
    public void onSuccess(T result, Bundle params) {

    }

    @Override
    public void onError(QBResponseException responseException) {

    }
}
