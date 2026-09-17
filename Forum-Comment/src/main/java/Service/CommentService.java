package Service;

import Dto.GetCommentsDto;
import Dto.LikeCommentDto;
import Dto.PublishCommentDto;
import Entity.Comment;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;

public interface CommentService extends IService<Comment>
{
    //发布评论，返回新评论的id
    public Long publishComment(PublishCommentDto dto);

    //给评论点赞
    public void likeComment(LikeCommentDto dto);

    //取消点赞
    public void unlikeComment(LikeCommentDto dto);

    //分页查询一个帖子下的评论，可按时间或点赞数排序
    public Page<Comment> getComments(GetCommentsDto dto);
}
