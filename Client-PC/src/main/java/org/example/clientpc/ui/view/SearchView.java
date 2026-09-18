package org.example.clientpc.ui.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.clientpc.api.BarApi;
import org.example.clientpc.api.SearchApi;
import org.example.clientpc.model.Bar;
import org.example.clientpc.model.Page;
import org.example.clientpc.model.PublishedPost;
import org.example.clientpc.ui.Router;
import org.example.clientpc.util.UiUtil;

/**
 * 搜索页：按关键字搜索帖子与吧；创建吧入口。
 */
public class SearchView extends VBox {

    private final Router router;
    private final SearchApi searchApi;
    private final BarApi barApi;

    private final TextField keywordField = new TextField();
    private final TabPane tabs = new TabPane();
    private final ResultList<PublishedPost> postList;
    private final ResultList<Bar> barList;

    public SearchView(Router router, SearchApi searchApi, BarApi barApi) {
        this.router = router;
        this.searchApi = searchApi;
        this.barApi = barApi;

        setSpacing(10);
        setPadding(new Insets(14));

        keywordField.getStyleClass().add("field");
        keywordField.setPromptText("搜索帖子标题 / 内容，或吧名 / 简介");
        HBox.setHgrow(keywordField, Priority.ALWAYS);
        keywordField.setOnAction(e -> search());

        Button searchBtn = new Button("搜索");
        searchBtn.getStyleClass().add("btn");
        searchBtn.setOnAction(e -> search());

        Button createBarBtn = new Button("创建吧");
        createBarBtn.getStyleClass().add("btn-secondary");
        createBarBtn.setOnAction(e -> showCreateBarDialog());

        HBox searchRow = new HBox(10, keywordField, searchBtn, createBarBtn);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        postList = new ResultList<>("输入关键字搜索帖子", searchApi::searchPosts);
        barList = new ResultList<>("输入关键字搜索吧", searchApi::searchBars);

        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(new Tab("帖子", postList), new Tab("吧", barList));
        VBox.setVgrow(tabs, Priority.ALWAYS);

        getChildren().addAll(searchRow, tabs);
    }

    private void search() {
        String kw = keywordField.getText().trim();
        if (kw.isEmpty()) {
            UiUtil.error("请输入搜索关键字");
            return;
        }
        if (tabs.getSelectionModel().getSelectedIndex() == 0) {
            postList.search(kw);
        } else {
            barList.search(kw);
        }
    }

    private void showCreateBarDialog() {
        Stage owner = (Stage) getScene().getWindow();

        TextField nameField = new TextField();
        nameField.getStyleClass().add("field");
        nameField.setPromptText("吧名（1-12 字）");
        nameField.textProperty().addListener((o, n, v) -> {
            if (v.length() > 12) {
                nameField.setText(v.substring(0, 12));
            }
        });

        TextField descField = new TextField();
        descField.getStyleClass().add("field");
        descField.setPromptText("吧简介（200 字以内，可不填）");
        descField.textProperty().addListener((o, n, v) -> {
            if (v.length() > 200) {
                descField.setText(v.substring(0, 200));
            }
        });

        Button ok = new Button("创建");
        ok.getStyleClass().add("btn");
        // 不用 bind：点击处理里要手动 setDisable，绑定后 set 会抛异常
        final boolean[] busy = {false};
        Runnable refreshOk = () -> ok.setDisable(busy[0] || nameField.getText().isEmpty());
        nameField.textProperty().addListener((o, n, v) -> refreshOk.run());
        refreshOk.run();

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle("创建吧");
        VBox box = new VBox(10, new Label("吧名："), nameField,
                new Label("简介："), descField, ok);
        box.setPadding(new Insets(16));
        box.setPrefSize(380, 240);
        dialog.setScene(new javafx.scene.Scene(box));

        ok.setOnAction(e -> {
            busy[0] = true;
            refreshOk.run();
            String name = nameField.getText().trim();
            String desc = descField.getText().trim();
            UiUtil.async(() -> {
                barApi.createBar(name, desc);
                Platform.runLater(() -> {
                    UiUtil.info("创建成功，你已成为该吧的吧主");
                    dialog.close();
                    tabs.getSelectionModel().select(1);
                    keywordField.setText(name);
                    barList.search(name);
                });
            }, msg -> {
                UiUtil.error(msg);
                Platform.runLater(() -> {
                    busy[0] = false;
                    refreshOk.run();
                });
            });
        });
        dialog.showAndWait();
    }

    @FunctionalInterface
    private interface Searcher<T> {
        Page<T> search(String keyword, int page, int size) throws Exception;
    }

    /** 可分页的关键字搜索结果列表 */
    private class ResultList<T> extends VBox {

        private static final int PAGE_SIZE = 20;

        private final Searcher<T> searcher;
        private final Label hint = new Label();
        private final VBox listBox = new VBox(10);
        private final Button moreBtn = new Button("加载更多");

        private String keyword;
        private int page = 0;
        private boolean finished = false;

        ResultList(String emptyText, Searcher<T> searcher) {
            this.searcher = searcher;
            setSpacing(10);
            setPadding(new Insets(4, 0, 0, 0));
            hint.setText(emptyText);
            hint.getStyleClass().add("muted");
            hint.setPadding(new Insets(30, 0, 0, 0));
            hint.setMaxWidth(Double.MAX_VALUE);
            hint.setAlignment(Pos.CENTER);

            moreBtn.setMaxWidth(Double.MAX_VALUE);
            moreBtn.getStyleClass().add("btn-secondary");
            moreBtn.setVisible(false);
            moreBtn.setOnAction(e -> search(keyword));

            getChildren().addAll(hint, listBox, moreBtn);
            VBox.setVgrow(listBox, Priority.ALWAYS);
        }

        void search(String kw) {
            // 换了关键字则重置分页
            if (!kw.equals(keyword)) {
                this.keyword = kw;
                this.page = 0;
                this.finished = false;
                listBox.getChildren().clear();
            }
            if (finished) {
                return;
            }
            moreBtn.setDisable(true);
            int nextPage = page + 1;
            UiUtil.async(() -> {
                Page<T> result = searcher.search(kw, nextPage, PAGE_SIZE);
                Platform.runLater(() -> {
                    page = nextPage;
                    if (result != null && result.getRecords() != null) {
                        for (Object item : result.getRecords()) {
                            if (item instanceof PublishedPost p) {
                                listBox.getChildren().add(new PostCard(router, p));
                            } else if (item instanceof Bar b) {
                                listBox.getChildren().add(new BarCard(router, b));
                            }
                        }
                    }
                    boolean noMore = result == null || result.getRecords() == null
                            || result.getRecords().size() < PAGE_SIZE;
                    if (noMore) {
                        finished = true;
                        moreBtn.setText("没有更多了");
                        moreBtn.setDisable(true);
                    } else {
                        moreBtn.setDisable(false);
                    }
                    moreBtn.setVisible(!listBox.getChildren().isEmpty());
                    hint.setText(listBox.getChildren().isEmpty() ? "没有找到相关结果" : "");
                    hint.setVisible(listBox.getChildren().isEmpty());
                });
            }, UiUtil::error);
        }
    }
}
