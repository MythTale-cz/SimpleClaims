package com.buuz135.simpleclaims.util;

import com.hypixel.hytale.server.core.universe.Universe;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class LuckPermsHelper {

    public static HashSet<String> getPerms(UUID uuid) {
        var list = new HashSet<String>();
        var userManager = LuckPermsProvider.get().getUserManager();
        User user = null;
        if (Universe.get().getPlayer(uuid) != null) {
            user = userManager.getUser(uuid);
        } else {
            user = userManager.loadUser(uuid).join();
        }
        if (user != null) {
            user.getNodes(NodeType.PERMISSION).stream().map(PermissionNode::getPermission).forEach(list::add);
            for (Group inheritedGroup : user.getInheritedGroups(QueryOptions.builder(QueryMode.NON_CONTEXTUAL).build())) {
                inheritedGroup.getNodes(NodeType.PERMISSION).stream().map(PermissionNode::getPermission).forEach(list::add);
            }
        }
        ;
        return list;
    }
}
