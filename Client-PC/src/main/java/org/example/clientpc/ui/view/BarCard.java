package org.example.clientpc.ui.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.clientpc.model.Bar;
import org.example.clientpc.ui.Router;

/** 搜索结果里的吧卡片 */
public class BarCard extends VBox {

    public BarCard(Router router, Bar bar) {
        getStyleClass().add("card");
        setSpacing(6);
        setPadding(new Insets(12, 14, 12, 14));
        setOnMouseClicked(e -> router.openBar(bar.getId()));

        Label name = new Label(bar.getName());
        name.getStyleClass().add("h2");

        Label desc = new Label(bar.getDescription() == null || bar.getDescription().isBlank()
                ? "（暂无简介）" : bar.getDescription());
        desc.setWrapText(true);
        desc.setMaxWidth(Double.MAX_VALUE);
        desc.setStyle("-fx-text-fill: #41464c;");

        Label meta = new Label("吧主：" + nz(bar.getMasterNickname())
                + "  成员 " + nz(bar.getMemberCount())
                + "  帖子 " + nz(bar.getPostCount()));
        meta.getStyleClass().add("muted");

        HBox metaRow = new HBox(meta);
        HBox.setHgrow(meta, Priority.ALWAYS);

        getChildren().addAll(name, desc, metaRow);
    }

    private static int nz(Integer i) {
        return i == null ? 0 : i;
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }
}
