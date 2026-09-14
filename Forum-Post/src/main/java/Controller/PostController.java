package Controller;


import Dto.*;
import Entity.PublishedPost;
import Entity.UnPublishedPost;
import Result.Result;
import Result.ResultUtil;
import Service.PostService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/MyForum")
@Slf4j
public class PostController
{
    @Autowired
    private PostService postService;


    @PostMapping("/uploadImage")
    public Result<?> uploadImage(MultipartFile request, @RequestBody @Valid UploadImageDto dto)
    {
        try
        {
            return ResultUtil.success(postService.uploadImage(request.getInputStream(),dto.getUserId(),dto.getPostId()));
        }
        catch(Exception e)
        {
            log.error("文件上传错误：{}",e.toString());
            return ResultUtil.error("上传图片错误");
        }
    }


    @PostMapping("/publishPost")
    public  Result<?> publishPost(@RequestBody UploadPublishedPostDto dto)
    {
        postService.uploadPublishedPost(dto.getId());
        return ResultUtil.success();
    }


    @PostMapping("/uploadUnPublishedPost")
    public Result<Long> upLoadUnPublishedPost(@RequestBody @Valid UploadUnPublishedPostDto dto)
    {
        return ResultUtil.success(postService.uploadUnPublishedPost(dto));
    }

    @PostMapping("/getUnPublishedPostIds")
    public Result<?> getUnPublishedPostIds()
    {
        return ResultUtil.success(postService.getUnPublishedPostIds());
    }


    @PostMapping("/getUnPublishedPost")
    public Result<UnPublishedPost> getUnPublishedPost(@RequestBody getUnPublishedPostDto dto)
    {
        return ResultUtil.success(postService.getUnPublishedPost(dto.getId()));
    }


    @PostMapping("/UpdateUnPublishedPost")
    public Result<?> UpdateUnPublishedPost(@RequestBody UpdateUnPublishedPostDto dto)
    {
        postService.updateUnPublishedPost(dto);
        return ResultUtil.success();
    }


    @PostMapping("/getPublishedPost")
    public Result<PublishedPost> getPublishedPost(@RequestBody @Valid GetPublishedPostDto dto)
    {
        return ResultUtil.success(postService.getPublishedPost(dto.getPostId()));
    }

    @PostMapping("/getPublishedPosts")
    public Result<Page<PublishedPost>> getPublishedPosts(@RequestBody @Valid GetPublishedPostsDto dto)
    {
        return ResultUtil.success(postService.getPublishedPosts(dto));
    }

}
