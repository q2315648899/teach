package com.xuecheng.order.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * //@Component注释默认是单例的
 *
 * Create by wong on 2021/6/10
 */
@Component
public class ChooseCourseTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

    /**
     * 测试基本的定时任务
     */
    // @Scheduled(fixedRate = 5000) //上次任务开始后5秒执行
    // @Scheduled(fixedDelay = 5000) //上次任务结束后5秒执行
    // @Scheduled(initialDelay=3000, fixedRate=5000) //第一次延迟3秒，以后每隔5秒执行一次
    @Scheduled(cron = "0/3 * * * * *")//（上次任务开始后）每隔3秒执行一次
    public void task1() {
        LOGGER.info("===============测试定时任务1开始===============");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("===============测试定时任务1结束===============");
    }

    /**
     * 第二个定时任务，测试串行
     */
    @Scheduled(cron = "0/3 * * * * *")//（上次任务开始后）每隔3秒执行一次
    public void task2() {
        LOGGER.info("===============测试定时任务2开始===============");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("===============测试定时任务2结束===============");
    }

}
