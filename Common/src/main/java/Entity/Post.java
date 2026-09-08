package Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("post")
public class Post
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
}