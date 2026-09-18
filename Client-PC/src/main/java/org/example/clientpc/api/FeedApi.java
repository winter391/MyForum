package org.example.clientpc.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.clientpc.http.ApiClient;
import org.example.clientpc.http.ServiceUrls;
import org.example.clientpc.model.Page;
import org.example.clientpc.model.PublishedPost;
import org.example.clientpc.model.Result;
import org.springframework.stereotype.Service;

import java.util.Map;

/** Forum-Feed（8086）：关注流与推荐流，均按发布时间倒序 */
@Service
public class FeedApi {

    private final ApiClient api;
    private final ServiceUrls urls;

    public FeedApi(ApiClient api, ServiceUrls urls) {
        this.api = api;
        this.urls = urls;
    }

    /** 关注流：我关注的人发布的帖子 */
    public Page<PublishedPost> getFollowingPosts(int page, int size) {
        return api.postForData(urls.forumFeed() + "/MyForum/Feed/getFollowingPosts",
                Map.of("page", page, "size", size),
                new TypeReference<Result<Page<PublishedPost>>>() {});
    }

    /** 推荐流：全站帖子按发布时间倒序 */
    public Page<PublishedPost> getRecommendPosts(int page, int size) {
        return api.postForData(urls.forumFeed() + "/MyForum/Feed/getRecommendPosts",
                Map.of("page", page, "size", size),
                new TypeReference<Result<Page<PublishedPost>>>() {});
    }
}
