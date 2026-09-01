package DTO;

import lombok.Data;

import java.util.List;



@Data
public class SendStringPrivateMessageDto
{
    private Long senderId;


    private Long receiverId;




    private String message;
}
