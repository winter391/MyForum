package Service;

import Dto.SearchBarsDto;
import Dto.SearchPostsDto;
import Entity.Bar;
import Entity.PublishedPost;
import V0.UserV0;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface SearchService
{
    //按用户id精确搜索用户
    public UserV0 searchUserById(Long userId);

    //按昵称模糊搜索用户
    public List<UserV0> searchUserByNickName(String nickName);

    //按帖子id搜索帖子
    public PublishedPost getPostById(Long postId);

    //按关键字模糊搜索帖子，匹配标题或内容
    public Page<PublishedPost> searchPosts(SearchPostsDto dto);

    //按贴吧id搜索贴吧
    public Bar getBarById(Long barId);

    //按关键字模糊搜索贴吧，匹配名字或简介
    public Page<Bar> searchBars(SearchBarsDto dto);
}
