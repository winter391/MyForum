package Service.Impl;

import Code.CommentCode;
import Dto.GetCommentsDto;
import Dto.LikeCommentDto;
import Dto.PublishCommentDto;
import Entity.*;
import Exception.GlobalException;
import Mapper.BarMapper;
import Mapper.BarMemberMapper;
import Mapper.CommentLikeMapper;
import Mapper.CommentMapper;
import Mapper.PublishedPostMapper;
import Service.CommentService;
import Util.GetUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService
{
    @Autowired
    private PublishedPostMapper publishedPostMapper;

    @Autowired
    private BarMapper barMapper;

    @Autowired
    private BarMemberMapper barMemberMapper;

    @Autowired
    private CommentLikeMapper commentLikeMapper;


    @Override
    @Transactional
    public Long publishComment(PublishCommentDto dto)
    {
        UserSession session = GetUser.getUser();
        //校验帖子存在
        PublishedPost post = publishedPostMapper.selectById(dto.getPostId());
        if(post==null)
        {
            throw new GlobalException("该帖子不存在");
        }
        //校验帖子所属的贴吧没有被封禁，且用户没有在该贴吧内被禁言
        checkBarState(post.getBarId(), session.getId());
        //校验所回复的评论存在，且和当前评论属于同一篇帖子
        if(dto.getParentId()!=null)
        {
            Comment parent = getById(dto.getParentId());
            if(parent==null||!parent.getPostId().equals(dto.getPostId()))
            {
                throw new GlobalException("所回复的评论不存在");
            }
        }
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setPublisherId(session.getId());
        comment.setPublisherNickname(session.getNickName());
        comment.setPostId(dto.getPostId());
        comment.setParentId(dto.getParentId());
        comment.setLikeCount(0);
        comment.setCreateTime(new Date());
        comment.setIsBanned(0);
        getBaseMapper().insert(comment);
        //帖子的评论数原子加一
        publishedPostMapper.addCommentCount(dto.getPostId(),1);
        return comment.getId();
    }

    @Override
    @Transactional
    public void likeComment(LikeCommentDto dto)
    {
        UserSession session = GetUser.getUser();
        //校验评论存在，且没有被封禁
        Comment comment = getById(dto.getCommentId());
        if(comment==null)
        {
            throw new GlobalException("该评论不存在");
        }
        if(Integer.valueOf(1).equals(comment.getIsBanned()))
        {
            throw new GlobalException("该评论已被封禁");
        }
        //校验用户没有重复点赞
        if(commentLikeMapper.getCommentLike(dto.getCommentId(),session.getId())!=null)
        {
            throw new GlobalException("您已点赞过该评论");
        }
        CommentLike like = new CommentLike();
        like.setCommentId(dto.getCommentId());
        like.setUserId(session.getId());
        commentLikeMapper.insert(like);
        //点赞数原子加一
        getBaseMapper().addLikeCount(dto.getCommentId(),1);
    }

    @Override
    @Transactional
    public void unlikeComment(LikeCommentDto dto)
    {
        UserSession session = GetUser.getUser();
        //校验评论存在
        Comment comment = getById(dto.getCommentId());
        if(comment==null)
        {
            throw new GlobalException("该评论不存在");
        }
        //校验用户确实点赞过该评论
        CommentLike like = commentLikeMapper.getCommentLike(dto.getCommentId(),session.getId());
        if(like==null)
        {
            throw new GlobalException("您没有点赞过该评论");
        }
        commentLikeMapper.deleteById(like.getId());
        //点赞数原子减一
        getBaseMapper().addLikeCount(dto.getCommentId(),-1);
    }

    @Override
    public Page<Comment> getComments(GetCommentsDto dto)
    {
        UserSession session = GetUser.getUser();
        //校验帖子存在
        PublishedPost post = publishedPostMapper.selectById(dto.getPostId());
        if(post==null)
        {
            throw new GlobalException("该帖子不存在");
        }
        //被封禁的贴吧，任何人都不可以查看其中的帖子，评论也一并不可查看
        checkBarNotBanned(post.getBarId());
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getPostId, dto.getPostId());
        //被封禁的评论不返回
        wrapper.ne(Comment::getIsBanned,1);
        if(dto.getSortType()==CommentCode.SORT_TIME)
        {
            //按时间排序，新的评论在前
            wrapper.orderByDesc(Comment::getCreateTime);
        }
        else if(dto.getSortType()==CommentCode.SORT_LIKE)
        {
            //按点赞数排序，点赞多的在前
            wrapper.orderByDesc(Comment::getLikeCount);
        }
        else
        {
            throw new GlobalException("不支持的排序方式");
        }
        //用id做次级排序，保证翻页时顺序稳定
        wrapper.orderByDesc(Comment::getId);
        Page<Comment> page = new Page<>(dto.getPage(), dto.getSize());
        return page(page, wrapper);
    }


    //校验帖子所属的贴吧没有被封禁，被封禁的贴吧内任何内容都不可查看
    //吧不存在或者帖子没有所属贴吧时不做校验
    private void checkBarNotBanned(Long barId)
    {
        if(barId==null)
        {
            return;
        }
        Bar bar = barMapper.selectById(barId);
        if(bar!=null&&Integer.valueOf(1).equals(bar.getIsBanned()))
        {
            throw new GlobalException("该贴吧已被封禁");
        }
    }

    //校验帖子所属的贴吧没有被封禁，且用户没有在该贴吧内被禁言
    //禁言只限制发言，被禁言的用户仍然可以查看评论
    private void checkBarState(Long barId, Long userId)
    {
        checkBarNotBanned(barId);
        if(barId==null)
        {
            return;
        }
        BarMember member = barMemberMapper.getBarMember(barId, userId);
        if(member!=null&&Integer.valueOf(1).equals(member.getIsBanned()))
        {
            throw new GlobalException("您在该贴吧内被禁言");
        }
    }
}
