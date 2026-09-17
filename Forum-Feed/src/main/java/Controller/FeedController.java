package Controller;

import Dto.GetFollowingPostsDto;
import Dto.GetRecommendPostsDto;
import Entity.PublishedPost;
import Result.Result;
import Result.ResultUtil;
import Service.FeedService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/MyForum/Feed")
@Slf4j
public class FeedController
{
    @Autowired
    private FeedService feedService;


    //关注流，返回当前用户关注的人发布的帖子
    @PostMapping("/getFollowingPosts")
    public Result<Page<PublishedPost>> getFollowingPosts(@RequestBody @Valid GetFollowingPostsDto dto)
    {
        return ResultUtil.success(feedService.getFollowingPosts(dto));
    }

    //推荐流，返回用户主页推荐的帖子
    @PostMapping("/getRecommendPosts")
    public Result<Page<PublishedPost>> getRecommendPosts(@RequestBody @Valid GetRecommendPostsDto dto)
    {
        return ResultUtil.success(feedService.getRecommendPosts(dto));
    }
}
