package Service.Impl;

import Dto.GetFollowingPostsDto;
import Dto.GetRecommendPostsDto;
import Entity.PublishedPost;
import Entity.UserSession;
import Mapper.PublishedPostMapper;
import Mapper.SubscribeMapper;
import Service.FeedService;
import Util.GetUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class FeedServiceImpl extends ServiceImpl<PublishedPostMapper, PublishedPost> implements FeedService
{
    @Autowired
    private SubscribeMapper subscribeMapper;


    @Override
    public Page<PublishedPost> getFollowingPosts(GetFollowingPostsDto dto)
    {
        UserSession session = GetUser.getUser();
        //查询当前用户关注的所有用户的id
        List<Long> followIds = subscribeMapper.getSubscribedIds(session.getId());
        Page<PublishedPost> page = new Page<>(dto.getPage(), dto.getSize());
        if(followIds==null||followIds.isEmpty())
        {
            //没有关注任何人时，直接返回空页
            page.setRecords(new ArrayList<>());
            return page;
        }
        LambdaQueryWrapper<PublishedPost> wrapper = new LambdaQueryWrapper<>();
        //只查询关注的人发布的帖子
        wrapper.in(PublishedPost::getPublisherId, followIds);
        //排除被封禁贴吧的帖子
        excludeBannedBar(wrapper);
        //按发布时间排序，新的在前，id做次级排序保证翻页顺序稳定
        wrapper.orderByDesc(PublishedPost::getCreateTime);
        wrapper.orderByDesc(PublishedPost::getId);
        return page(page, wrapper);
    }

    @Override
    public Page<PublishedPost> getRecommendPosts(GetRecommendPostsDto dto)
    {
        LambdaQueryWrapper<PublishedPost> wrapper = new LambdaQueryWrapper<>();
        //排除被封禁贴吧的帖子
        excludeBannedBar(wrapper);
        //推荐流暂时只按发布时间排序，新的在前，id做次级排序保证翻页顺序稳定
        wrapper.orderByDesc(PublishedPost::getCreateTime);
        wrapper.orderByDesc(PublishedPost::getId);
        Page<PublishedPost> page = new Page<>(dto.getPage(), dto.getSize());
        return page(page, wrapper);
    }


    //排除被封禁贴吧的帖子，被封禁的贴吧内任何人都不能查看帖子，信息流里也不应该出现
    //没有所属贴吧的帖子不排除
    private void excludeBannedBar(LambdaQueryWrapper<PublishedPost> wrapper)
    {
        wrapper.and(w -> w.notInSql(PublishedPost::getBarId, "select id from bar where is_banned = 1")
                .or()
                .isNull(PublishedPost::getBarId));
    }
}
