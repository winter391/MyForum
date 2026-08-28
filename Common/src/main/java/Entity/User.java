package Entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("user")
public class User
{
    private Long id;

    private String userName;

    private String password;

    private String nickName;

    private Short sex;

    private Boolean isBanned;

    private String headImage;

    private String headImageThumb;

    private Date createTime;

    private Date loginTime;

    private Short type;
}
