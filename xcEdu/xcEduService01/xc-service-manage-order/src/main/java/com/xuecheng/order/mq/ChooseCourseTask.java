package com.xuecheng.order.mq;

import com.rabbitmq.client.Channel;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * //@Component注释默认是单例的
 * <p>
 * Create by wong on 2021/6/10
 */
@Component
public class ChooseCourseTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

    @Autowired
    TaskService taskService;

    /**
     * 监听完成添加选课消息队列，接收选课响应结果
     */
    @RabbitListener(queues = {RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE})
    public void receiveFinishChoosecourseTask(XcTask task, Message message, Channel channel) throws
            IOException {
        LOGGER.info("receiveChoosecourseTask...{}", task.getId());
        //接收到的消息id
        String id = task.getId();
        //删除任务，添加历史任务
        taskService.finishTask(id);
    }


    // 添加选课信息的定时任务
    @Scheduled(cron = "0/3 * * * * *")
    public void sendChoosecourseTask() {
        //取出当前时间1分钟之前的时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(GregorianCalendar.MINUTE, -1);
        Date time = calendar.getTime();
        List<XcTask> xcTaskList = taskService.findTaskList(time, 100);
        System.out.println(xcTaskList);
        // 调用service发布消息,将添加选课的任务发送给mq
        for (XcTask xcTask : xcTaskList) {
            //任务id
            String taskId = xcTask.getId();
            //版本号
            Integer version = xcTask.getVersion();
            //调用乐观锁方法校验任务是否可以执行
            if (taskService.getTask(taskId, version) > 0) {
                String exchange = xcTask.getMqExchange();// 要发送的交换机
                String routingKey = xcTask.getMqRoutingkey();// 发送消息要带的routingKey
                taskService.publish(xcTask, exchange, routingKey);
            }
        }
    }


    /**
     * 测试基本的定时任务
     */
    // @Scheduled(fixedRate = 5000) //上次任务开始后5秒执行
    // @Scheduled(fixedDelay = 5000) //上次任务结束后5秒执行
    // @Scheduled(initialDelay=3000, fixedRate=5000) //第一次延迟3秒，以后每隔5秒执行一次
//    @Scheduled(cron = "0/3 * * * * *")//（上次任务开始后）每隔3秒执行一次
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
//    @Scheduled(cron = "0/3 * * * * *")//（上次任务开始后）每隔3秒执行一次
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
