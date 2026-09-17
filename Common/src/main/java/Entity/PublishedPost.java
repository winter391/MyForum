package Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("post_published")
public class PublishedPost
{
    private Long id;

    private String title;

    private String content;

    private String publisherNickname;

    private Long publisherId;

    private String barName;

    private Long barId;

    private Integer pin;

    private Integer viewCount;

    private Integer likeCount;

    private Integer commentCount;

    private Date createTime;

    //帖子是否被封禁，被封禁的帖子不能被用户获取
    private Integer isBanned;
}