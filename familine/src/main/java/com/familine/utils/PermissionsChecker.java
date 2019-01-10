package com.familine.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

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

public class PermissionsChecker {
    private final Context context;

    public PermissionsChecker(Context context) {
        this.context = context;
    }

    public boolean lacksPermissions(String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED;
    }

}