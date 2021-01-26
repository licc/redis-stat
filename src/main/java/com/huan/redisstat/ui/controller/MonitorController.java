package com.huan.redisstat.ui.controller;

import com.huan.redisstat.common.RedisClusterStore;
import com.huan.redisstat.model.RedisNode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lihuan on 2021/01/18.
 */
@Controller
public class MonitorController {


    @RequestMapping(value = "/monitor/{node:.*}", produces = {"text/html"})
    public ModelAndView monitor(@PathVariable("node") String node) {
        Map<String, Object> data = new HashMap<>();
        RedisNode redisNode = RedisClusterStore.getNodeMapping().get(node);
        data.put("node", node);
        data.put("info", redisNode.info());
        return new ModelAndView("monitor").addAllObjects(data);
    }


    @RequestMapping(value = "/config/{node:.*}", produces = {"text/html"})
    public ModelAndView config(@PathVariable("node") String node) {
        Map<String, Object> data = new HashMap<>();
        RedisNode redisNode = RedisClusterStore.getNodeMapping().get(node);
        data.put("node", node);
        data.put("configs", redisNode.configs());
        return new ModelAndView("config").addAllObjects(data);
    }

    @RequestMapping(value = "/slowlog/{node:.*}", produces = {"text/html"})
    public ModelAndView slowlog(@PathVariable("node") String node) {
        Map<String, Object> data = new HashMap<>();
        RedisNode redisNode = RedisClusterStore.getNodeMapping().get(node);
        data.put("node", node);
        data.put("slowLogs", redisNode.slowLogs());
        return new ModelAndView("slowlog").addAllObjects(data);
    }

}
