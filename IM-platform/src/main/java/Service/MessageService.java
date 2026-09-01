package Service;

import DTO.SendStringPrivateMessageDto;
import Entity.Message;
import Entity.PrivateMessage;
import com.baomidou.mybatisplus.spring.service.IService;

public interface MessageService extends IService<PrivateMessage>
{
    public void SendStringPrivateMessage(SendStringPrivateMessageDto dto);

    public void Subscribe(Long id);
}
