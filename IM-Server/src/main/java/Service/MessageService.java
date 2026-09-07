package Service;

import Entity.PrivateMessage;
import com.baomidou.mybatisplus.spring.service.IService;

import java.util.List;

public interface MessageService extends IService<PrivateMessage>
{
    public List<PrivateMessage> getPrivateMessage(Long chat_id,String chat_key);
}
