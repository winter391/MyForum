package Entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("private_message")
public class PrivateMessage implements Message
{
    private Long id;
    
    private Long senderId;
    
    private Long receiverId;

    
    private String message;

    //发送问消息类型，0：文字，1：文件
    private Short type;
    
    private Integer chatId;

    private String chatKey;
}
