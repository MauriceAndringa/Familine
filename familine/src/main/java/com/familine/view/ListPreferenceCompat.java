package com.familine.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.ListPreference;
import android.text.TextUtils;
import android.util.AttributeSet;

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

public class ListPreferenceCompat extends ListPreference {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ListPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ListPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ListPreferenceCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListPreferenceCompat(Context context) {
        super(context);
    }

    @Override
    public void setValue(String value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            super.setValue(value);
        } else {
            String oldValue = getValue();
            super.setValue(value);
            if (!TextUtils.equals(value, oldValue)) {
                notifyChanged();
            }
        }
    }
}