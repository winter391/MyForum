package org.example.clientpc.ui.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.clientpc.api.CommentApi;
import org.example.clientpc.api.PostApi;
import org.example.clientpc.content.BlocksHtml;
import org.example.clientpc.model.Comment;
import org.example.clientpc.model.Page;
import org.example.clientpc.model.PublishedPost;
import org.example.clientpc.ui.Router;
import org.example.clientpc.ui.component.ContentWebView;
import org.example.clientpc.util.DateTimeUtil;
import org.example.clientpc.util.UiUtil;

/**
 * 帖子详情页：正文 + 评论（可按时间/点赞数排序、点赞、发表评论）。
 */
public class PostDetailView extends VBox {

    private static final int PAGE_SIZE = 20;

    private final Router router;
    private final PostApi postApi;
    private final CommentApi commentApi;
    private final long postId;

    private final Label titleLabel = new Label();
    private final Label authorLabel = new Label();
    private final Label barLabel = new Label();
    private final Label metaLabel = new Label();
    private final ContentWebView contentView = new ContentWebView();
    private final VBox commentBox = new VBox(8);
    private final Button moreBtn = new Button("加载更多评论");
    private final TextField input = new TextField();
    private final ComboBox<String> sortBox = new ComboBox<>();

    private int commentPage = 0;
    private int sortType = CommentApi.SORT_TIME;
    private PublishedPost post;

    public PostDetailView(Router router, PostApi postApi, CommentApi commentApi, long postId) {
        this.router = router;
        this.postApi = postApi;
        this.commentApi = commentApi;
        this.postId = postId;

        setSpacing(10);
        setPadding(new Insets(14));

        // ---------- 头部 ----------
        Button back = new Button("← 返回");
        back.getStyleClass().add("btn-secondary");
        back.setOnAction(e -> router.back());

        titleLabel.getStyleClass().add("h1");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        authorLabel.getStyleClass().add("link");
        barLabel.getStyleClass().add("link");
        barLabel.setStyle("-fx-text-fill: #3370ff;");
        metaLabel.getStyleClass().add("muted");
        HBox meta = new HBox(10, authorLabel, new Label("·"), barLabel, new Label("·"), metaLabel);

        // ---------- 评论区 ----------
        Label commentTitle = new Label("评论");
        commentTitle.getStyleClass().add("h2");
        sortBox.getItems().addAll("最新", "最热");
        sortBox.getSelectionModel().select(0);
        sortBox.valueProperty().addListener((o, n, v) -> {
            sortType = "最热".equals(v) ? CommentApi.SORT_LIKE : CommentApi.SORT_TIME;
            reloadComments();
        });
        HBox commentHead = new HBox(10, commentTitle, sortBox);

        moreBtn.setMaxWidth(Double.MAX_VALUE);
        moreBtn.getStyleClass().add("btn-secondary");
        moreBtn.setVisible(false);
        moreBtn.setOnAction(e -> loadComments(false));

        input.getStyleClass().add("field");
        input.setPromptText("发表评论（任何人的评论都无法被删除）");
        HBox.setHgrow(input, Priority.ALWAYS);
        input.setOnAction(e -> submitComment());
        Button send = new Button("发布");
        send.getStyleClass().add("btn");
        send.setOnAction(e -> submitComment());
        HBox inputRow = new HBox(8, input, send);

        VBox commentsSection = new VBox(8, commentHead, commentBox, moreBtn, inputRow);

        ScrollPane scroll = new ScrollPane(new VBox(12, titleLabel, meta, contentView.node(),
                commentsSection));
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("edge-to-edge");
        scroll.setStyle("-fx-background: #f5f6f7; -fx-background-color: #f5f6f7;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        getChildren().addAll(back, scroll);

        loadPost();
    }

    private void loadPost() {
        UiUtil.async(() -> {
            PublishedPost p = postApi.getPublishedPost(postId);
            UiUtil.fx(() -> {
                post = p;
                titleLabel.setText(p.getTitle());
                authorLabel.setText(p.getPublisherNickname());
                authorLabel.setOnMouseClicked(e -> router.openProfile(p.getPublisherId()));
                barLabel.setText(p.getBarName());
                barLabel.setOnMouseClicked(e -> router.openBar(p.getBarId()));
                metaLabel.setText(DateTimeUtil.format(p.getCreateTime())
                        + "  浏览 " + nz(p.getViewCount())
                        + "  评论 " + nz(p.getCommentCount()));
                contentView.setContentHtml(BlocksHtml.toHtml(BlocksHtml.parse(p.getContent())));
            });
        }, UiUtil::error);
        reloadComments();
    }

    private void reloadComments() {
        commentPage = 0;
        commentBox.getChildren().clear();
        moreBtn.setVisible(false);
        loadComments(true);
    }

    private void loadComments(boolean reset) {
        int nextPage = commentPage + 1;
        UiUtil.async(() -> {
            Page<Comment> page = commentApi.getComments(postId, sortType, nextPage, PAGE_SIZE);
            UiUtil.fx(() -> {
                commentPage = nextPage;
                if (page != null && page.getRecords() != null) {
                    page.getRecords().forEach(c ->
                            commentBox.getChildren().add(new CommentRow(c)));
                }
                boolean noMore = page == null || page.getRecords() == null
                        || page.getRecords().size() < PAGE_SIZE;
                moreBtn.setText(noMore && !commentBox.getChildren().isEmpty()
                        ? "没有更多了" : "加载更多评论");
                moreBtn.setVisible(!noMore || !commentBox.getChildren().isEmpty());
                moreBtn.setDisable(noMore);
            });
        }, UiUtil::error);
    }

    private void submitComment() {
        String text = input.getText().trim();
        if (text.isEmpty()) {
            return;
        }
        input.setDisable(true);
        UiUtil.async(() -> {
            commentApi.publishComment(postId, text);
            UiUtil.fx(() -> {
                input.clear();
                input.setDisable(false);
                reloadComments();
            });
        }, msg -> {
            input.setDisable(false);
            UiUtil.error(msg);
        });
    }

    /** 一条评论：昵称可点进主页，右侧点赞/取消点赞 */
    private class CommentRow extends HBox {

        private final Comment comment;
        private boolean liked = false;
        private int likeCount;
        private final Button likeBtn = new Button();

        CommentRow(Comment c) {
            this.comment = c;
            this.likeCount = c.getLikeCount() == null ? 0 : c.getLikeCount();
            setSpacing(10);
            setPadding(new Insets(8, 10, 8, 10));
            setStyle("-fx-background-color: white; -fx-background-radius: 8;");

            Label nick = new Label(c.getPublisherNickname());
            nick.getStyleClass().add("link");
            nick.setOnMouseClicked(e -> router.openProfile(c.getPublisherId()));

            Label content = new Label(c.getContent());
            content.setWrapText(true);
            content.setMaxWidth(Double.MAX_VALUE);

            Label time = new Label(DateTimeUtil.format(c.getCreateTime()));
            time.getStyleClass().add("muted");

            VBox left = new VBox(4, nick, content, time);
            HBox.setHgrow(left, Priority.ALWAYS);

            likeBtn.setOnAction(e -> toggleLike());
            refreshLikeBtn();

            getChildren().addAll(left, likeBtn);
        }

        private void toggleLike() {
            likeBtn.setDisable(true);
            if (!liked) {
                UiUtil.async(() -> {
                    try {
                        commentApi.likeComment(comment.getId());
                        likeCount++;
                        liked = true;
                    } catch (org.example.clientpc.http.ApiException ae) {
                        if (ae.getMessage() != null && ae.getMessage().contains("已点赞")) {
                            liked = true; // 已点过赞，修正本地状态
                        } else {
                            throw ae;
                        }
                    }
                    UiUtil.fx(this::refreshLikeBtn);
                }, msg -> {
                    UiUtil.error(msg);
                    UiUtil.fx(this::refreshLikeBtn);
                });
            } else {
                UiUtil.async(() -> {
                    commentApi.unlikeComment(comment.getId());
                    likeCount = Math.max(0, likeCount - 1);
                    liked = false;
                    UiUtil.fx(this::refreshLikeBtn);
                }, msg -> {
                    UiUtil.error(msg);
                    UiUtil.fx(this::refreshLikeBtn);
                });
            }
        }

        private void refreshLikeBtn() {
            likeBtn.setDisable(false);
            likeBtn.getStyleClass().clear();
            likeBtn.getStyleClass().add(liked ? "btn" : "btn-secondary");
            likeBtn.setText((liked ? "已赞 " : "赞 ") + likeCount);
        }
    }

    private static int nz(Integer i) {
        return i == null ? 0 : i;
    }
}
