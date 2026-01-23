package com.buuz135.simpleclaims.util;

import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.permissions.provider.PermissionProvider;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Permissions {

    public static final String CLAIM_CHUNK_AMOUNT = "simpleclaims.party.claim_chunk_amount";

    public static int getPermissionClaimAmount(UUID uuid) { //TODO Check of admin parties
        int amount = -1;
        for (PermissionProvider provider : PermissionsModule.get().getProviders()) {
            if (provider.getName().equals("LuckPerms")) {
                for (String perm : LuckPermsHelper.getPerms(uuid)) {
                    try {
                        var parsed = Integer.parseInt(perm.replace(CLAIM_CHUNK_AMOUNT + ".", ""));
                        if (parsed > amount) amount = parsed;
                    } catch (NumberFormatException e) {
                    }
                }
                return amount;
            }
            var userNodes = new HashSet<String>();
            userNodes.addAll(provider.getUserPermissions(uuid));
            for (String s : provider.getGroupsForUser(uuid)) {
                userNodes.addAll(provider.getGroupPermissions(s));
            }
            for (String node : userNodes) {
                if (node.contains(CLAIM_CHUNK_AMOUNT)) {
                    try {
                        var parsed = Integer.parseInt(node.replace(CLAIM_CHUNK_AMOUNT + ".", ""));
                        if (parsed > amount) amount = parsed;
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }

        return amount;
    }
}
