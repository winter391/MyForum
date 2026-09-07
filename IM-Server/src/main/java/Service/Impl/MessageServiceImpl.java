package Service.Impl;

import Entity.PrivateMessage;
import Mapper.PrivateMessageMapper;
import Service.MessageService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public class MessageServiceImpl extends ServiceImpl<PrivateMessageMapper,PrivateMessage> implements MessageService
{
    @Override
    public List<PrivateMessage> getPrivateMessage(Long chat_id, String chat_key)
    {
        return this.getBaseMapper().selectPrivateMessageByChatKeyAndSeqNo(chat_key,chat_id);
    }
}
