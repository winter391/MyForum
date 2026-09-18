package org.example.clientpc.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.clientpc.http.ApiClient;
import org.example.clientpc.http.ServiceUrls;
import org.example.clientpc.model.IdName;
import org.example.clientpc.model.Result;
import org.springframework.stereotype.Service;

import java.util.List;

/** IM-platform（8080）：私信发送、关注/取关、粉丝与关注列表 */
@Service
public class ImApi {

    private final ApiClient api;
    private final ServiceUrls urls;

    public ImApi(ApiClient api, ServiceUrls urls) {
        this.api = api;
        this.urls = urls;
    }

    /** 发送文字私信（后端要求双方互相关注） */
    public void sendMessage(Long receiverId, String message) {
        api.post(urls.imPlatform() + "/MyForum/SendStringPrivateMessage",
                new SendDto(receiverId, message));
    }

    /** 关注（请求体为裸数字的用户 id） */
    public void subscribe(Long userId) {
        api.post(urls.imPlatform() + "/MyForum/Subscribe", userId);
    }

    /** 取消关注 */
    public void unsubscribe(Long userId) {
        api.post(urls.imPlatform() + "/MyForum/Unsubscribe", userId);
    }

    /** 我的粉丝（关注了我的人） */
    public List<IdName> getFollowers() {
        return api.postForData(urls.imPlatform() + "/MyForum/GetFollowers", null,
                new TypeReference<Result<List<IdName>>>() {});
    }

    /** 我关注的人 */
    public List<IdName> getSubscribers() {
        return api.postForData(urls.imPlatform() + "/MyForum/GetSubscribers", null,
                new TypeReference<Result<List<IdName>>>() {});
    }

    record SendDto(Long receiverId, String message) {}
}
