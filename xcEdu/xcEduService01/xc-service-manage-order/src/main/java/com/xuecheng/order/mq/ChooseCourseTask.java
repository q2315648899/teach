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

    /**
     * cron表达式：
     * cron=" * * * * * *"
     * 第一个:秒（0-59）
     * 第二个:分（0-59）
     * 第三个:小时（0-23）
     * 第四个:每月的哪一天（1-31）
     * 第五个:月份（1-12）
     * 第六个:周几（填写MON，TUE，WED，THU，FRI，SAT,SUN，或数字1~7 1表示MON，依次类推）
     *
     * 特殊字符介绍：
     * “/”字符表示指定数值的增量
     * “*”字符表示所有可能的值
     * “-”字符表示区间范围
     * "," 字符表示列举
     * “？”字符仅被用于月中的天和周中的天两个子表达式，表示不指定值
     */
}
