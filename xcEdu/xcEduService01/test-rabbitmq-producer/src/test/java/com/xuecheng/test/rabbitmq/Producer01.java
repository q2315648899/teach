package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbit的入门程序(生产者)
 * Create by wong on 2021/5/22
 */
public class Producer01 {

    // 队列
    private static final String QUEUE = "helloworld";

    public static void main(String[] args) {
        // 通过连接窗口创建新的连接和mq建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        // 设置虚拟机，rabbitmq默认虚拟机名称为“/”，一个mq服务可以设置多个虚拟机，每个虚拟机就相当于一个独立的mq
        connectionFactory.setVirtualHost("/");
        Connection connection = null;
        Channel channel = null;
        try {
            // 创建与RabbitMQ服务的TCP连接
            connection = connectionFactory.newConnection();
            // 创建会话通道，生产者和mq服务所有通信都在channel通道中完成
            channel = connection.createChannel();
            // 声明队列，如果Rabbit中没有此队列将自动创建
            // 参数：String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
            /**
             * 参数明细：
             * param1:队列名称
             * param2:是否持久化，如果持久化，mq重启后队列还在
             * param3:队列是否独占此连接，队列只允许在该连接中访问，如果connection连接关闭则队列自动删除，如果将此参数设置为true可用于临时队列的创建
             * param4:队列不再使用时是否自动删除此队列，如果将此参数和exclusive参数设置为true就可以实现临时队列（队列不用了就自动删除）
             * param5:队列参数，可以设置一个队列的扩展参数，比如：可设置存活时间
             */
            channel.queueDeclare(QUEUE, true, false, false, null);
            // 发送消息
            // 消息发布方法basicPublish
            // 参数：String exchange, String routingKey, BasicProperties props, byte[] body
            /**
             * 参数明细：
             * param1：Exchange的名称，如果没有指定，则使用Default Exchange（mq的默认交换机）
             * param2:routingKey,消息的路由Key，是用于Exchange（交换机）将消息转发到指定的消息队列，如果使用默认交换机，routingKey设置为队列的名称
             * param3:消息包含的属性
             * param4：消息内容
             *
             * 这里没有指定交换机，消息将发送给默认交换机，每个队列也会绑定那个默认的交换机，但是不能显示绑定或解除绑定
             * 默认的交换机，routingKey等于队列名称
             * */
            // 消息内容
            String message = "helloworld 黑马程序员";
            channel.basicPublish("", QUEUE, null, message.getBytes());
            System.out.println("send to mq " + message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 先关闭通道
            // 再关闭连接
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
