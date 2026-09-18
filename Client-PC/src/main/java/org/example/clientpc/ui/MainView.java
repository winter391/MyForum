package org.example.clientpc.ui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.clientpc.model.MessagePack;
import org.example.clientpc.model.PrivateMessage;
import org.example.clientpc.model.SystemMessage;
import org.example.clientpc.session.SessionContext;
import org.example.clientpc.ui.view.BarView;
import org.example.clientpc.ui.view.ChatListView;
import org.example.clientpc.ui.view.ChatView;
import org.example.clientpc.ui.view.HomeView;
import org.example.clientpc.ui.view.NotificationView;
import org.example.clientpc.ui.view.PostDetailView;
import org.example.clientpc.ui.view.SearchView;
import org.example.clientpc.ui.view.UserProfileView;
import org.example.clientpc.util.UiUtil;
import org.example.clientpc.ws.ImClient;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 主窗口：左侧导航 + 中央内容区。实现 Router 做页面导航，
 * 并作为 IM 消息的路由中枢（聊天页打开时直投，否则计未读）。
 */
public class MainView extends BorderPane implements Router {

    private final ViewFactory vf;
    private final SessionContext session;
    private final NotificationCenter notifCenter;
    private final Runnable onLogout;

    private final StackPane contentHolder = new StackPane();
    private final Deque<Runnable> history = new ArrayDeque<>();
    private Runnable currentRenderer;
    private ChatView activeChat;

    private final Map<Long, Integer> unreadByUser = new ConcurrentHashMap<>();
    private final Label unreadBadge = new Label();
    private final Button msgNavBtn = new Button("消息");

    public MainView(ViewFactory vf, ImClient im, NotificationCenter notifCenter,
                    SessionContext session, Runnable onLogout) {
        this.vf = vf;
        this.session = session;
        this.notifCenter = notifCenter;
        this.onLogout = onLogout;

        setStyle("-fx-background-color: #f5f6f7;");
        setLeft(buildNav());
        setCenter(contentHolder);

        // IM 消息路由（ImClient 回调在后台线程，统一切回 FX 线程）
        im.addPrivateMessageListener(pm -> UiUtil.fx(() -> routePrivateMessage(pm)));
        im.addSystemMessageListener(sm -> UiUtil.fx(() -> routeSystemMessage(sm)));
        im.addErrorListener(msg -> UiUtil.fx(() -> Toast.show("IM：" + msg)));

        showHome();
    }

    // ---------------- 导航栏 ----------------

    private VBox buildNav() {
        VBox nav = new VBox(6);
        nav.getStyleClass().add("nav");

        Button homeBtn = new Button("首页");
        Button searchBtn = new Button("搜索");
        msgNavBtn.getStyleClass().add("nav-button");
        Button notifBtn = new Button("通知");

        for (Button b : new Button[]{homeBtn, searchBtn, msgNavBtn, notifBtn}) {
            b.getStyleClass().add("nav-button");
            b.setMaxWidth(Double.MAX_VALUE);
        }

        homeBtn.setOnAction(e -> showHome());
        searchBtn.setOnAction(e -> showSearch());
        msgNavBtn.setOnAction(e -> showMessages());
        notifBtn.setOnAction(e -> showNotifications());

        unreadBadge.setStyle("-fx-background-color: #f54a45; -fx-text-fill: white;"
                + "-fx-padding: 1 7; -fx-background-radius: 9; -fx-font-size: 11;");
        unreadBadge.setVisible(false);
        HBox msgRow = new HBox(6, msgNavBtn, unreadBadge);
        HBox.setHgrow(msgNavBtn, Priority.ALWAYS);
        msgRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label user = new Label(session.getNickName());
        user.getStyleClass().add("muted");
        Button logoutBtn = new Button("退出登录");
        logoutBtn.getStyleClass().add("btn-secondary");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> {
            if (UiUtil.confirm("确定退出登录吗？")) {
                onLogout.run();
            }
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        nav.getChildren().addAll(homeBtn, searchBtn, msgRow, notifBtn, spacer, user, logoutBtn);
        return nav;
    }

    // ---------------- Router 实现 ----------------

    @Override
    public void showHome() {
        resetTo(() -> setPage(new HomeView(this, vf.feedApi)));
    }

    @Override
    public void showSearch() {
        resetTo(() -> setPage(new SearchView(this, vf.searchApi, vf.barApi)));
    }

    @Override
    public void showMessages() {
        resetTo(() -> setPage(new ChatListView(this, vf.imApi,
                userId -> unreadByUser.getOrDefault(userId, 0))));
    }

    @Override
    public void showNotifications() {
        resetTo(() -> setPage(new NotificationView(this, notifCenter)));
    }

    @Override
    public void openPost(long postId) {
        pushAndShow(() -> setPage(new PostDetailView(this, vf.postApi, vf.commentApi, postId)));
    }

    @Override
    public void openBar(long barId) {
        pushAndShow(() -> setPage(new BarView(this, vf.barApi, vf.postApi, vf.searchApi,
                vf.session, barId)));
    }

    @Override
    public void openProfile(long userId) {
        pushAndShow(() -> setPage(new UserProfileView(this, vf.searchApi, vf.imApi,
                vf.session.getUserId(), userId)));
    }

    @Override
    public void openChat(long userId, String nickName) {
        unreadByUser.remove(userId);
        updateBadge();
        pushAndShow(() -> setPage(new ChatView(this, vf.imApi, vf.messageApi,
                vf.session.getUserId(), userId, nickName)));
    }

    @Override
    public void back() {
        if (!history.isEmpty()) {
            currentRenderer = history.pop();
            currentRenderer.run();
        }
    }

    // ---------------- 导航内部 ----------------

    private void resetTo(Runnable renderer) {
        history.clear();
        currentRenderer = renderer;
        renderer.run();
    }

    private void pushAndShow(Runnable renderer) {
        if (currentRenderer != null) {
            history.push(currentRenderer);
        }
        currentRenderer = renderer;
        renderer.run();
    }

    private void setPage(Node node) {
        contentHolder.getChildren().setAll(node);
        activeChat = node instanceof ChatView cv ? cv : null;
    }

    private void updateBadge() {
        int total = unreadByUser.values().stream().mapToInt(Integer::intValue).sum();
        UiUtil.fx(() -> {
            unreadBadge.setText(String.valueOf(total));
            unreadBadge.setVisible(total > 0);
        });
    }

    // ---------------- IM 消息路由 ----------------

    private void routePrivateMessage(PrivateMessage pm) {
        if (pm == null || pm.getReceiverId() == null
                || !pm.getReceiverId().equals(session.getUserId())) {
            return;
        }
        if (activeChat != null && pm.getSenderId() != null
                && pm.getSenderId() == activeChat.getPeerId()) {
            activeChat.onIncoming(pm);
            return;
        }
        unreadByUser.merge(pm.getSenderId(), 1, Integer::sum);
        updateBadge();
        Toast.show("收到一条新私信");
    }

    private void routeSystemMessage(SystemMessage sm) {
        if (sm == null || sm.getMessage() == null) {
            return;
        }
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            MessagePack pack = mapper.readValue(sm.getMessage(), MessagePack.class);
            String text;
            Long postId = null;
            if (pack.getType() != null && pack.getType() == MessagePack.TYPE_SUBSCRIBE
                    && pack.getMessage() != null) {
                MessagePack.SubscribeMessage sub = mapper.treeToValue(
                        pack.getMessage(), MessagePack.SubscribeMessage.class);
                text = "你关注的 " + sub.getPublisherNickname() + " 在「" + sub.getBarName()
                        + "」发布了新帖：" + sub.getPostTitle();
                postId = sub.getPostId();
            } else {
                text = "系统消息：" + (pack.getMessage() == null ? ""
                        : pack.getMessage().path("message").asText(""));
            }
            notifCenter.add(new NotificationCenter.NotificationItem(text, postId));
            Toast.show(text);
        } catch (Exception e) {
            Toast.show("收到一条系统通知");
        }
    }
}
