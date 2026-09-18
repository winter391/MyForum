package org.example.clientpc.ui.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.clientpc.ui.Router;
import org.example.clientpc.ui.NotificationCenter;

/**
 * 通知页：展示本次运行期间收到的推送（关注的人发帖等）。
 * 注意：后端暂无通知列表查询接口，这里只显示在线期间收到的通知。
 */
public class NotificationView extends VBox {

    public NotificationView(Router router, NotificationCenter center) {
        setSpacing(10);
        setPadding(new Insets(14));

        Label title = new Label("通知");
        title.getStyleClass().add("h1");

        Label hint = new Label("你关注的人发布帖子后，通知会出现在这里");
        hint.getStyleClass().add("muted");

        VBox list = new VBox(8);
        VBox.setVgrow(list, Priority.ALWAYS);

        for (NotificationCenter.NotificationItem item : center.getItems()) {
            list.getChildren().add(row(router, item));
        }
        if (list.getChildren().isEmpty()) {
            Label empty = new Label("暂无通知");
            empty.getStyleClass().add("muted");
            empty.setPadding(new Insets(30, 0, 0, 0));
            empty.setMaxWidth(Double.MAX_VALUE);
            empty.setAlignment(Pos.CENTER);
            list.getChildren().add(empty);
        }

        getChildren().addAll(title, hint, list);
    }

    private HBox row(Router router, NotificationCenter.NotificationItem item) {
        HBox row = new HBox(8);
        row.getStyleClass().add("card");
        row.setPadding(new Insets(10, 14, 10, 14));
        if (item.getPostId() != null) {
            row.setStyle(row.getStyle() + "; -fx-cursor: hand;");
            row.setOnMouseClicked(e -> router.openPost(item.getPostId()));
        }

        Label text = new Label(item.getText());
        text.setWrapText(true);
        text.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(text, Priority.ALWAYS);

        row.getChildren().add(text);
        return row;
    }
}
