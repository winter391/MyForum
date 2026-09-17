package Service.Impl;

import Code.RedisCode;
import Code.RabbitMqCode;
import Code.SystemMessageTypeCode;
import Code.TerminalCode;
import DLL.SendMessage;
import Entity.SystemMessage;
import Entity.User;
import Exception.GlobalException;
import Mapper.SubscribeMapper;
import Mapper.SystemMessageMapper;
import Mapper.UserMapper;
import Message.ChatMessage;
import Message.MessagePack;
import Message.SubscribeMessage;
import Service.NotificationService;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class NotificationServiceImpl implements NotificationService
{
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SendMessage sendMessage;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SubscribeMapper subscribeMapper;

    @Autowired
    private SystemMessageMapper systemMessageMapper;


    //该方法有硬编码问题
    @Override
    public void sendPostNotification(SubscribeMessage subscribeMessage)
    {
        //查询关注了发帖用户的所有粉丝
        List<Long> followerIds = subscribeMapper.getSubscriberIds(subscribeMessage.getPublisherId());
        if(followerIds==null||followerIds.isEmpty())
        {
            return;
        }
        MessagePack<SubscribeMessage> messagePack = new MessagePack<>();
        messagePack.setType(SystemMessageTypeCode.suscribe);
        messagePack.setMessage(subscribeMessage);
        String content = JSON.toJSONString(messagePack);
        for(Long followerId:followerIds)
        {
            sendSystemMessage(followerId,content,SystemMessageTypeCode.text);
        }
    }

    @Override
    public void broadcastSystemMessage(ChatMessage chatMessage)
    {
        //广播消息发送给所有用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(User::getId);
        List<User> users = userMapper.selectList(wrapper);
        for(User user:users)
        {
            sendSystemMessage(user.getId(),chatMessage.getMessage(),SystemMessageTypeCode.text);
        }
    }

    @Override
    public void sendSystemMessage(Long receiverId, String message, Short type)
    {
        //校验接收者存在
        User receiver = userMapper.selectById(receiverId);
        if(receiver==null)
        {
            throw new GlobalException("该用户不存在");
        }
        //计算系统与该用户会话内的消息序号
        Long chatId = getChatId(receiverId);
        SystemMessage systemMessage = new SystemMessage();
        systemMessage.setReceiverId(receiverId);
        systemMessage.setMessage(message);
        systemMessage.setType(type);
        systemMessage.setChatId(chatId.intValue());
        //先落库，再推送，这样离线用户下次拉取时也能看到消息
        systemMessageMapper.insert(systemMessage);
        //查询该用户所有在线终端所在的IM-Server，按照原有消息链路的预留设计发到main交换机
        List<Integer> serverIds = getOnlineServerIds(receiverId);
        sendMessage.SendMessage(serverIds,systemMessage, RabbitMqCode.SYSTEM_MESSAGE);
    }


    //计算系统与某个用户的会话内消息序号，系统视为id为0的用户，会话key与私信会话的格式保持一致
    //计数逻辑与IM-platform的私信链路相同：Redis自增，首次自增到1时从数据库恢复计数
    private Long getChatId(Long receiverId)
    {
        String chatKey = String.join(":","0",receiverId.toString());
        RLock lock = redissonClient.getLock(String.join(":",RedisCode.LOCK,chatKey));
        Long chatId;
        boolean sign = false;
        try
        {
            boolean isLock = lock.tryLock(RedisCode.WAITING_TIME, TimeUnit.SECONDS);
            if(!isLock)
            {
                throw new GlobalException("服务超时");
            }
            sign = true;
            chatId = redisTemplate.opsForValue().increment(String.join(":",RedisCode.CHAT_KEY_TO_CHAT_ID,chatKey));
            if(chatId==1)
            {
                redisTemplate.delete(String.join(":",RedisCode.CHAT_KEY_TO_CHAT_ID,chatKey));
                Long maxId = systemMessageMapper.getMaxChatId(receiverId);
                if(maxId==null)
                {
                    maxId = 0L;
                }
                chatId = maxId+1;
                redisTemplate.opsForValue().set(String.join(":",RedisCode.CHAT_KEY_TO_CHAT_ID,chatKey),maxId+1);
            }
        }
        catch (InterruptedException e)
        {
            throw new GlobalException("服务超时");
        }
        finally
        {
            if(sign)lock.unlock();
        }
        return chatId;
    }

    //查询某个用户所有在线终端所在的IM-Server的id
    //该实现与IM-platform的私信链路相同：遍历所有终端类型，从redis的在线映射中批量查出serverId
    private List<Integer> getOnlineServerIds(Long userId)
    {
        List<String> terminal_redisKey = new ArrayList<>();
        for(Integer terminal: TerminalCode.getAllTerminal())
        {
            terminal_redisKey.add(String.join(":",RedisCode.ONLINE_USER_ID_WITH_TERMINAL_TO_SERVER_ID,userId.toString(),terminal.toString()));
        }
        List<Object> serverIds = redisTemplate.opsForValue().multiGet(terminal_redisKey);
        List<Integer> result_serverIds = new ArrayList<>();
        for(Object id:serverIds)
        {
            if(id!=null)
            {
                result_serverIds.add((Integer) id);
            }
        }
        return result_serverIds;
    }
}
