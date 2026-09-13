package Service.Impl;


import Code.RabbitMqCode;
import Code.RedisCode;
import Code.TerminalCode;
import DLL.SendMessage;
import DTO.SendStringPrivateMessageDto;
import Entity.PrivateMessage;
import Entity.Subscribe;
import Entity.User;
import Entity.UserSession;
import Mapper.PrivateMessageMapper;
import Mapper.UserMapper;
import Mapper.SubscribeMapper;
import Util.CopyProperties;
import Util.GetUser;
import V0.GetFollowersV0;
import V0.GetSubscribersV0;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import Exception.GlobalException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import Service.MessageService;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.Long.max;
import static java.lang.Long.min;


@Service
public class MessageServiceImpl extends ServiceImpl<PrivateMessageMapper,PrivateMessage> implements MessageService
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

    @Override
    public void SendStringPrivateMessage(SendStringPrivateMessageDto dto)
    {
        Long senderId = GetUser.getUser().getId();
        if(!isSubcribeEachOther(senderId,dto.getReceiverId()))
        {
            throw new GlobalException("双方没有互相关注");
        }


        RLock lock = redissonClient.getLock(String.join(":",RedisCode.LOCK,Long.valueOf(min(senderId,dto.getReceiverId())).toString(),Long.valueOf(max(senderId,dto.getReceiverId())).toString()));
        String chatKey = String.join(":",Long.valueOf(min(dto.getReceiverId(),senderId)).toString(),Long.valueOf(max(dto.getReceiverId(),senderId)).toString());
        Long chatId = -1L;
        boolean sign = false;
        try {
            boolean isLock = lock.tryLock(RedisCode.WAITING_TIME, TimeUnit.SECONDS);
            if (!isLock) {
                throw new GlobalException("服务超时");
            }

            chatId = redisTemplate.opsForValue().increment(String.join(":", RedisCode.CHAT_KEY_TO_CHAT_ID, chatKey));
            if (chatId == 1) {
                redisTemplate.delete(String.join(":", RedisCode.CHAT_KEY_TO_CHAT_ID, chatKey));
                Long maxId = getBaseMapper().maxChatId(chatKey);
                if (maxId == null) {
                    maxId = 0L;
                }
                chatId = maxId + 1;
                redisTemplate.opsForValue().set(String.join(":", RedisCode.CHAT_KEY_TO_CHAT_ID, chatKey), maxId + 1);
            }
            sign = true;
        }
        catch (InterruptedException e)
        {
            throw new GlobalException("服务超时");
        }
        finally {
            if(sign)lock.unlock();
        }
        PrivateMessage message = CopyProperties.copyProperties(dto, PrivateMessage.class);
        message.setSenderId(senderId);
        message.setType((short)0);
        message.setChatId(chatId.intValue());
        message.setChatKey(chatKey);
        save(message);
        List<String> terminal_redisKey = new ArrayList<>();
        for(Integer terminal: TerminalCode.getAllTerminal())
        {
            terminal_redisKey.add(String.join(":",RedisCode.ONLINE_USER_ID_WITH_TERMINAL_TO_SERVER_ID,dto.getReceiverId().toString(),terminal.toString()));
        }
        List<Object> serverIds = redisTemplate.opsForValue().multiGet(terminal_redisKey);
        List<Integer> result_serverIds = new ArrayList<>();
        for(Object id:serverIds)
        {
            if(id!=null)
            {
                result_serverIds.add((Integer)id);
            }
        }
        sendMessage.SendMessage(result_serverIds,message, RabbitMqCode.PRIVATE_MESSAGE);
    }

    private boolean isSubcribeEachOther(Long id_first,Long id_second)
    {
        Long first = userMapper.getSubscriberId(id_first,id_second);
        Long second = userMapper.getSubscriberId(id_second,id_first);

        if(first==null||second==null) return false;
        else return true;
    }


    @Override
    public void Subscribe(Long id)
    {
        UserSession user = GetUser.getUser();
        Long target_id = subscribeMapper.getSubscribedId(user.getId(),id);
        if(target_id!=null)
        {
            throw new GlobalException("已关注对方");
        }
        subscribeMapper.insert(new Subscribe(user.getId(),id));
    }

    @Override
    public List<GetFollowersV0> GetFollowers()
    {
        UserSession session = GetUser.getUser();
        List<Long> ids = subscribeMapper.getSubscriberIds(session.getId());
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.select(User::getId,User::getNickName).in(User::getId,ids);
        List<User> users = userMapper.selectList(query);
        List<GetFollowersV0> res = new ArrayList<>();
        for(User temp_user:users)
        {
            res.add(CopyProperties.copyProperties(temp_user, GetFollowersV0.class));
        }
        return res;
    }


    @Override
    public List<GetSubscribersV0> Getsubscribers()
    {
        UserSession session = GetUser.getUser();
        List<Long> ids = subscribeMapper.getSubscribedIds(session.getId());
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.select(User::getId,User::getNickName).in(User::getId,ids);
        List<User> users = userMapper.selectList(query);
        List<GetSubscribersV0> res = new ArrayList<>();
        for(User temp_user:users)
        {
            res.add(CopyProperties.copyProperties(temp_user, GetSubscribersV0.class));
        }
        return res;
    }
}
