package org.example.clientpc.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.clientpc.http.ApiClient;
import org.example.clientpc.http.ServiceUrls;
import org.example.clientpc.model.Bar;
import org.example.clientpc.model.Page;
import org.example.clientpc.model.PublishedPost;
import org.example.clientpc.model.Result;
import org.example.clientpc.model.UserV0;
import org.springframework.stereotype.Service;

import java.util.Map;

/** Fourm-Search（8088）：帖子/吧搜索、按 id 查帖子/吧/用户 */
@Service
public class SearchApi {

    private final ApiClient api;
    private final ServiceUrls urls;

    public SearchApi(ApiClient api, ServiceUrls urls) {
        this.api = api;
        this.urls = urls;
    }

    public Page<PublishedPost> searchPosts(String keyword, int page, int size) {
        return api.postForData(urls.fourmSearch() + "/MyForum/Search/searchPosts",
                Map.of("keyword", keyword, "page", page, "size", size),
                new TypeReference<Result<Page<PublishedPost>>>() {});
    }

    public Page<Bar> searchBars(String keyword, int page, int size) {
        return api.postForData(urls.fourmSearch() + "/MyForum/Search/searchBars",
                Map.of("keyword", keyword, "page", page, "size", size),
                new TypeReference<Result<Page<Bar>>>() {});
    }

    public PublishedPost getPostById(Long postId) {
        return api.postForData(urls.fourmSearch() + "/MyForum/Search/getPostById",
                Map.of("postId", postId),
                new TypeReference<Result<PublishedPost>>() {});
    }

    public Bar getBarById(Long barId) {
        return api.postForData(urls.fourmSearch() + "/MyForum/Search/getBarById",
                Map.of("barId", barId),
                new TypeReference<Result<Bar>>() {});
    }

    /** 用户公开资料（查看他人主页 / 解析成员昵称用） */
    public UserV0 searchUserById(Long userId) {
        return api.postForData(urls.fourmSearch() + "/MyForum/Search/searchUserById",
                Map.of("userId", userId),
                new TypeReference<Result<UserV0>>() {});
    }
}
