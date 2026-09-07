package Controller;


import Dto.GetPrivateMessageDto;
import Entity.PrivateMessage;
import Entity.UserSession;
import Result.Result;
import Result.ResultUtil;
import Service.MessageService;
import Util.GetUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.lang.Long.max;
import static java.lang.Long.min;

@RestController
@RequestMapping("/MyForum")
public class MessageController
{

    @Autowired
    private MessageService messageService;



    @GetMapping("/getPrivateMessage")
    public Result<List<PrivateMessage>> getPrivateMessage(GetPrivateMessageDto dto)
    {
        UserSession session = GetUser.getUser();
        Long mi_id = min(session.getId(),dto.getTarget_id());
        Long ma_id = max(session.getId(),dto.getTarget_id());
        String ChatKey = String.join(":",mi_id.toString(),ma_id.toString());
        return ResultUtil.success(messageService.getPrivateMessage(dto.getChat_id(),ChatKey));
    }
}
