package org.example.clientpc.ui.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.clientpc.content.BlocksHtml;
import org.example.clientpc.model.PublishedPost;
import org.example.clientpc.ui.Router;
import org.example.clientpc.util.DateTimeUtil;

/**
 * 帖子卡片（信息流/搜索/吧内帖子列表共用）。
 */
public class PostCard extends VBox {

    public PostCard(Router router, PublishedPost post) {
        getStyleClass().add("card");
        setSpacing(6);
        setPadding(new Insets(12, 14, 12, 14));
        setOnMouseClicked(e -> router.openPost(post.getId()));

        Label title = new Label(post.getTitle());
        title.getStyleClass().add("h2");
        title.setWrapText(true);
        title.setMaxWidth(Double.MAX_VALUE);

        Label preview = new Label(BlocksHtml.textPreview(post.getContent(), 80));
        preview.setWrapText(true);
        preview.setMaxWidth(Double.MAX_VALUE);
        preview.setStyle("-fx-text-fill: #41464c;");

        Label author = new Label(post.getPublisherNickname());
        author.getStyleClass().add("link");
        author.setOnMouseClicked(e -> {
            e.consume();
            router.openProfile(post.getPublisherId());
        });

        Label bar = new Label(post.getBarName());
        bar.getStyleClass().add("link");
        bar.setStyle("-fx-text-fill: #3370ff;");
        bar.setOnMouseClicked(e -> {
            e.consume();
            router.openBar(post.getBarId());
        });

        Label meta = new Label(DateTimeUtil.format(post.getCreateTime())
                + "  浏览 " + nz(post.getViewCount())
                + "  评论 " + nz(post.getCommentCount()));

        HBox metaRow = new HBox(10, author, new Label("·"), bar, new Label("·"), meta);
        HBox.setHgrow(meta, Priority.ALWAYS);

        if (post.getPin() != null && post.getPin() == 1) {
            Label pin = new Label("置顶");
            pin.setStyle("-fx-background-color: #fff3e0; -fx-text-fill: #e65100;"
                    + "-fx-padding: 1 6; -fx-background-radius: 4; -fx-font-size: 11;");
            HBox titleRow = new HBox(8, pin, title);
            titleRow.setSpacing(8);
            HBox.setHgrow(title, Priority.ALWAYS);
            getChildren().addAll(titleRow, preview, metaRow);
        } else {
            getChildren().addAll(title, preview, metaRow);
        }
    }

    private static int nz(Integer i) {
        return i == null ? 0 : i;
    }
}
