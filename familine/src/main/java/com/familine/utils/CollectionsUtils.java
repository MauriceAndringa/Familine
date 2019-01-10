package com.familine.utils;

import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.Collection;

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

public class CollectionsUtils {

    public static String makeStringFromUsersFullNames(ArrayList<QBUser> allUsers) {
        StringifyArrayList<String> usersNames = new StringifyArrayList<>();

            for (QBUser usr : allUsers) {
                if (usr.getFullName() != null) {
                    usersNames.add(usr.getFullName());
                } else if (usr.getId() != null) {
                    usersNames.add(String.valueOf(usr.getId()));
                }
            }
        return usersNames.getItemsAsString().replace(",",", ");
    }

    public static ArrayList<Integer> getIdsSelectedOpponents(Collection<QBUser> selectedUsers){
        ArrayList<Integer> opponentsIds = new ArrayList<>();
        if (!selectedUsers.isEmpty()){
            for (QBUser qbUser : selectedUsers){
                opponentsIds.add(qbUser.getId());
            }
        }

        return opponentsIds;
    }
}
