package org.example.clientpc.ui.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.clientpc.api.BarApi;
import org.example.clientpc.api.SearchApi;
import org.example.clientpc.model.BarMember;
import org.example.clientpc.util.UiUtil;
import org.springframework.context.ApplicationContext;

/**
 * 成员管理（吧主/管理员用）：查看成员身份与禁言状态，禁言/解除禁言（管理员及以上），
 * 设置/撤销管理员（仅吧主）。
 */
public class MemberManageDialog extends Stage {

    private final BarApi barApi;
    private final SearchApi searchApi;
    private final long barId;
    private final int myIdentity;
    private final Runnable onChange;
    private final VBox memberBox = new VBox(8);

    public MemberManageDialog(Stage owner, BarApi barApi, SearchApi searchApi,
                              long barId, int myIdentity, Runnable onChange) {
        this.barApi = barApi;
        this.searchApi = searchApi;
        this.barId = barId;
        this.myIdentity = myIdentity;
        this.onChange = onChange;

        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);
        setTitle("成员管理");

        Label hint = new Label("身份：吧主可管理管理员与成员，管理员可管理成员（禁言）");
        hint.getStyleClass().add("muted");

        ScrollPane scroll = new ScrollPane(memberBox);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(420);
        scroll.setStyle("-fx-background: #f5f6f7; -fx-background-color: #f5f6f7;");

        Button close = new Button("关闭");
        close.getStyleClass().add("btn-secondary");
        close.setOnAction(e -> close());

        VBox root = new VBox(10, hint, scroll, close);
        root.setPadding(new Insets(14));
        root.setPrefSize(560, 560);
        setScene(new javafx.scene.Scene(root));

        load();
    }

    private void load() {
        memberBox.getChildren().clear();
        UiUtil.async(() -> {
            java.util.List<BarMember> members = barApi.getBarMembers(barId);
            UiUtil.fx(() -> members.forEach(m -> memberBox.getChildren().add(new Row(m))));
        }, UiUtil::error);
    }

    /** 一行成员信息 + 操作按钮 */
    private class Row extends HBox {

        Row(BarMember m) {            setSpacing(10);
            setPadding(new Insets(8, 10, 8, 10));
            setStyle("-fx-background-color: white; -fx-background-radius: 8;");

            // 昵称需要经搜索模块解析
            Label nick = new Label("用户 " + m.getUserId());
            UiUtil.async(() -> {
                var user = searchApi.searchUserById(m.getUserId());
                UiUtil.fx(() -> nick.setText(user == null ? "用户 " + m.getUserId()
                        : user.getNickName()));
            }, msg -> { });

            String identityText = m.getIdentity() == null ? "成员"
                    : switch (m.getIdentity()) {
                        case BarApi.IDENTITY_MASTER -> "吧主";
                        case BarApi.IDENTITY_ADMIN -> "管理员";
                        default -> "成员";
                    };
            Label identity = new Label(identityText);
            identity.setStyle("-fx-padding: 1 8; -fx-background-radius: 4; -fx-font-size: 11;"
                    + (BarApi.IDENTITY_MASTER == m.getIdentity()
                        ? "-fx-background-color: #fff3e0; -fx-text-fill: #e65100;"
                        : BarApi.IDENTITY_ADMIN == m.getIdentity()
                            ? "-fx-background-color: #e8f0ff; -fx-text-fill: #3370ff;"
                            : "-fx-background-color: #f0f2f5; -fx-text-fill: #41464c;"));

            boolean muted = m.getIsBanned() != null && m.getIsBanned() == 1;
            Label muteState = new Label(muted ? "已禁言" : "");
            muteState.setStyle("-fx-text-fill: #f54a45; -fx-font-size: 12;");

            VBox info = new VBox(4, new HBox(8, nick, identity, muteState));
            HBox.setHgrow(info, Priority.ALWAYS);

            getChildren().add(info);

            int targetIdentity = m.getIdentity() == null ? 0 : m.getIdentity();
            // 禁言/解除禁言：管理员及以上，且目标身份低于自己
            if (myIdentity >= BarApi.IDENTITY_ADMIN && targetIdentity < myIdentity) {
                Button muteBtn = new Button(muted ? "解除禁言" : "禁言");
                muteBtn.getStyleClass().add(muted ? "btn" : "btn-danger");
                muteBtn.setOnAction(e -> {
                    muteBtn.setDisable(true);
                    UiUtil.async(() -> {
                        if (muted) {
                            barApi.unmuteMember(barId, m.getUserId());
                        } else {
                            barApi.muteMember(barId, m.getUserId());
                        }
                        UiUtil.fx(() -> {
                            onChange.run();
                            load();
                        });
                    }, msg -> {
                        UiUtil.error(msg);
                        UiUtil.fx(() -> muteBtn.setDisable(false));
                    });
                });
                getChildren().add(muteBtn);
            }
            // 管理员任免：仅吧主
            if (myIdentity == BarApi.IDENTITY_MASTER) {
                if (targetIdentity == BarApi.IDENTITY_MEMBER) {
                    Button setBtn = new Button("设为管理员");
                    setBtn.getStyleClass().add("btn-secondary");
                    setBtn.setOnAction(e -> act(() -> barApi.setAdmin(barId, m.getUserId())));
                    getChildren().add(setBtn);
                } else if (targetIdentity == BarApi.IDENTITY_ADMIN) {
                    Button removeBtn = new Button("撤销管理员");
                    removeBtn.getStyleClass().add("btn-secondary");
                    removeBtn.setOnAction(e -> act(() -> barApi.removeAdmin(barId, m.getUserId())));
                    getChildren().add(removeBtn);
                }
            }
        }

        private void act(UiUtil.Task task) {
            UiUtil.async(() -> {
                task.run();
                UiUtil.fx(() -> {
                    onChange.run();
                    load();
                });
            }, UiUtil::error);
        }
    }
}
