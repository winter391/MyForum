package org.example.clientpc.ui.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.clientpc.api.BarApi;
import org.example.clientpc.api.PostApi;
import org.example.clientpc.api.SearchApi;
import org.example.clientpc.model.Bar;
import org.example.clientpc.model.BarMember;
import org.example.clientpc.model.Page;
import org.example.clientpc.model.PublishedPost;
import org.example.clientpc.session.SessionContext;
import org.example.clientpc.ui.Router;
import org.example.clientpc.util.UiUtil;

/**
 * 吧主页：吧信息、加入/退出、发帖、成员管理（吧主/管理员）、发帖权限设置（吧主）、
 * 吧内帖子列表（置顶帖排最前，管理员及以上可置顶/取消置顶）。
 */
public class BarView extends VBox {

    private static final int PAGE_SIZE = 20;

    private final Router router;
    private final BarApi barApi;
    private final PostApi postApi;
    private final SearchApi searchApi;
    private final SessionContext session;
    private final long barId;

    private final Label nameLabel = new Label();
    private final Label descLabel = new Label();
    private final Label metaLabel = new Label();
    private final VBox infoBox = new VBox(6);
    private final HBox actionBar = new HBox(10);
    private final VBox postBox = new VBox(10);
    private final Button moreBtn = new Button("加载更多");

    private Bar bar;
    private BarMember myMember;
    private int page = 0;
    private boolean finished = false;

    public BarView(Router router, BarApi barApi, PostApi postApi, SearchApi searchApi,
                   SessionContext session, long barId) {
        this.router = router;
        this.barApi = barApi;
        this.postApi = postApi;
        this.searchApi = searchApi;
        this.session = session;
        this.barId = barId;

        setSpacing(10);
        setPadding(new Insets(14));

        Button back = new Button("← 返回");
        back.getStyleClass().add("btn-secondary");
        back.setOnAction(e -> router.back());

        infoBox.getStyleClass().add("card");
        infoBox.setPadding(new Insets(14));
        nameLabel.getStyleClass().add("h1");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(Double.MAX_VALUE);
        descLabel.setStyle("-fx-text-fill: #41464c;");
        metaLabel.getStyleClass().add("muted");
        infoBox.getChildren().addAll(nameLabel, descLabel, metaLabel, actionBar);

        Label postsTitle = new Label("帖子");
        postsTitle.getStyleClass().add("h2");
        moreBtn.setMaxWidth(Double.MAX_VALUE);
        moreBtn.getStyleClass().add("btn-secondary");
        moreBtn.setOnAction(e -> loadPosts(false));

        VBox content = new VBox(10, postsTitle, postBox, moreBtn);
        VBox.setVgrow(postBox, Priority.ALWAYS);
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #f5f6f7; -fx-background-color: #f5f6f7;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        getChildren().addAll(back, infoBox, scroll);

        load();
    }

    private void load() {
        UiUtil.async(() -> {
            Bar b = barApi.getBar(barId);
            java.util.List<BarMember> members = barApi.getBarMembers(barId);
            UiUtil.fx(() -> {
                bar = b;
                myMember = members.stream()
                        .filter(m -> session.getUserId().equals(m.getUserId()))
                        .findFirst().orElse(null);
                renderInfo();
                reloadPosts();
            });
        }, UiUtil::error);
    }

    private void renderInfo() {
        nameLabel.setText(bar.getName());
        descLabel.setText(bar.getDescription() == null || bar.getDescription().isBlank()
                ? "（暂无简介）" : bar.getDescription());
        metaLabel.setText("吧主：" + nz(bar.getMasterNickname())
                + "  成员 " + nz(bar.getMemberCount())
                + "  帖子 " + nz(bar.getPostCount())
                + "  发帖权限：" + (bar.getPostPermission() != null && bar.getPostPermission() == 1
                        ? "仅成员可发帖" : "所有人可发帖"));

        actionBar.getChildren().clear();
        actionBar.setAlignment(Pos.CENTER_LEFT);

        int myIdentity = myMember == null ? -1
                : (myMember.getIdentity() == null ? -1 : myMember.getIdentity());

        // 加入 / 退出
        Button memberBtn = new Button(myMember == null ? "加入本吧" : "退出本吧");
        memberBtn.getStyleClass().add(myMember == null ? "btn" : "btn-secondary");
        memberBtn.setOnAction(e -> {
            if (myMember == null) {
                UiUtil.async(() -> {
                    barApi.joinBar(barId);
                    UiUtil.fx(this::load);
                }, UiUtil::error);
            } else {
                UiUtil.async(() -> {
                    barApi.leaveBar(barId);
                    UiUtil.fx(this::load);
                }, UiUtil::error);
            }
        });
        actionBar.getChildren().add(memberBtn);

        // 发帖（后端会校验禁言与发帖权限）
        Button postBtn = new Button("发帖");
        postBtn.getStyleClass().add("btn");
        postBtn.setOnAction(e -> {
            Stage owner = (Stage) getScene().getWindow();
            new PostEditorStage(owner, postApi, session, barId, bar.getName(), this::reloadPosts)
                    .showAndWait();
        });
        actionBar.getChildren().add(postBtn);

        // 成员管理（管理员及以上）
        if (myIdentity >= BarApi.IDENTITY_ADMIN) {
            Button manageBtn = new Button("成员管理");
            manageBtn.getStyleClass().add("btn-secondary");
            manageBtn.setOnAction(e -> {
                Stage owner = (Stage) getScene().getWindow();
                new MemberManageDialog(owner, barApi, searchApi, barId, myIdentity, this::load)
                        .showAndWait();
            });
            actionBar.getChildren().add(manageBtn);
        }

        // 发帖权限设置（仅吧主）
        if (myIdentity == BarApi.IDENTITY_MASTER) {
            ComboBox<String> permBox = new ComboBox<>();
            permBox.getItems().addAll("所有人可发帖", "仅成员可发帖");
            permBox.getSelectionModel().select(
                    bar.getPostPermission() != null && bar.getPostPermission() == 1 ? 1 : 0);
            Button permBtn = new Button("应用");
            permBtn.getStyleClass().add("btn-secondary");
            permBtn.setOnAction(e -> {
                int perm = permBox.getSelectionModel().getSelectedIndex() == 1 ? 1 : 0;
                UiUtil.async(() -> {
                    barApi.setPostPermission(barId, perm);
                    UiUtil.fx(() -> {
                        UiUtil.info("发帖权限已更新");
                        load();
                    });
                }, UiUtil::error);
            });
            actionBar.getChildren().addAll(new Label("发帖权限："), permBox, permBtn);
        }
    }

    private void reloadPosts() {
        page = 0;
        postBox.getChildren().clear();
        finished = false;
        moreBtn.setText("加载更多");
        loadPosts(true);
    }

    private void loadPosts(boolean reset) {
        if (finished && !reset) {
            return;
        }
        int nextPage = page + 1;
        UiUtil.async(() -> {
            Page<PublishedPost> result = postApi.getPublishedPosts(barId,
                    PostApi.SORT_TIME, nextPage, PAGE_SIZE);
            UiUtil.fx(() -> {
                page = nextPage;
                if (result != null && result.getRecords() != null) {
                    result.getRecords().forEach(p -> postBox.getChildren().add(postRow(p)));
                }
                boolean noMore = result == null || result.getRecords() == null
                        || result.getRecords().size() < PAGE_SIZE;
                if (noMore) {
                    finished = true;
                    moreBtn.setText(postBox.getChildren().isEmpty() ? "暂无帖子" : "没有更多了");
                    moreBtn.setDisable(true);
                } else {
                    moreBtn.setDisable(false);
                }
            });
        }, UiUtil::error);
    }

    /** 帖子卡片；管理员及以上附带置顶/取消置顶按钮 */
    private HBox postRow(PublishedPost p) {
        PostCard card = new PostCard(router, p);
        HBox.setHgrow(card, Priority.ALWAYS);
        HBox row = new HBox(10, card);
        row.setSpacing(10);

        int myIdentity = myMember == null || myMember.getIdentity() == null
                ? -1 : myMember.getIdentity();
        boolean pinned = p.getPin() != null && p.getPin() == 1;
        if (myIdentity >= BarApi.IDENTITY_ADMIN) {
            Button pinBtn = new Button(pinned ? "取消置顶" : "置顶");
            pinBtn.getStyleClass().add("btn-secondary");
            pinBtn.setOnAction(e -> {
                pinBtn.setDisable(true);
                UiUtil.async(() -> {
                    barApi.setPostPin(barId, p.getId(), pinned ? 0 : 1);
                    UiUtil.fx(this::reloadPosts);
                }, msg -> {
                    UiUtil.error(msg);
                    UiUtil.fx(() -> pinBtn.setDisable(false));
                });
            });
            row.getChildren().add(pinBtn);
        }
        return row;
    }

    private static int nz(Integer i) {
        return i == null ? 0 : i;
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }
}
