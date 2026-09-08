package Service;

import DTO.SendStringPrivateMessageDto;
import Entity.Message;
import Entity.PrivateMessage;
import V0.GetFollowersV0;
import V0.GetSubscribersV0;
import com.baomidou.mybatisplus.spring.service.IService;

import java.util.List;

public interface MessageService extends IService<PrivateMessage>
{
    public void SendStringPrivateMessage(SendStringPrivateMessageDto dto);

    public void Subscribe(Long id);

    public List<GetFollowersV0> GetFollowers();


    public List<GetSubscribersV0> Getsubscribers();

}
