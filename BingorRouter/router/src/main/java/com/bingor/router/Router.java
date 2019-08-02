package com.bingor.router;

import com.bingor.router.exception.RouterNodeNotFoundException;
import com.bingor.router.node.RouterNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HXB on 2018/7/23.
 */
public class Router {
    public static Map<String, RouterNode> rules = new HashMap<>();

    /**
     * 添加节点
     *
     * @param key        节点名
     * @param routerNode 节点
     */
    public static void addNode(String key, RouterNode routerNode) {
        rules.put(key, routerNode);
    }

    /**
     * 检查节点是否存在
     *
     * @param key 节点名
     * @return
     */
    public static boolean checkNode(String key) {
        return rules.containsKey(key);
    }

    /**
     * 查找节点
     *
     * @param key 节点名
     * @return
     * @throws RouterNodeNotFoundException
     */
    public static <T extends RouterNode> T findNode(String key) throws RouterNodeNotFoundException {
        if (checkNode(key)) {
            return (T) rules.get(key);
        } else {
            throw new RouterNodeNotFoundException(key);
        }
    }
}
