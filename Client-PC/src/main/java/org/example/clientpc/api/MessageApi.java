package org.example.clientpc.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.clientpc.http.ApiClient;
import org.example.clientpc.http.ServiceUrls;
import org.example.clientpc.model.PrivateMessage;
import org.example.clientpc.model.Result;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/** IM-Server（8081）：历史私信增量拉取 */
@Service
public class MessageApi {

    private final ApiClient api;
    private final ServiceUrls urls;

    public MessageApi(ApiClient api, ServiceUrls urls) {
        this.api = api;
        this.urls = urls;
    }

    /**
     * 增量拉取与某人的私信：返回 chat_id 大于给定游标的消息。
     * 首次传 0 拉取全部。
     */
    public List<PrivateMessage> getPrivateMessage(long chatIdCursor, long targetId) {
        return api.getForData(urls.imServer() + "/MyForum/getPrivateMessage",
                Map.of("chat_id", String.valueOf(chatIdCursor),
                        "target_id", String.valueOf(targetId)),
                new TypeReference<Result<List<PrivateMessage>>>() {});
    }
}
