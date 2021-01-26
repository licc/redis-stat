package com.huan.redisstat.ui.controller;

import com.huan.redisstat.common.RestResult;
import com.huan.redisstat.common.exception.ErrorCode;
import com.huan.redisstat.service.ClusterService;
import com.huan.redisstat.ui.vo.ClusterVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: lihuan
 * @Date: 2021/1/22 17:35
 * @Description:
 */
@Slf4j
@Controller
@RequestMapping("/clusters")
public class ClusterController {

    @Autowired
    ClusterService clusterService;


    @GetMapping(produces = {"text/html"})
    public ModelAndView clusters() {
        Map<String, Object> data = new HashMap<>();
        data.put("clusters",
                clusterService.selectAllCluster().stream().map(item ->
                        ClusterVO.translateFromCluster(item)).collect(Collectors.toList()));

        return new ModelAndView("clusters").addAllObjects(data);
    }

    @ResponseBody
    @RequestMapping("/add")
    public RestResult<String> add(@RequestBody ClusterVO clusterVo) {
        if (StringUtils.isBlank(clusterVo.getName())) {
            return RestResult.newInstance(ErrorCode.ILLEGAL_PARAMS);
        } else {
            clusterService.addClusters(ClusterVO.translateFromClustersVO(clusterVo));
            return RestResult.newInstance();
        }

    }

    @ResponseBody
    @DeleteMapping("/{id}")
    public RestResult<String> del(@PathVariable("id") Long id) {
        if (clusterService.deleteById(id) != 1) {
            return RestResult.newInstance(ErrorCode.OPT_ERROR);
        }

        return RestResult.newInstance();
    }
}
