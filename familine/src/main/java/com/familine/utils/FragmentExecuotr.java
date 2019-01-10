package com.familine.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

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

public class FragmentExecuotr {

    public static void addFragment(FragmentManager fragmentManager, int containerId, Fragment fragment, String tag) {
        fragmentManager.beginTransaction().replace(containerId, fragment, tag).commitAllowingStateLoss();
    }

    public static void addFragmentAtTop(FragmentManager fragmentManager, int containerId, Fragment fragment, String tag) {
        fragmentManager.beginTransaction().add(containerId, fragment, tag).commitAllowingStateLoss();
    }

    public static void addFragmentWithBackStack(FragmentManager fragmentManager, int containerId, Fragment fragment, String tag) {
        fragmentManager.beginTransaction().replace(containerId, fragment, tag).addToBackStack(null).commitAllowingStateLoss();
    }

    public static void removeFragment(FragmentManager fragmentManager, Fragment fragment) {
        fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
    }
}
