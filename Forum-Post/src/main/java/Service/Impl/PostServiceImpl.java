package Service.Impl;

import Dto.UpdateUnPublishedPostDto;
import Dto.UploadUnPublishedPostDto;
import Entity.*;
import Mapper.PublishedPostMapper;
import Mapper.UnPublishedPostMapper;
import Service.PostService;
import Util.CopyProperties;
import Util.GetUser;
import Util.PostUtil;
import Util.OssUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import Exception.GlobalException;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class PostServiceImpl extends ServiceImpl<PublishedPostMapper, PublishedPost> implements PostService
{

    @Autowired
    private UnPublishedPostMapper unPublishedPostMapper;




    @Override
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
        OssUtil.ForEachDelete(set,"Post/"+unPublishedPost.getPublisherId()+"/");
        Long old_id = unPublishedPost.getId();
        unPublishedPost.setId(null);
        PublishedPost res = CopyProperties.copyProperties(unPublishedPost,PublishedPost.class);
        save(res);
        unPublishedPostMapper.deleteById(old_id);
    }

    @Override
    public String uploadImage(InputStream inputStream,Long userId)
    {
        UserSession session = GetUser.getUser();
        if(!session.getId().equals(userId))
        {
            throw new GlobalException("您没有权限上传该文件");
        }
        String fileName = Long.valueOf(System.currentTimeMillis()).toString() + ".jpg";
        String filePath = "Post/"+userId.toString()+"/";
        String res = OssUtil.upLoadFile(inputStream,fileName,filePath);
        if(res==null)
        {
            throw new GlobalException("文件上传失败");
        }
        return res;
    }

    @Override
    public void uploadUnPublishedPost(UploadUnPublishedPostDto dto)
    {
        UserSession session = GetUser.getUser();
        UnPublishedPost post = CopyProperties.copyProperties(dto,UnPublishedPost.class);
        post.setPublisherId(session.getId());
        post.setPublisherNickname(session.getNickName());
        PostSave postSave = JSON.parseObject(post.getContent(),PostSave.class);
        for(PostSaveBlock block:postSave.getBlocks())
        {
            if(block.getType().equals("image"))
            {
                if(!PostUtil.verifyPostPath(block.getContent(),session.getId()))
                {
                    throw new GlobalException("错误的文件路径");
                }
            }
        }
        unPublishedPostMapper.insert(post);
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
        UnPublishedPost post = CopyProperties.copyProperties(dto.getDto(),UnPublishedPost.class);
        PostSave postSave = JSON.parseObject(post.getContent(),PostSave.class);
        Set<String> set = new HashSet<>();
        for(PostSaveBlock block:postSave.getBlocks())
        {
            if(block.getType().equals("image"))
            {
                if(!PostUtil.verifyPostPath(block.getContent(),session.getId()))
                {
                    throw new GlobalException("错误的文件路径");
                }
                set.add(block.getContent());
            }
        }
        OssUtil.ForEachDelete(set,"Post/"+unPublishedPost.getPublisherId()+"/");
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
}
