package org.example.clientpc.ui.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.example.clientpc.api.ImApi;
import org.example.clientpc.api.SearchApi;
import org.example.clientpc.model.UserV0;
import org.example.clientpc.ui.Router;
import org.example.clientpc.util.UiUtil;

/**
 * 用户主页：头像、昵称、关注/取消关注。
 */
public class UserProfileView extends VBox {

    public UserProfileView(Router router, SearchApi searchApi, ImApi imApi,
                           long myId, long userId) {
        setSpacing(10);
        setPadding(new Insets(14));

        Button back = new Button("← 返回");
        back.getStyleClass().add("btn-secondary");
        back.setOnAction(e -> router.back());

        // 头像（无图时显示昵称首字）
        ImageView avatar = new ImageView();
        avatar.setFitWidth(72);
        avatar.setFitHeight(72);
        Circle clip = new Circle(36, 36, 36);
        avatar.setClip(clip);
        Label letter = new Label("?");
        letter.setStyle("-fx-font-size: 28; -fx-text-fill: white;");
        StackPane avatarBox = new StackPane(avatar, letter);
        avatarBox.setPrefSize(72, 72);
        avatarBox.setStyle("-fx-background-color: #3370ff; -fx-background-radius: 36;");

        Label nick = new Label("加载中…");
        nick.getStyleClass().add("h1");

        Label idLabel = new Label();
        idLabel.getStyleClass().add("muted");

        Button followBtn = new Button();
        followBtn.setVisible(false);

        HBox head = new HBox(16, avatarBox, new VBox(6, nick, idLabel, followBtn));
        head.setAlignment(Pos.CENTER_LEFT);
        head.setPadding(new Insets(8, 0, 8, 0));

        Label postsHint = new Label();
        postsHint.getStyleClass().add("muted");
        postsHint.setWrapText(true);

        getChildren().addAll(back, head, postsHint);

        boolean self = myId == userId;

        UiUtil.async(() -> {
            UserV0 user = searchApi.searchUserById(userId);
            java.util.List<org.example.clientpc.model.IdName> following = imApi.getSubscribers();
            UiUtil.fx(() -> {
                nick.setText(user.getNickName());
                letter.setText(user.getNickName() == null || user.getNickName().isEmpty()
                        ? "?" : user.getNickName().substring(0, 1));
                idLabel.setText("ID：" + userId);
                if (user.getHeadImage() != null && !user.getHeadImage().isBlank()) {
                    try {
                        avatar.setImage(new Image(user.getHeadImage(), true));
                    } catch (Exception ignored) {
                    }
                }

                if (self) {
                    followBtn.setText("这是我");
                    followBtn.setDisable(true);
                    followBtn.setVisible(true);
                    followBtn.getStyleClass().add("btn-secondary");
                    return;
                }

                boolean followed = following != null && following.stream()
                        .anyMatch(u -> u.getId() != null && u.getId() == userId);
                renderFollowBtn(imApi, router, followBtn, userId, followed);
            });
        }, UiUtil::error);
    }

    private void renderFollowBtn(ImApi imApi, Router router, Button btn,
                                 long userId, boolean followed) {
        btn.setVisible(true);
        Runnable refresh = () -> UiUtil.async(() -> {
            java.util.List<org.example.clientpc.model.IdName> following = imApi.getSubscribers();
            UiUtil.fx(() -> {
                boolean now = following != null && following.stream()
                        .anyMatch(u -> u.getId() != null && u.getId() == userId);
                renderFollowBtn(imApi, router, btn, userId, now);
            });
        }, UiUtil::error);

        btn.getStyleClass().clear();
        btn.getStyleClass().add(followed ? "btn-secondary" : "btn");
        btn.setText(followed ? "已关注" : "+ 关注");
        btn.setOnAction(e -> {
            btn.setDisable(true);
            UiUtil.async(() -> {
                if (followed) {
                    imApi.unsubscribe(userId);
                } else {
                    imApi.subscribe(userId);
                }
                UiUtil.fx(() -> btn.setDisable(false));
                refresh.run();
            }, msg -> {
                UiUtil.error(msg);
                UiUtil.fx(() -> btn.setDisable(false));
            });
        });
    }
}
