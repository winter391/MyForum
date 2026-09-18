package org.example.clientpc.ui.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.clientpc.api.PostApi;
import org.example.clientpc.model.PostSave;
import org.example.clientpc.session.SessionContext;
import org.example.clientpc.util.UiUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 发帖窗口：标题（纯文字）+ 正文（文字与图片/动图，图片位置由其在正文中的先后决定，
 * 大小可在编辑器里拖动四角调整）。发布流程与后端一致：建草稿 → 传图 → 更新草稿 → 发布。
 */
public class PostEditorStage extends Stage {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final PostApi postApi;
    private final SessionContext session;
    private final long barId;
    private final String barName;

    private final TextField titleField = new TextField();
    private final WebEngine engine;
    private final Label status = new Label();
    private final Button publishBtn = new Button("发布");
    private final Button insertBtn = new Button("插入图片 / 动图");

    /** 待上传图片：pendingId -> 本地文件 */
    private final Map<String, File> pendingImages = new LinkedHashMap<>();
    private int pendingSeq = 0;

    public PostEditorStage(Stage owner, PostApi postApi, SessionContext session,
                           long barId, String barName, Runnable onPublished) {
        this.postApi = postApi;
        this.session = session;
        this.barId = barId;
        this.barName = barName;

        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);
        setTitle("在「" + barName + "」发帖");

        titleField.getStyleClass().add("field");
        titleField.setPromptText("标题（1-20 字，纯文字）");
        titleField.textProperty().addListener((o, n, v) -> {
            if (v.length() > 20) {
                titleField.setText(v.substring(0, 20));
            }
        });
        HBox titleRow = new HBox(8, new Label("标题："), titleField);
        HBox.setHgrow(titleField, Priority.ALWAYS);

        WebView webView = new WebView();
        VBox.setVgrow(webView, Priority.ALWAYS);
        engine = webView.getEngine();
        engine.load(PostEditorStage.class.getResource("/editor/editor.html").toExternalForm());
        webView.setContextMenuEnabled(false);

        insertBtn.getStyleClass().add("btn-secondary");
        insertBtn.setOnAction(e -> chooseImages());
        publishBtn.getStyleClass().add("btn");
        publishBtn.setOnAction(e -> publish(onPublished));
        status.getStyleClass().add("muted");

        HBox actions = new HBox(10, insertBtn, publishBtn, status);
        actions.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(status, Priority.ALWAYS);

        VBox root = new VBox(10, titleRow, webView, actions);
        root.setPadding(new Insets(14));
        root.setPrefSize(720, 640);

        setScene(new javafx.scene.Scene(root));
    }

    private void chooseImages() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("选择图片或动图");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "图片（jpg / png / gif / webp）", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.webp"));
        List<File> files = chooser.showOpenMultipleDialog(this);
        if (files == null || files.isEmpty()) {
            return;
        }
        for (File f : files) {
            String pendingId = "p" + (pendingSeq++);
            pendingImages.put(pendingId, f);
            try {
                String dataUrl = toDataUrl(f);
                engine.executeScript("editorAPI.insertImage(" + MAPPER.writeValueAsString(dataUrl)
                        + ", " + MAPPER.writeValueAsString(pendingId) + ")");
            } catch (Exception ex) {
                UiUtil.error("插入图片失败：" + f.getName());
            }
        }
    }

    private static String toDataUrl(File file) throws IOException {
        String name = file.getName().toLowerCase();
        String mime = name.endsWith(".png") ? "image/png"
                : name.endsWith(".gif") ? "image/gif"
                : name.endsWith(".webp") ? "image/webp"
                : "image/jpeg";
        byte[] bytes = Files.readAllBytes(file.toPath());
        return "data:" + mime + ";base64," + Base64.getEncoder().encodeToString(bytes);
    }

    private void publish(Runnable onPublished) {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            UiUtil.error("标题不能为空");
            return;
        }
        String blocksJson;
        try {
            blocksJson = (String) engine.executeScript("editorAPI.getBlocksJson()");
        } catch (Exception e) {
            UiUtil.error("读取正文失败");
            return;
        }
        List<PostSave.PostSaveBlock> blocks;
        try {
            blocks = MAPPER.readValue(blocksJson,
                    MAPPER.getTypeFactory().constructCollectionType(List.class,
                            PostSave.PostSaveBlock.class));
        } catch (Exception e) {
            UiUtil.error("解析正文失败");
            return;
        }
        if (blocks.isEmpty()) {
            UiUtil.error("正文不能为空");
            return;
        }

        publishBtn.setDisable(true);
        insertBtn.setDisable(true);
        UiUtil.async(() -> {
            // 1. 用纯文字内容建草稿（pending 图片此时尚无合法 URL，不能出现在草稿里）
            List<PostSave.PostSaveBlock> initial = new ArrayList<>();
            for (PostSave.PostSaveBlock b : blocks) {
                if (PostSave.PostSaveBlock.TYPE_TEXT.equals(b.getType())) {
                    initial.add(b);
                }
            }
            PostSave initialSave = new PostSave();
            initialSave.setTitle(title);
            initialSave.setBlocks(initial);
            Long draftId = postApi.createDraft(title,
                    MAPPER.writeValueAsString(initialSave), barName, barId);

            // 2. 逐张上传图片
            Map<String, String> uploaded = new LinkedHashMap<>();
            for (Map.Entry<String, File> entry : pendingImages.entrySet()) {
                UiUtil.fx(() -> status.setText("正在上传图片（"
                        + (uploaded.size() + 1) + "/" + pendingImages.size() + "）…"));
                String url = postApi.uploadImage(entry.getValue(), session.getUserId(), draftId);
                uploaded.put(entry.getKey(), url);
            }

            // 3. 回填 URL 并更新草稿
            List<PostSave.PostSaveBlock> finalBlocks = new ArrayList<>();
            for (PostSave.PostSaveBlock b : blocks) {
                if (PostSave.PostSaveBlock.TYPE_IMAGE.equals(b.getType())
                        && b.getContent() != null && b.getContent().startsWith("pending:")) {
                    String url = uploaded.get(b.getContent().substring("pending:".length()));
                    if (url == null) {
                        continue;
                    }
                    finalBlocks.add(PostSave.PostSaveBlock.image(url, b.getWidth()));
                } else {
                    finalBlocks.add(b);
                }
            }
            PostSave finalSave = new PostSave();
            finalSave.setTitle(title);
            finalSave.setBlocks(finalBlocks);
            postApi.updateDraft(draftId, title, MAPPER.writeValueAsString(finalSave),
                    barName, barId);

            // 4. 发布
            postApi.publish(draftId);
            UiUtil.fx(() -> {
                UiUtil.info("发布成功");
                close();
                if (onPublished != null) {
                    onPublished.run();
                }
            });
        }, msg -> {
            UiUtil.fx(() -> {
                status.setText("");
                publishBtn.setDisable(false);
                insertBtn.setDisable(false);
            });
            UiUtil.error("发布失败：" + msg);
        });
    }
}
