package Controller;


import DTO.SendStringPrivateMessageDto;
import Result.Result;
import Result.ResultUtil;
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
    public Result<?> SendStringPrivateMessage(@RequestBody SendStringPrivateMessageDto dto)
    {
        messageService.SendStringPrivateMessage(dto);
        return ResultUtil.success();
    }


    @PostMapping("/Subscribe")
    public Result<?> Subscribe(@RequestBody Long id)
    {
        messageService.Subscribe(id);
        return ResultUtil.success();
    }


    @PostMapping("/Unsubscribe")
    public Result<?> Unsubscribe(@RequestBody Long id)
    {
        messageService.Unsubscribe(id);
        return ResultUtil.success();
    }


    @PostMapping("/GetFollowers")
    public Result<List<GetFollowersV0>> GetFollowers()
    {
        return ResultUtil.success(messageService.GetFollowers());
    }

    @PostMapping("/GetSubscribers")
    public Result<List<GetSubscribersV0>> GetSubscribers()
    {
        return ResultUtil.success(messageService.Getsubscribers());
    }

}
