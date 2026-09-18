package org.example.clientpc.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.clientpc.http.ApiClient;
import org.example.clientpc.http.ServiceUrls;
import org.example.clientpc.model.Bar;
import org.example.clientpc.model.BarMember;
import org.example.clientpc.model.Result;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/** Forum-Bar（8084）：吧的创建与管理 */
@Service
public class BarApi {

    /** 成员身份：0 普通成员 1 管理员 2 吧主 */
    public static final int IDENTITY_MEMBER = 0;
    public static final int IDENTITY_ADMIN = 1;
    public static final int IDENTITY_MASTER = 2;

    private final ApiClient api;
    private final ServiceUrls urls;

    public BarApi(ApiClient api, ServiceUrls urls) {
        this.api = api;
        this.urls = urls;
    }

    public void createBar(String name, String description) {
        api.post(urls.forumBar() + "/MyForum/Bar/createBar",
                Map.of("name", name, "description", description == null ? "" : description));
    }

    public Bar getBar(Long barId) {
        return api.postForData(urls.forumBar() + "/MyForum/Bar/getBar",
                Map.of("barId", barId),
                new TypeReference<Result<Bar>>() {});
    }

    public List<BarMember> getBarMembers(Long barId) {
        return api.postForData(urls.forumBar() + "/MyForum/Bar/getBarMembers",
                Map.of("barId", barId),
                new TypeReference<Result<List<BarMember>>>() {});
    }

    public void joinBar(Long barId) {
        api.post(urls.forumBar() + "/MyForum/Bar/joinBar", Map.of("barId", barId));
    }

    public void leaveBar(Long barId) {
        api.post(urls.forumBar() + "/MyForum/Bar/leaveBar", Map.of("barId", barId));
    }

    /** 仅吧主：设置管理员（target 须为普通成员） */
    public void setAdmin(Long barId, Long userId) {
        api.post(urls.forumBar() + "/MyForum/Bar/setAdmin", memberDto(barId, userId));
    }

    /** 仅吧主：撤销管理员 */
    public void removeAdmin(Long barId, Long userId) {
        api.post(urls.forumBar() + "/MyForum/Bar/removeAdmin", memberDto(barId, userId));
    }

    /** 管理员及以上：禁言成员 */
    public void muteMember(Long barId, Long userId) {
        api.post(urls.forumBar() + "/MyForum/Bar/muteMember", memberDto(barId, userId));
    }

    /** 管理员及以上：解除禁言 */
    public void unmuteMember(Long barId, Long userId) {
        api.post(urls.forumBar() + "/MyForum/Bar/unmuteMember", memberDto(barId, userId));
    }

    /** 仅吧主：发帖权限 0 所有人可发帖 / 1 仅成员可发帖 */
    public void setPostPermission(Long barId, int postPermission) {
        api.post(urls.forumBar() + "/MyForum/Bar/setPostPermission",
                Map.of("barId", barId, "postPermission", postPermission));
    }

    /** 管理员及以上：置顶/取消置顶帖子（pin 1 置顶 0 取消） */
    public void setPostPin(Long barId, Long postId, int pin) {
        api.post(urls.forumBar() + "/MyForum/Bar/setPostPin",
                Map.of("barId", barId, "postId", postId, "pin", pin));
    }

    /** 校验当前用户能否在该吧发帖，不能发帖时后端直接抛错 */
    public boolean checkPostPermission(Long barId) {
        return api.postForData(urls.forumBar() + "/MyForum/Bar/checkPostPermission",
                Map.of("barId", barId),
                new TypeReference<Result<Boolean>>() {});
    }

    private Map<String, Object> memberDto(Long barId, Long userId) {
        return Map.of("barId", barId, "userId", userId);
    }
}
