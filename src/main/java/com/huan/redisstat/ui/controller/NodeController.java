package com.huan.redisstat.ui.controller;

import com.huan.redisstat.common.RedisClusterStore;
import com.huan.redisstat.common.RestResult;
import com.huan.redisstat.common.exception.ErrorCode;
import com.huan.redisstat.persistence.entities.Cluster;
import com.huan.redisstat.service.ClusterService;
import com.huan.redisstat.ui.vo.NodeVO;
import com.huan.redisstat.service.NodeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: lihuan
 * @Date: 2021/1/22 17:35
 * @Description:
 */
@Slf4j
@Controller
@RequestMapping("/node")
public class NodeController {

    @Autowired
    NodeService nodeService;

    @Autowired
    ClusterService clusterService;

    @ResponseBody
    @PostMapping("/add")
    public RestResult<String> add(@RequestBody NodeVO nodeVo) {

        if (nodeVo.getClusterId() == null || StringUtils.isBlank(nodeVo.getHost())) {
            return RestResult.newInstance(ErrorCode.ILLEGAL_PARAMS);
        } else {
            nodeService.add(NodeVO.translateFromNodeVO(nodeVo));
            Cluster cluster = clusterService.selectById(nodeVo.getClusterId());
            RedisClusterStore.refresh(cluster);
            return RestResult.newInstance();
        }

    }


    @ResponseBody
    @DeleteMapping("/{id}")
    public RestResult<String> del(@PathVariable("id") Long id) {
        if (nodeService.deleteById(id) != 1) {
            return RestResult.newInstance(ErrorCode.OPT_ERROR);
        }

        return RestResult.newInstance();
    }
}
