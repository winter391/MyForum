package org.example.clientpc.ui.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;

/**
 * 展示帖子正文的 WebView（viewer.html）。
 * 高度随内容自动调整（图片异步加载会导致高度变化，因此做短暂轮询）。
 * WebView 是 final 类，这里用组合方式封装。
 */
public class ContentWebView {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final WebView view = new WebView();
    private final WebEngine engine;
    private String pendingHtml;
    private boolean loaded = false;
    private Timeline heightWatcher;

    public ContentWebView() {
        view.setPrefHeight(200);
        view.setContextMenuEnabled(false);
        engine = view.getEngine();
        engine.getLoadWorker().stateProperty().addListener((obs, old, state) -> {
            if (state == javafx.concurrent.Worker.State.SUCCEEDED) {
                loaded = true;
                if (pendingHtml != null) {
                    String html = pendingHtml;
                    pendingHtml = null;
                    applyContent(html);
                }
            }
        });
        engine.load(ContentWebView.class.getResource("/editor/viewer.html").toExternalForm());
    }

    public Node node() {
        return view;
    }

    /** 设置正文 HTML（由 BlocksHtml.toHtml 生成） */
    public void setContentHtml(String html) {
        if (loaded) {
            applyContent(html);
        } else {
            pendingHtml = html;
        }
    }

    private void applyContent(String html) {
        try {
            engine.executeScript("viewerAPI.setContent(" + MAPPER.writeValueAsString(html) + ")");
        } catch (Exception ignored) {
        }
        watchHeight();
    }

    /** 轮询内容高度并调整 WebView 高度 */
    private void watchHeight() {
        if (heightWatcher != null) {
            heightWatcher.stop();
        }
        heightWatcher = new Timeline(new KeyFrame(Duration.millis(400), ev -> {
            try {
                Object h = engine.executeScript("document.body.scrollHeight");
                if (h instanceof Number n) {
                    view.setPrefHeight(Math.max(120, n.doubleValue() + 16));
                }
            } catch (Exception ignored) {
            }
        }));
        heightWatcher.setCycleCount(15);
        heightWatcher.play();
    }
}
