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
    
    private Short type;
    
    private Integer chatId;

    private String chatKey;
}
