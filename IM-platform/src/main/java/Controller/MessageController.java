package Controller;


import DTO.SendStringPrivateMessageDto;
import Service.MessageService;
import V0.GetFollowersV0;
import V0.GetSubscribersV0;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/MyForum")
public class MessageController
{
    @Autowired
    private MessageService messageService;


    @PostMapping("/SendStringPrivateMessage")
    public void SendStringPrivateMessage(@RequestBody SendStringPrivateMessageDto dto)
    {
        messageService.SendStringPrivateMessage(dto);
    }


    @PostMapping("/Subscribe")
    public void Subscribe(@RequestBody Long id)
    {
        messageService.Subscribe(id);
    }


    @PostMapping("/GetFollowers")
    public List<GetFollowersV0> GetFollowers()
    {
        return messageService.GetFollowers();
    }

    @PostMapping("/GetSubscribers")
    public List<GetSubscribersV0> GetSubscribers()
    {
        return messageService.Getsubscribers();
    }

}
