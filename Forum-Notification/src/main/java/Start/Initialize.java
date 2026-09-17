package Start;

import Code.NotificationCode;
import Listener.NotificationListener;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

//该类在启动时声明通知链路的交换机和队列，并启动队列的监听容器
//通知链路独立于私信聊天链路：Forum-Post等生产者只负责往notification交换机发消息，本模块负责消费
@Component
public class Initialize implements ApplicationRunner
{
    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private NotificationListener notificationListener;

    @Autowired
    private ConnectionFactory connectionFactory;


    @Override
    public void run(ApplicationArguments args)
    {
        //声明通知交换机、队列以及绑定，声明是幂等的，重复启动不会出错
        TopicExchange topicExchange = new TopicExchange(NotificationCode.EXCHANGER,true,false);
        Queue postQueue = new Queue(NotificationCode.POST_QUEUE,true,false,false);
        rabbitAdmin.declareExchange(topicExchange);
        rabbitAdmin.declareQueue(postQueue);
        Binding bindingPostQueue = BindingBuilder.bind(postQueue).to(topicExchange).with(NotificationCode.POST_ROUTING_KEY);
        rabbitAdmin.declareBinding(bindingPostQueue);

        //启动监听容器，消费帖子通知队列
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setQueueNames(NotificationCode.POST_QUEUE);
        container.setMessageListener(notificationListener);
        container.setConnectionFactory(connectionFactory);
        container.start();
    }
}
