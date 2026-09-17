package Controller;

import Dto.SendSystemMessageDto;
import Result.Result;
import Result.ResultUtil;
import Service.NotificationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//该接口供后续的后台系统调用，用来向指定用户发送系统消息
@RestController
@RequestMapping("/MyForum/Notification")
@Slf4j
public class NotificationController
{
    @Autowired
    private NotificationService notificationService;


    //向指定用户发送一条系统消息，落库并推送到该用户在线的终端
    @PostMapping("/sendSystemMessage")
    public Result<?> sendSystemMessage(@RequestBody @Valid SendSystemMessageDto dto)
    {
        notificationService.sendSystemMessage(dto.getReceiverId(),dto.getMessage(),dto.getType());
        return ResultUtil.success();
    }
}
