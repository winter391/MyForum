package Service;

import Dto.GetPublishedPostsDto;
import Dto.UpdateUnPublishedPostDto;
import Dto.UploadUnPublishedPostDto;
import Entity.PublishedPost;
import Entity.UnPublishedPost;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;

import java.io.InputStream;
import java.util.List;

public interface PostService extends IService<PublishedPost>
{
    public void uploadPublishedPost(Long PostId);

    public String uploadImage(InputStream inputStream, Long userId);

    public void uploadUnPublishedPost(UploadUnPublishedPostDto dto);

    public List<Long> getUnPublishedPostIds();

    public UnPublishedPost getUnPublishedPost(Long id);

    public void updateUnPublishedPost(UpdateUnPublishedPostDto dto);

    public PublishedPost getPublishedPost(Long id);

    public Page<PublishedPost> getPublishedPosts(GetPublishedPostsDto dto);
}
