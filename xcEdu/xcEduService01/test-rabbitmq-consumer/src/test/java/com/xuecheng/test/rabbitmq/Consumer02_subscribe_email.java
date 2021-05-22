package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbit的发布订阅模式publish/subscribe（邮件发送消费者）
 * Create by wong on 2021/5/22
 */
public class Consumer02_subscribe_email {

    // 队列名称
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";// 邮件
    // exchange交换机名称
    private static final String EXCHANGE_FANOUT_INFORM="exchange_fanout_inform";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 通过连接窗口创建新的连接和mq建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        // 设置虚拟机，rabbitmq默认虚拟机名称为“/”，一个mq服务可以设置多个虚拟机，每个虚拟机就相当于一个独立的mq
        connectionFactory.setVirtualHost("/");
        // 创建与RabbitMQ服务的TCP连接
        Connection connection = connectionFactory.newConnection();
        // 创建会话通道，消费者和mq服务所有通信也都在channel通道中完成
        Channel channel = connection.createChannel();

        /**
         * 声明队列，如果Rabbit中没有此队列将自动创建
         * 参数：String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
         * 参数明细：
         * param1:队列名称
         * param2:是否持久化，如果持久化，mq重启后队列还在
         * param3:队列是否独占此连接，队列只允许在该连接中访问，如果connection连接关闭则队列自动删除，如果将此参数设置为true可用于临时队列的创建
         * param4:队列不再使用时是否自动删除此队列，如果将此参数和exclusive参数设置为true就可以实现临时队列（队列不用了就自动删除）
         * param5:队列参数，可以设置一个队列的扩展参数，比如：可设置存活时间
         */
        channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);

        // 声明一个交换机
        // 参数：String exchange, BuiltinExchangeType type
        /**
         * 参数明细：
         * param1：交换机的名称
         * param2：交换机的类型。fanout、topic、direct、headers
         * fanout：对应的rabbitmq的工作模式是publish/subscribe
         * direct：对应的Routing的工作模式
         * topic：对应的topics工作模式
         * headers：对应的headers工作模式
         *
         */
        channel.exchangeDeclare(EXCHANGE_FANOUT_INFORM, BuiltinExchangeType.FANOUT);

        //  进行交换机和队列绑定
        // 参数：String queue, String exchange, String routingKey
        /**
         * 参数明细：
         * param1：队列名称
         * param2：交换机名称
         * param3：路由key，作用是交换机根据路由key的值将信息转发到指定的队列中，在发布订阅模式中设置为空字符串
         */
        channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_FANOUT_INFORM, "");

        // 消费消息的方法
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            /**
             * 消费者接收消息调用此方法
             * @param consumerTag 消费者的标签，在channel.basicConsume()去指定
             * @param envelope 消息包的内容，可从中获取消息id，消息routingkey，交换机，消息和重传标志
            (收到消息失败后是否需要重新发送)
             * @param properties 对应生产者发过来的消息属性
             * @param body 对应生产者发过来的消息内容
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                // 交换机
                String exchange = envelope.getExchange();
                // 路由key
                String routingKey = envelope.getRoutingKey();
                // 消息id,mq在channel中用来标识消息的id，可用于当设置为手动回复mq时确认消息已接收
                long deliveryTag = envelope.getDeliveryTag();
                // 消息内容
                String msg = new String(body, "utf-8");
                System.out.println("receive message.." + msg);
            }
        };

        /**
         *
         * 监听队列
         * 参数：String queue, boolean autoAck, Consumer callback
         * 参数明细：
         * param1:队列名称
         * param2:自动回复，当消费者接收到消息后要告诉mq消息已接受，如果将此参数设置为true表示会自动回复mq，mq接收到回复会删除消息，设置
         为false则需要手动回复通过编程实现

         * param3:消费消息的方法，消费者接收到消息后调用此方法
         */
        channel.basicConsume(QUEUE_INFORM_EMAIL, true, consumer);

    }
}
