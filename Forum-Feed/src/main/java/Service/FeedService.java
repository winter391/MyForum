package Service;

import Dto.GetFollowingPostsDto;
import Dto.GetRecommendPostsDto;
import Entity.PublishedPost;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;

public interface FeedService extends IService<PublishedPost>
{
    //关注流：查询当前用户关注的人发布的帖子，按发布时间分页返回
    public Page<PublishedPost> getFollowingPosts(GetFollowingPostsDto dto);

    //推荐流：查询用户主页推荐的帖子，按发布时间分页返回
    public Page<PublishedPost> getRecommendPosts(GetRecommendPostsDto dto);
}
