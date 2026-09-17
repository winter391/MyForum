package Controller;

import Dto.GetCommentsDto;
import Dto.LikeCommentDto;
import Dto.PublishCommentDto;
import Entity.Comment;
import Result.Result;
import Result.ResultUtil;
import Service.CommentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/MyForum/Comment")
@Slf4j
public class CommentController
{
    @Autowired
    private CommentService commentService;


    //发布评论，返回新评论的id
    @PostMapping("/publishComment")
    public Result<Long> publishComment(@RequestBody @Valid PublishCommentDto dto)
    {
        return ResultUtil.success(commentService.publishComment(dto));
    }

    //给评论点赞
    @PostMapping("/likeComment")
    public Result<?> likeComment(@RequestBody @Valid LikeCommentDto dto)
    {
        commentService.likeComment(dto);
        return ResultUtil.success();
    }

    //取消点赞
    @PostMapping("/unlikeComment")
    public Result<?> unlikeComment(@RequestBody @Valid LikeCommentDto dto)
    {
        commentService.unlikeComment(dto);
        return ResultUtil.success();
    }

    //分页查询一个帖子下的评论，可按时间或点赞数排序
    @PostMapping("/getComments")
    public Result<Page<Comment>> getComments(@RequestBody @Valid GetCommentsDto dto)
    {
        return ResultUtil.success(commentService.getComments(dto));
    }
}
