package org.example.clientpc.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.clientpc.http.ApiClient;
import org.example.clientpc.http.ServiceUrls;
import org.example.clientpc.model.Page;
import org.example.clientpc.model.PublishedPost;
import org.example.clientpc.model.Result;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

/** Forum-Post（8089）：草稿创建/更新、图片上传、发布、帖子查询 */
@Service
public class PostApi {

    /** 排序方式：0 点赞 1 浏览 2 评论 3 时间 */
    public static final int SORT_TIME = 3;

    private final ApiClient api;
    private final ServiceUrls urls;

    public PostApi(ApiClient api, ServiceUrls urls) {
        this.api = api;
        this.urls = urls;
    }

    /** 新建草稿，返回草稿 id */
    public Long createDraft(String title, String content, String barName, Long barId) {
        return api.postForData(urls.forumPost() + "/MyForum/uploadUnPublishedPost",
                Map.of("title", title, "content", content,
                        "barName", barName, "barId", barId),
                new TypeReference<Result<Long>>() {});
    }

    /** 上传帖子图片（multipart：文件 part 名 request + 表单字段 userId/postId），返回图片 URL */
    public String uploadImage(File file, Long userId, Long postId) {
        return api.postMultipartImage(urls.forumPost() + "/MyForum/uploadImage", file,
                Map.of("userId", String.valueOf(userId), "postId", String.valueOf(postId)));
    }

    /** 更新草稿（targetId 为草稿 id） */
    public void updateDraft(Long targetId, String title, String content, String barName, Long barId) {
        api.post(urls.forumPost() + "/MyForum/UpdateUnPublishedPost",
                Map.of(
                        "targetId", targetId,
                        "dto", Map.of("title", title, "content", content,
                                "barName", barName, "barId", barId)));
    }

    /** 发布草稿（发布后草稿作废，正式帖获得新 id） */
    public void publish(Long draftId) {
        api.post(urls.forumPost() + "/MyForum/publishPost", Map.of("id", draftId));
    }

    /** 帖子详情（每次调用浏览量 +1） */
    public PublishedPost getPublishedPost(Long postId) {
        return api.postForData(urls.forumPost() + "/MyForum/getPublishedPost",
                Map.of("postId", postId),
                new TypeReference<Result<PublishedPost>>() {});
    }

    /** 吧内帖子分页列表，置顶帖排最前 */
    public Page<PublishedPost> getPublishedPosts(Long barId, int sortType, int page, int size) {
        return api.postForData(urls.forumPost() + "/MyForum/getPublishedPosts",
                Map.of("barId", barId, "sortType", sortType, "page", page, "size", size),
                new TypeReference<Result<Page<PublishedPost>>>() {});
    }
}
