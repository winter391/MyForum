package Service.Impl;

import Dto.GetPublishedPostsDto;
import Dto.UpdateUnPublishedPostDto;
import Dto.UploadUnPublishedPostDto;
import Entity.*;
import Code.NotificationCode;
import Code.PostCode;
import Code.SystemMessageTypeCode;
import Message.MessagePack;
import Message.SubscribeMessage;
import Mapper.BarMapper;
import Mapper.BarMemberMapper;
import Mapper.PublishedPostMapper;
import Mapper.UnPublishedPostMapper;
import Service.PostService;
import Util.CopyProperties;
import Util.GetUser;
import Util.PostUtil;
import Util.OssUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import Exception.GlobalException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class PostServiceImpl extends ServiceImpl<PublishedPostMapper, PublishedPost> implements PostService
{



    @Autowired
    private UnPublishedPostMapper unPublishedPostMapper;

    @Autowired
    private BarMapper barMapper;

    @Autowired
    private BarMemberMapper barMemberMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;



    @Override
    @Transactional
    public void uploadPublishedPost(Long PostId)
    {
        UserSession session = GetUser.getUser();
        UnPublishedPost unPublishedPost = unPublishedPostMapper.selectById(PostId);
        if(unPublishedPost==null)
        {
            throw new GlobalException("该帖子不存在");
        }
        if(!session.getId().equals(unPublishedPost.getPublisherId()))
        {
            throw new GlobalException("您没有权限发布该帖子");
        }
        checkBarPostPermission(unPublishedPost.getBarId(), session.getId());
        String rawContent = unPublishedPost.getContent();
        PostSave postSave = JSON.parseObject(rawContent,PostSave.class);
        Set<String> set = new HashSet<>();
        for(PostSaveBlock block:postSave.getBlocks())
        {
            if(block.getType().equals("image"))
            {
                set.add(block.getContent());
            }
        }
        OssUtil.ForEachDelete(set,"Post/"+unPublishedPost.getPublisherId()+"/"+PostId+"/");
        Long old_id = unPublishedPost.getId();
        unPublishedPost.setId(null);
        PublishedPost res = CopyProperties.copyProperties(unPublishedPost,PublishedPost.class);
        save(res);
        unPublishedPostMapper.deleteById(old_id);
        //发布成功后，向通知链路发送消息，由Forum-Notification推送给发布者的所有粉丝
        SubscribeMessage subscribeMessage = new SubscribeMessage();
        subscribeMessage.setPublisherId(res.getPublisherId());
        subscribeMessage.setPublisherNickname(res.getPublisherNickname());
        subscribeMessage.setPostId(res.getId());
        subscribeMessage.setPostTitle(res.getTitle());
        subscribeMessage.setBarName(res.getBarName());
        //MessagePack包装领域消息，json序列化之后存入SystemMessage的message字段，队列里传输的是整个SystemMessage
        MessagePack<SubscribeMessage> messagePack = new MessagePack<>();
        messagePack.setType(SystemMessageTypeCode.suscribe);
        messagePack.setMessage(subscribeMessage);
        //这里传输的是一个消息模板：id在落库时生成，receiverId和chatId是每个粉丝各自不同的值，
        //由Forum-Notification在扇出时按粉丝逐一填充，所以这三项在这里留空
        SystemMessage systemMessage = new SystemMessage();
        systemMessage.setMessage(JSON.toJSONString(messagePack));
        systemMessage.setType(SystemMessageTypeCode.text);
        //发送必须在事务提交之后进行，否则消息可能先于数据库提交到达，粉丝会收到一个指向不存在帖子的通知
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
        {
            @Override
            public void afterCommit()
            {
                rabbitTemplate.convertAndSend(NotificationCode.EXCHANGER, NotificationCode.POST_ROUTING_KEY, systemMessage);
            }
        });
    }

    @Override
    public String uploadImage(InputStream inputStream,Long userId,Long postId)
    {
        UserSession session = GetUser.getUser();
        if(!session.getId().equals(userId))
        {
            throw new GlobalException("您没有权限上传该文件");
        }
        UnPublishedPost post = unPublishedPostMapper.selectById(postId);
        if(post==null||!post.getPublisherId().equals(userId))
        {
            throw new GlobalException("该草稿不存在");
        }
        String fileName = Long.valueOf(System.currentTimeMillis()).toString() + ".jpg";
        String filePath = "Post/"+userId.toString()+"/"+postId.toString()+"/";
        String res = OssUtil.upLoadFile(inputStream,fileName,filePath);
        if(res==null)
        {
            throw new GlobalException("文件上传失败");
        }
        return res;
    }

    @Override
    public Long uploadUnPublishedPost(UploadUnPublishedPostDto dto)
    {
        UserSession session = GetUser.getUser();
        checkBarPostPermission(dto.getBarId(), session.getId());
        UnPublishedPost post = CopyProperties.copyProperties(dto,UnPublishedPost.class);
        post.setPublisherId(session.getId());
        post.setPublisherNickname(session.getNickName());
        post.setId(IdWorker.getId());
        if(post.getContent()!=null)
        {
            PostSave postSave = JSON.parseObject(post.getContent(),PostSave.class);
            if(postSave!=null&&postSave.getBlocks()!=null)
            {
                for(PostSaveBlock block:postSave.getBlocks())
                {
                    if(block.getType().equals("image"))
                    {
                        if(!PostUtil.verifyPostPath(block.getContent(),session.getId(),post.getId()))
                        {
                            throw new GlobalException("错误的文件路径");
                        }
                    }
                }
            }
        }
        unPublishedPostMapper.insert(post);
        return post.getId();
    }


    @Override
    public void updateUnPublishedPost(UpdateUnPublishedPostDto dto)
    {
        UserSession session = GetUser.getUser();
        UnPublishedPost unPublishedPost = unPublishedPostMapper.selectById(dto.getTargetId());
        if(unPublishedPost==null)
        {
            throw new GlobalException("该帖子不存在");
        }
        if(!unPublishedPost.getPublisherId().equals(session.getId()))
        {
            throw new GlobalException("您没有权限更新该帖子");
        }
        checkBarPostPermission(dto.getDto().getBarId(), session.getId());
        UnPublishedPost post = CopyProperties.copyProperties(dto.getDto(),UnPublishedPost.class);
        if(post.getContent()!=null)
        {
            PostSave postSave = JSON.parseObject(post.getContent(),PostSave.class);
            Set<String> set = new HashSet<>();
            if(postSave!=null&&postSave.getBlocks()!=null)
            {
                for(PostSaveBlock block:postSave.getBlocks())
                {
                    if(block.getType().equals("image"))
                    {
                        if(!PostUtil.verifyPostPath(block.getContent(),session.getId(),unPublishedPost.getId()))
                        {
                            throw new GlobalException("错误的文件路径");
                        }
                        set.add(block.getContent());
                    }
                }
            }
            OssUtil.ForEachDelete(set,"Post/"+unPublishedPost.getPublisherId()+"/"+unPublishedPost.getId()+"/");
        }
        post.setId(unPublishedPost.getId());
        unPublishedPostMapper.updateById(post);
    }




    @Override
    public List<Long> getUnPublishedPostIds()
    {
        UserSession session = GetUser.getUser();
        List<Long> ids = unPublishedPostMapper.selectUnPublishedPostIdsByPublisherId(session.getId());
        if(ids==null)
        {
            return new ArrayList<>();
        }
        return ids;
    }

    @Override
    public UnPublishedPost getUnPublishedPost(Long id) {
        UserSession session = GetUser.getUser();
        UnPublishedPost post = unPublishedPostMapper.selectById(id);
        if(post==null)
        {
            throw new GlobalException("该帖子不存在");
        }
        if(!post.getPublisherId().equals(session.getId()))
        {
            throw new GlobalException("您没有权限查看该帖子");
        }
        return post;
    }


    private void checkBarPostPermission(Long barId, Long userId)
    {
        if(barId==null)
        {
            throw new GlobalException("帖子必须指定所属贴吧");
        }
        Bar bar = barMapper.selectById(barId);
        if(bar==null)
        {
            throw new GlobalException("该贴吧不存在");
        }
        if(Integer.valueOf(1).equals(bar.getIsBanned()))
        {
            throw new GlobalException("该贴吧已被封禁");
        }
        BarMember member = barMemberMapper.getBarMember(barId, userId);
        if(member!=null)
        {
            if(Integer.valueOf(1).equals(member.getIsBanned()))
            {
                throw new GlobalException("您在该贴吧内被禁言");
            }
        }
        else if(Integer.valueOf(1).equals(bar.getPostPermission()))
        {
            throw new GlobalException("该贴吧只有成员才能发帖");
        }
    }


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


    @Override
    public PublishedPost getPublishedPost(Long id)
    {
        PublishedPost post = getById(id);
        if(post==null)
        {
            throw new GlobalException("该帖子不存在");
        }
        checkBarNotBanned(post.getBarId());
        if(Integer.valueOf(1).equals(post.getIsBanned()))
        {
            throw new GlobalException("该帖子已被封禁");
        }
        getBaseMapper().addViewCount(id);
        post.setViewCount(post.getViewCount()+1);
        return post;
    }

    @Override
    public Page<PublishedPost> getPublishedPosts(GetPublishedPostsDto dto)
    {
        checkBarNotBanned(dto.getBarId());
        LambdaQueryWrapper<PublishedPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PublishedPost::getBarId, dto.getBarId());
        //排除被封禁的帖子
        wrapper.and(w -> w.isNull(PublishedPost::getIsBanned).or().ne(PublishedPost::getIsBanned,1));
        wrapper.orderByDesc(PublishedPost::getPin);
        if(dto.getSortType()==PostCode.SORT_LIKE)
        {
            wrapper.orderByDesc(PublishedPost::getLikeCount);
        }
        else if(dto.getSortType()==PostCode.SORT_VIEW)
        {
            wrapper.orderByDesc(PublishedPost::getViewCount);
        }
        else if(dto.getSortType()==PostCode.SORT_COMMENT)
        {
            wrapper.orderByDesc(PublishedPost::getCommentCount);
        }
        else if(dto.getSortType()==PostCode.SORT_TIME)
        {
            wrapper.orderByDesc(PublishedPost::getCreateTime);
        }
        else
        {
            throw new GlobalException("不支持的排序方式");
        }
        wrapper.orderByDesc(PublishedPost::getId);
        Page<PublishedPost> page = new Page<>(dto.getPage(), dto.getSize());
        return page(page, wrapper);
    }
}
