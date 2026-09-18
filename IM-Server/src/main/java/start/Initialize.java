package start;

import Code.ImRabbitmqCode;
import Code.RedisCode;
import Code.RedissonCode;
import Code.ServerId;
import Listener.PrivateMessageListener;
import Listener.SystemMessageListener;

import Netty.NettyMain;
import jakarta.annotation.PreDestroy;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
import org.springframework.data.redis.core.RedisTemplate;
import Exception.GlobalException;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class Initialize implements ApplicationRunner
{
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private PrivateMessageListener privateMessageListener;

    @Autowired
    private SystemMessageListener systemMessageListener;

    @Autowired
    private ConnectionFactory connectionFactory;


    @Autowired
    private NettyMain nettyMain;


    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        serverIdIntialiaze();
        messageServerInitialize();
        ListenerInitialize();
        new Thread(nettyMain::run,"netty-ws-server").start();
    }


    @PreDestroy
    public void destroy()
    {
        rabbitAdmin.deleteQueue(ImRabbitmqCode.privateMessageQueueName+ServerId.id);
        rabbitAdmin.deleteQueue(ImRabbitmqCode.systemMessageQueueName+ServerId.id);
        rabbitAdmin.deleteExchange(ImRabbitmqCode.SERVER_NAME+ServerId.id);
    }




    public void serverIdIntialiaze()
    {
        RLock lock = redissonClient.getLock(RedissonCode.LOCK_SERVER_ID);
        boolean sign = false;
        try {
            boolean isLock = lock.tryLock(RedissonCode.WAITING_TIME, TimeUnit.SECONDS);
            if (isLock) {
                sign =true;
                ServerId.id = redisTemplate.opsForValue().increment(RedisCode.SERVER_ID);
            } else {
                throw new GlobalException("初始化错误，无法获得redisson锁");
            }
        }
        catch (InterruptedException e)
        {
            throw new GlobalException("初始化错误，无法获得redisson锁");
        }
        finally {
            if(sign) lock.unlock();
        }
    }

    public void messageServerInitialize()
    {
        TopicExchange topicExchange = new TopicExchange(ImRabbitmqCode.SERVER_NAME+ServerId.id,true,false);
        Queue privateMessageQueue = new Queue(ImRabbitmqCode.privateMessageQueueName+ServerId.id,true,false,false);
        Queue systemMessageQueue = new Queue(ImRabbitmqCode.systemMessageQueueName+ServerId.id,true,false,false);
        rabbitAdmin.declareExchange(topicExchange);
        rabbitAdmin.declareQueue(privateMessageQueue);
        rabbitAdmin.declareQueue(systemMessageQueue);
        Binding bindingPrivateMessage = BindingBuilder.bind(privateMessageQueue).to(topicExchange).with(ImRabbitmqCode.privateMessageRoutingKey);
        Binding bindingSystemMessage = BindingBuilder.bind(systemMessageQueue).to(topicExchange).with(ImRabbitmqCode.systemMessageRoutingKey);
        Binding bindingExchanger = BindingBuilder.bind(topicExchange).to(new TopicExchange(ImRabbitmqCode.mainExchanger)).with(ImRabbitmqCode.getMainExchangerRoutingKey(ServerId.id));
        rabbitAdmin.declareBinding(bindingPrivateMessage);
        rabbitAdmin.declareBinding(bindingSystemMessage);
        rabbitAdmin.declareBinding(bindingExchanger);
    }

    public void ListenerInitialize()
    {
        SimpleMessageListenerContainer PMcontainer = new SimpleMessageListenerContainer();
        PMcontainer.setQueueNames(ImRabbitmqCode.privateMessageQueueName+ServerId.id);
        PMcontainer.setMessageListener(privateMessageListener);
        PMcontainer.setConnectionFactory(connectionFactory);
        PMcontainer.start();

        SimpleMessageListenerContainer SMcontainer = new SimpleMessageListenerContainer();
        SMcontainer.setQueueNames(ImRabbitmqCode.systemMessageQueueName+ServerId.id);
        SMcontainer.setMessageListener(systemMessageListener);
        SMcontainer.setConnectionFactory(connectionFactory);
        SMcontainer.start();
    }


}
