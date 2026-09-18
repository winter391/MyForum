package org.example.clientpc.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.clientpc.http.ApiClient;
import org.example.clientpc.http.ServiceUrls;
import org.example.clientpc.model.Comment;
import org.example.clientpc.model.Page;
import org.example.clientpc.model.Result;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/** Forum-Comment（8085）：评论发布、点赞、排序查询 */
@Service
public class CommentApi {

    /** 排序：0 按时间 1 按点赞数 */
    public static final int SORT_TIME = 0;
    public static final int SORT_LIKE = 1;

    private final ApiClient api;
    private final ServiceUrls urls;

    public CommentApi(ApiClient api, ServiceUrls urls) {
        this.api = api;
        this.urls = urls;
    }

    /** 发布一级评论（parentId 为 null），返回新评论 id */
    public Long publishComment(Long postId, String content) {
        Map<String, Object> body = new HashMap<>();
        body.put("postId", postId);
        body.put("content", content);
        body.put("parentId", null);
        return api.postForData(urls.forumComment() + "/MyForum/Comment/publishComment",
                body, new TypeReference<Result<Long>>() {});
    }

    public void likeComment(Long commentId) {
        api.post(urls.forumComment() + "/MyForum/Comment/likeComment",
                Map.of("commentId", commentId));
    }

    public void unlikeComment(Long commentId) {
        api.post(urls.forumComment() + "/MyForum/Comment/unlikeComment",
                Map.of("commentId", commentId));
    }

    public Page<Comment> getComments(Long postId, int sortType, int page, int size) {
        return api.postForData(urls.forumComment() + "/MyForum/Comment/getComments",
                Map.of("postId", postId, "sortType", sortType, "page", page, "size", size),
                new TypeReference<Result<Page<Comment>>>() {});
    }
}
