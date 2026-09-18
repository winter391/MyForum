package org.example.clientpc.ui.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.clientpc.api.ImApi;
import org.example.clientpc.model.IdName;
import org.example.clientpc.ui.Router;
import org.example.clientpc.util.UiUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongFunction;

/**
 * 消息页：我关注的人与我的粉丝，点击进入聊天。
 * （后端约定：双方互相关注后才能互发消息）
 */
public class ChatListView extends VBox {

    public ChatListView(Router router, ImApi imApi, LongFunction<Integer> unreadLookup) {
        setSpacing(10);
        setPadding(new Insets(14));

        Label title = new Label("消息");
        title.getStyleClass().add("h1");

        Label hint = new Label("点击联系人开始聊天；双方互相关注后才能互发消息");
        hint.getStyleClass().add("muted");

        VBox list = new VBox(8);
        VBox.setVgrow(list, Priority.ALWAYS);

        getChildren().addAll(title, hint, list);

        UiUtil.async(() -> {
            List<IdName> following = imApi.getSubscribers();
            List<IdName> followers = imApi.getFollowers();
            UiUtil.fx(() -> {
                list.getChildren().add(sectionLabel("我关注的人"));
                if (following != null && !following.isEmpty()) {
                    following.forEach(u -> list.getChildren().add(row(router, u, unreadLookup)));
                } else {
                    list.getChildren().add(emptyRow("还没有关注任何人"));
                }
                list.getChildren().add(sectionLabel("我的粉丝"));
                if (followers != null && !followers.isEmpty()) {
                    followers.forEach(u -> list.getChildren().add(row(router, u, unreadLookup)));
                } else {
                    list.getChildren().add(emptyRow("还没有粉丝"));
                }
            });
        }, UiUtil::error);
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("h2");
        l.setPadding(new Insets(8, 0, 0, 0));
        return l;
    }

    private Label emptyRow(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("muted");
        l.setPadding(new Insets(4, 4, 4, 4));
        return l;
    }

    private HBox row(Router router, IdName user, LongFunction<Integer> unreadLookup) {
        HBox row = new HBox(10);
        row.getStyleClass().add("card");
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setStyle(row.getStyle() + "; -fx-cursor: hand;");
        row.setAlignment(Pos.CENTER_LEFT);

        Label name = new Label(user.getNickName());
        HBox.setHgrow(name, Priority.ALWAYS);

        int unread = unreadLookup.apply(user.getId());
        Label badge = new Label();
        if (unread > 0) {
            badge.setText(unread + " 条新消息");
            badge.setStyle("-fx-background-color: #f54a45; -fx-text-fill: white;"
                    + "-fx-padding: 2 8; -fx-background-radius: 10; -fx-font-size: 11;");
        }

        row.getChildren().addAll(name, badge);
        row.setOnMouseClicked(e -> router.openChat(user.getId(), user.getNickName()));
        return row;
    }
}
