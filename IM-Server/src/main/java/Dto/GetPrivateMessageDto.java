package Dto;


import lombok.Data;

@Data
public class GetPrivateMessageDto
{
    private Long chat_id;

    private Long target_id;
}
