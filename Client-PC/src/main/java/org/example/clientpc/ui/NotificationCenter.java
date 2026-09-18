package org.example.clientpc.ui;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 会话内通知中心：收集本次运行期间经 WebSocket 收到的系统消息（关注的人发帖推送等）。
 * 注意：后端目前没有通知列表查询接口，通知仅在客户端在线期间可积累。
 */
@Component
public class NotificationCenter {

    @Getter
    public static class NotificationItem {
        private final String text;
        private final Long postId;

        public NotificationItem(String text, Long postId) {
            this.text = text;
            this.postId = postId;
        }
    }

    private final List<NotificationItem> items = new ArrayList<>();
    private final List<Consumer<NotificationItem>> listeners = new ArrayList<>();

    public synchronized void add(NotificationItem item) {
        items.add(item);
        new ArrayList<>(listeners).forEach(l -> l.accept(item));
    }

    public synchronized List<NotificationItem> getItems() {
        return new ArrayList<>(items);
    }

    public synchronized void clear() {
        items.clear();
    }

    public synchronized void addListener(Consumer<NotificationItem> l) {
        listeners.add(l);
    }
}
