package org.example.clientpc.ui.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.clientpc.api.FeedApi;
import org.example.clientpc.model.Page;
import org.example.clientpc.model.PublishedPost;
import org.example.clientpc.ui.Router;
import org.example.clientpc.util.UiUtil;

/**
 * 主页：关注流 / 推荐流，均按发布时间倒序，分页加载。
 */
public class HomeView extends TabPane {

    private final Router router;
    private final FeedApi feedApi;

    public HomeView(Router router, FeedApi feedApi) {
        this.router = router;
        this.feedApi = feedApi;
        setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        getTabs().addAll(
                new Tab("关注", new FeedList("你关注的人还没有发过帖子",
                        (page, size) -> feedApi.getFollowingPosts(page, size))),
                new Tab("推荐", new FeedList("还没有帖子",
                        (page, size) -> feedApi.getRecommendPosts(page, size))));
    }

    @FunctionalInterface
    private interface PageLoader {
        Page<PublishedPost> load(int page, int size) throws Exception;
    }

    /** 一个可分页加载的帖子流 */
    private class FeedList extends VBox {

        private static final int PAGE_SIZE = 20;

        private final PageLoader loader;
        private final VBox listBox = new VBox(10);
        private final Label emptyHint = new Label();
        private final Button moreBtn = new Button("加载更多");
        private int page = 0;
        private boolean finished = false;

        FeedList(String emptyText, PageLoader loader) {
            this.loader = loader;
            setSpacing(10);
            setPadding(new Insets(14));
            VBox.setVgrow(this, Priority.ALWAYS);

            emptyHint.setText(emptyText);
            emptyHint.setStyle("-fx-text-fill: #8a9099; -fx-padding: 40 0 0 0;");
            emptyHint.setMaxWidth(Double.MAX_VALUE);
            emptyHint.setAlignment(javafx.geometry.Pos.CENTER);

            moreBtn.getStyleClass().add("btn-secondary");
            moreBtn.setMaxWidth(Double.MAX_VALUE);
            moreBtn.setVisible(false);
            moreBtn.setOnAction(e -> loadNext());

            listBox.setPadding(Insets.EMPTY);
            getChildren().addAll(emptyHint, listBox, moreBtn);
            VBox.setVgrow(listBox, Priority.ALWAYS);

            loadNext();
        }

        private void loadNext() {
            if (finished) {
                return;
            }
            moreBtn.setDisable(true);
            int nextPage = page + 1;
            UiUtil.async(() -> {
                Page<PublishedPost> result = loader.load(nextPage, PAGE_SIZE);
                Platform.runLater(() -> {
                    page = nextPage;
                    if (result != null && result.getRecords() != null
                            && !result.getRecords().isEmpty()) {
                        result.getRecords().forEach(p ->
                                listBox.getChildren().add(new PostCard(router, p)));
                    }
                    boolean noMore = result == null || result.getRecords() == null
                            || result.getRecords().size() < PAGE_SIZE;
                    if (noMore) {
                        finished = true;
                        if (listBox.getChildren().isEmpty()) {
                            emptyHint.setVisible(true);
                        } else {
                            moreBtn.setText("没有更多了");
                            moreBtn.setDisable(true);
                        }
                    } else {
                        moreBtn.setDisable(false);
                        moreBtn.setVisible(true);
                    }
                    emptyHint.setVisible(listBox.getChildren().isEmpty());
                });
            }, msg -> {
                UiUtil.error(msg);
                moreBtn.setDisable(false);
            });
        }

        public Node asNode() {
            return this;
        }
    }
}
