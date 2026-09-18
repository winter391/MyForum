package org.example.clientpc.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.example.clientpc.http.ServiceUrls;
import org.example.clientpc.model.MessagePack;
import org.example.clientpc.model.PrivateMessage;
import org.example.clientpc.model.Result;
import org.example.clientpc.model.SystemMessage;
import org.example.clientpc.session.SessionContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * IM-Server 的 WebSocket 客户端。
 * 协议约定：连接后第一帧发送裸 accessToken（仅一次，重复发送会触发同终端互踢）；
 * 之后每 30 秒发送 "ping" 保持心跳（服务端 60 秒读空闲即断开）；
 * 收到的文本帧可能是 pong、Result JSON、私聊消息或系统消息。
 */
@Slf4j
@Component
public class ImClient {

    private final ServiceUrls urls;
    private final ObjectMapper mapper;
    private final SessionContext session;

    private WebSocket socket;
    private ScheduledExecutorService heartbeat;
    /** 是否被服务端明确拒绝（如互踢/token 失效），此时不再重连 */
    private volatile boolean rejected = false;
    private volatile boolean closedByUser = false;
    private volatile boolean loginConfirmed = false;

    private final List<Consumer<PrivateMessage>> privateListeners = new CopyOnWriteArrayList<>();
    private final List<Consumer<SystemMessage>> systemListeners = new CopyOnWriteArrayList<>();
    private final List<Consumer<String>> errorListeners = new CopyOnWriteArrayList<>();

    public ImClient(ServiceUrls urls, ObjectMapper mapper, SessionContext session) {
        this.urls = urls;
        this.mapper = mapper;
        this.session = session;
    }

    public void addPrivateMessageListener(Consumer<PrivateMessage> l) {
        privateListeners.add(l);
    }

    public void addSystemMessageListener(Consumer<SystemMessage> l) {
        systemListeners.add(l);
    }

    public void addErrorListener(Consumer<String> l) {
        errorListeners.add(l);
    }

    /** 登录成功后调用，建立 WebSocket 连接 */
    public synchronized void connect(String accessToken) {
        rejected = false;
        closedByUser = false;
        loginConfirmed = false;
        open(accessToken);
    }

    private void open(String accessToken) {
        Request request = new Request.Builder().url(urls.websocket()).build();
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS) // 心跳由应用层 ping 维持
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();
        socket = client.newWebSocket(request, new WsListener(accessToken));
    }

    public synchronized void close() {
        closedByUser = true;
        stopHeartbeat();
        if (socket != null) {
            socket.close(1000, "logout");
            socket = null;
        }
    }

    private void startHeartbeat() {
        stopHeartbeat();
        heartbeat = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "im-heartbeat");
            t.setDaemon(true);
            return t;
        });
        heartbeat.scheduleAtFixedRate(() -> {
            WebSocket ws = socket;
            if (ws != null) {
                ws.send("ping");
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    private void stopHeartbeat() {
        if (heartbeat != null) {
            heartbeat.shutdownNow();
            heartbeat = null;
        }
    }

    private void notifyError(String msg) {
        errorListeners.forEach(l -> l.accept(msg));
    }

    private class WsListener extends WebSocketListener {

        private final String accessToken;

        WsListener(String accessToken) {
            this.accessToken = accessToken;
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            webSocket.send(accessToken);
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            if ("pong".equals(text)) {
                return;
            }
            try {
                JsonNode node = mapper.readTree(text);
                if (node.has("code")) {
                    // 登录确认或错误
                    int code = node.path("code").asInt();
                    if (code == Result.SUCCESS) {
                        if (!loginConfirmed) {
                            loginConfirmed = true;
                            startHeartbeat();
                        }
                    } else {
                        rejected = true;
                        notifyError(node.path("msg").asText("IM 连接被服务端拒绝"));
                    }
                    return;
                }
                if (node.has("senderId")) {
                    PrivateMessage pm = mapper.treeToValue(node, PrivateMessage.class);
                    privateListeners.forEach(l -> l.accept(pm));
                    return;
                }
                if (node.has("receiverId")) {
                    SystemMessage sm = mapper.treeToValue(node, SystemMessage.class);
                    systemListeners.forEach(l -> l.accept(sm));
                }
            } catch (Exception e) {
                // 非 JSON 纯文本（如互踢提示）
                log.warn("IM 收到无法解析的消息：{}", text);
                rejected = true;
                notifyError(text);
            }
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            log.warn("IM 连接失败：{}", t.toString());
            handleDisconnect();
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            log.info("IM 连接关闭：{} {}", code, reason);
            handleDisconnect();
        }
    }

    private synchronized void handleDisconnect() {
        stopHeartbeat();
        if (closedByUser || rejected) {
            return;
        }
        // 网络波动等意外断开：10 秒后用最新 token 重连
        Thread retry = new Thread(() -> {
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                return;
            }
            synchronized (ImClient.this) {
                if (!closedByUser && !rejected && session.isLoggedIn()) {
                    open(session.getAccessToken());
                }
            }
        }, "im-reconnect");
        retry.setDaemon(true);
        retry.start();
    }
}
