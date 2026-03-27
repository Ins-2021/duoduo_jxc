package com.duoduo.jxc.service.rbac;

import com.duoduo.jxc.dto.rbac.MenuTreeNode;
import com.duoduo.jxc.entity.rbac.SysMenu;

import java.util.*;

public class MenuTreeBuilder {

    public static List<MenuTreeNode> build(List<SysMenu> menus) {
        if (menus == null || menus.isEmpty()) {
            return List.of();
        }
        Map<Long, MenuTreeNode> map = new LinkedHashMap<>();
        for (SysMenu m : menus) {
            if (m == null) {
                continue;
            }
            MenuTreeNode n = new MenuTreeNode();
            n.setMenuId(m.getMenuId());
            n.setParentId(m.getParentId());
            n.setMenuName(m.getMenuName());
            n.setOrderNum(m.getOrderNum());
            n.setPath(m.getPath());
            n.setComponent(m.getComponent());
            n.setRouteName(m.getRouteName());
            n.setIcon(m.getIcon());
            n.setMenuType(m.getMenuType());
            n.setVisible(m.getVisible());
            n.setStatus(m.getStatus());
            n.setPerms(m.getPerms());
            map.put(n.getMenuId(), n);
        }
        List<MenuTreeNode> roots = new ArrayList<>();
        for (MenuTreeNode n : map.values()) {
            Long pid = n.getParentId() == null ? 0L : n.getParentId();
            if (pid == 0L) {
                roots.add(n);
                continue;
            }
            MenuTreeNode parent = map.get(pid);
            if (parent == null) {
                roots.add(n);
                continue;
            }
            parent.getChildren().add(n);
        }
        sortTree(roots);
        return roots;
    }

    private static void sortTree(List<MenuTreeNode> nodes) {
        if (nodes == null) {
            return;
        }
        nodes.sort(Comparator.comparing(MenuTreeNode::getOrderNum, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(MenuTreeNode::getMenuId, Comparator.nullsLast(Long::compareTo)));
        for (MenuTreeNode n : nodes) {
            sortTree(n.getChildren());
        }
    }
}

