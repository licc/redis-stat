package com.huan.redisstat.common;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lihuan
 * 导航节点
 */
@Getter
@Setter
public class NavNode {
    /**
     * 导航节点类型：一级导航
     */
    public static final int NAV_NODE_TYPE_TOP_NAV = 1;
    /**
     * 导航节点类型：页内导航
     */
    public static final int NAV_NODE_TYPE_PAGE_NAV = 2;

    private Integer id;
    private String name;
    private String icon;
    private String path;
    private String description;
    private Integer type;
    private List<NavNode> children = new ArrayList<>();
    private NavNode parent;

    public NavNode() {
    }

    public NavNode(String name, String path, String icon, String description) {
        this.name = name;
        this.icon = icon;
        this.path = path;
        this.description = description;
    }

    public NavNode(String name, String url, String icon) {
        this(name, url, icon, "");
    }


    public List<NavNode> getChildren() {
        return children;
    }

    public void setChildren(List<NavNode> children) {
        this.children = children;
        for (NavNode navNode : children) {
            navNode.setParent(this);
        }
    }


    /**
     * 是否有子节点
     * @return
     */
    public boolean hasChild() {
        return children.size() > 0;
    }

    /**
     * 是否有一级导航类别（type=NAV_NODE_TYPE_TOP_NAV）的子节点
     * @return
     */
    public boolean hasTopNavChild() {
        for (NavNode navNode : this.children) {
            if (navNode.getType() == NAV_NODE_TYPE_TOP_NAV) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取一级导航类别（type=NAV_NODE_TYPE_TOP_NAV）的子节点
     * @return
     */
    public List<NavNode> getTopNavChildren() {
        List<NavNode> topNavNodes = new ArrayList<>();
        for (NavNode navNode : this.children) {
            if (navNode.getType() == NAV_NODE_TYPE_TOP_NAV) {
                topNavNodes.add(navNode);
            }
        }
        return topNavNodes;
    }

}
