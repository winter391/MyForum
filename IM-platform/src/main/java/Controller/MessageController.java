package Controller;


import DTO.SendStringPrivateMessageDto;
import Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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



}
