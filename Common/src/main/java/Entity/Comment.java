package Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

//该实体对应comment表，存储了帖子下的每一条评论
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("comment")
public class Comment
{
    private Long id;

    //评论的内容
    private String content;

    //评论发布者的id
    private Long publisherId;

    //评论发布者的昵称
    private String publisherNickname;

    //评论所属帖子的id
    private Long postId;

    //所回复的评论的id，为null时表示这是帖子下的一级评论
    private Long parentId;

    //该评论的点赞数
    private Integer likeCount;

    //该评论的发布时间
    private Date createTime;

    //该评论是否被封禁，被封禁的评论不会在查询接口中返回
    private Integer isBanned;
}
