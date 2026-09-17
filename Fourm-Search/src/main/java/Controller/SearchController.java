package Controller;

import Dto.GetBarByIdDto;
import Dto.GetPostByIdDto;
import Dto.SearchBarsDto;
import Dto.SearchPostsDto;
import Dto.SearchUserByIdDto;
import Dto.SearchUserByNickNameDto;
import Entity.Bar;
import Entity.PublishedPost;
import Result.Result;
import Result.ResultUtil;
import Service.SearchService;
import V0.UserV0;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/MyForum/Search")
@Slf4j
public class SearchController
{
    @Autowired
    private SearchService searchService;


    //按用户id精确搜索用户
    @PostMapping("/searchUserById")
    public Result<UserV0> searchUserById(@RequestBody @Valid SearchUserByIdDto dto)
    {
        return ResultUtil.success(searchService.searchUserById(dto.getUserId()));
    }

    //按昵称模糊搜索用户
    @PostMapping("/searchUserByNickName")
    public Result<List<UserV0>> searchUserByNickName(@RequestBody @Valid SearchUserByNickNameDto dto)
    {
        return ResultUtil.success(searchService.searchUserByNickName(dto.getNickName()));
    }

    //按帖子id搜索帖子
    @PostMapping("/getPostById")
    public Result<PublishedPost> getPostById(@RequestBody @Valid GetPostByIdDto dto)
    {
        return ResultUtil.success(searchService.getPostById(dto.getPostId()));
    }

    //按关键字模糊搜索帖子，匹配标题或内容
    @PostMapping("/searchPosts")
    public Result<Page<PublishedPost>> searchPosts(@RequestBody @Valid SearchPostsDto dto)
    {
        return ResultUtil.success(searchService.searchPosts(dto));
    }

    //按贴吧id搜索贴吧
    @PostMapping("/getBarById")
    public Result<Bar> getBarById(@RequestBody @Valid GetBarByIdDto dto)
    {
        return ResultUtil.success(searchService.getBarById(dto.getBarId()));
    }

    //按关键字模糊搜索贴吧，匹配名字或简介
    @PostMapping("/searchBars")
    public Result<Page<Bar>> searchBars(@RequestBody @Valid SearchBarsDto dto)
    {
        return ResultUtil.success(searchService.searchBars(dto));
    }
}
