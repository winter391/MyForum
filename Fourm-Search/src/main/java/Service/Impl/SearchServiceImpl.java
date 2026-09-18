package Service.Impl;

import Code.BarCode;
import Code.UserCode;
import Dto.SearchBarsDto;
import Dto.SearchPostsDto;
import Entity.Bar;
import Entity.PublishedPost;
import Entity.User;
import Exception.GlobalException;
import Mapper.BarMapper;
import Mapper.PublishedPostMapper;
import Mapper.UserMapper;
import Service.SearchService;
import Util.CopyProperties;
import V0.UserV0;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class SearchServiceImpl implements SearchService
{
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PublishedPostMapper publishedPostMapper;

    @Autowired
    private BarMapper barMapper;


    @Override
    public UserV0 searchUserById(Long userId)
    {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        //只查询展示需要的列，不查出密码等敏感字段
        wrapper.select(User::getId,User::getNickName,User::getHeadImage,User::getHeadImageThumb);
        wrapper.eq(User::getId,userId);
        User user = userMapper.selectOne(wrapper);
        if(user==null)
        {
            throw new GlobalException("该用户不存在");
        }
        if(UserCode.isBanned(user.getIsBanned()))
        {
            throw new GlobalException("该用户已被封禁");
        }
        return CopyProperties.copyProperties(user,UserV0.class);
    }

    @Override
    public List<UserV0> searchUserByNickName(String nickName)
    {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        //只查询展示需要的列，不查出密码等敏感字段
        wrapper.select(User::getId,User::getNickName,User::getHeadImage,User::getHeadImageThumb);
        //按昵称模糊匹配
        wrapper.like(User::getNickName,escapeLike(nickName));
        List<User> users = userMapper.selectList(wrapper);
        List<UserV0> res = new ArrayList<>();
        for(User user:users)
        {
            if(UserCode.isBanned(user.getIsBanned())) continue;
            res.add(CopyProperties.copyProperties(user,UserV0.class));
        }
        return res;
    }

    @Override
    public PublishedPost getPostById(Long postId)
    {
        PublishedPost post = publishedPostMapper.selectById(postId);
        if(post==null)
        {
            throw new GlobalException("该帖子不存在");
        }
        //被封禁的贴吧内的帖子不可查看
        checkBarNotBanned(post.getBarId());
        //被封禁的帖子不可查看，只返回提示
        if(Integer.valueOf(1).equals(post.getIsBanned()))
        {
            throw new GlobalException("该帖子已被封禁");
        }
        return post;
    }

    @Override
    public Page<PublishedPost> searchPosts(SearchPostsDto dto)
    {
        String keyword = escapeLike(dto.getKeyword());
        LambdaQueryWrapper<PublishedPost> wrapper = new LambdaQueryWrapper<>();
        //匹配标题或内容
        wrapper.and(w -> w.like(PublishedPost::getTitle,keyword).or().like(PublishedPost::getContent,keyword));
        //排除被封禁贴吧的帖子
        excludeBannedBar(wrapper);
        //排除被封禁的帖子
        wrapper.and(w -> w.isNull(PublishedPost::getIsBanned).or().ne(PublishedPost::getIsBanned,1));
        //按发布时间排序，新的在前，id做次级排序保证翻页顺序稳定
        wrapper.orderByDesc(PublishedPost::getCreateTime);
        wrapper.orderByDesc(PublishedPost::getId);
        Page<PublishedPost> page = new Page<>(dto.getPage(), dto.getSize());
        return publishedPostMapper.selectPage(page, wrapper);
    }

    @Override
    public Bar getBarById(Long barId)
    {
        Bar bar = barMapper.selectById(barId);
        if(bar==null)
        {
            throw new GlobalException("该贴吧不存在");
        }
        if(bar.getIsBanned()==1)
        {
            throw new GlobalException("该贴吧已被封禁");
        }
        return bar;
    }

    @Override
    public Page<Bar> searchBars(SearchBarsDto dto)
    {
        String keyword = escapeLike(dto.getKeyword());
        LambdaQueryWrapper<Bar> wrapper = new LambdaQueryWrapper<>();
        //匹配贴吧名字或简介
        wrapper.and(w -> w.like(Bar::getName,keyword).or().like(Bar::getDescription,keyword));
        //按成员数排序，大的贴吧在前，id做次级排序保证翻页顺序稳定
        wrapper.orderByDesc(Bar::getMemberCount);
        wrapper.orderByDesc(Bar::getId);
        Page<Bar> page = new Page<>(dto.getPage(), dto.getSize());
        return barMapper.selectPage(page, wrapper);
    }


    //转义like的通配符，防止用户输入的%、_、\改变匹配范围
    //MySQL的like默认转义符就是反斜杠
    private String escapeLike(String keyword)
    {
        return keyword.replace("\\","\\\\").replace("%","\\%").replace("_","\\_");
    }

    //校验帖子所属的贴吧没有被封禁，被封禁的贴吧内任何人都不能查看帖子
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

    //排除被封禁贴吧的帖子，没有所属贴吧的帖子不排除
    private void excludeBannedBar(LambdaQueryWrapper<PublishedPost> wrapper)
    {
        wrapper.and(w -> w.notInSql(PublishedPost::getBarId, "select id from bar where is_banned = 1")
                .or()
                .isNull(PublishedPost::getBarId));
    }
}
