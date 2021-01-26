package com.huan.redisstat.ui.controller;

import com.huan.redisstat.common.RedisClusterStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 优雅停机
 * User: lihuan(of2032)
 * Date: 2018-09-06
 * Time: 20:40
 */
@Slf4j
@Controller
@RequestMapping("/")
public class DcHealthController {
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;


    @RequestMapping(value = "/HealthServlet/check")
    @ResponseBody
    public String shutdown(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("redis-stats开始停机...");
        try {
            stopSpringTask();

            RedisClusterStore.closeJedisClient();

        } catch (Exception e) {
            log.error("停止异常", e);
            return e.getMessage();
        } finally {

        }
        log.info("redis-stats停机完成...");
        return "";
    }

    public void stopSpringTask() {
        log.info("开始停止spring-task的线程池ActiveCount:{} ...", threadPoolTaskScheduler.getActiveCount());
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskScheduler.shutdown();
        while (threadPoolTaskScheduler.getActiveCount() != 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("停止线程池失败");
            }
        }
        log.info("停止spring-task的线程池成功");
    }
}
