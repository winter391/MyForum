package org.example.clientpc.ui.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.clientpc.api.ImApi;
import org.example.clientpc.api.MessageApi;
import org.example.clientpc.model.PrivateMessage;
import org.example.clientpc.ui.Router;
import org.example.clientpc.util.UiUtil;

import java.util.Comparator;
import java.util.List;

/**
 * 与某人的聊天页：历史消息 + 实时收发（文字）。
 */
public class ChatView extends VBox {

    private final long peerId;
    private final String peerName;
    private final ImApi imApi;
    private final MessageApi messageApi;
    private final long myId;

    private final VBox messageBox = new VBox(8);
    private final ScrollPane scroll = new ScrollPane(messageBox);
    private final TextField input = new TextField();

    public ChatView(Router router, ImApi imApi, MessageApi messageApi,
                    long myId, long peerId, String peerName) {
        this.peerId = peerId;
        this.peerName = peerName;
        this.imApi = imApi;
        this.messageApi = messageApi;
        this.myId = myId;

        setSpacing(10);
        setPadding(new Insets(14));

        HBox head = new HBox(10);
        Button back = new Button("← 返回");
        back.getStyleClass().add("btn-secondary");
        back.setOnAction(e -> router.back());
        Label title = new Label(peerName);
        title.getStyleClass().add("h2");
        head.getChildren().addAll(back, title);

        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #f5f6f7; -fx-background-color: #f5f6f7;");
        messageBox.setPadding(new Insets(6));
        VBox.setVgrow(scroll, Priority.ALWAYS);

        input.getStyleClass().add("field");
        input.setPromptText("输入消息（双方互相关注后才能互发）");
        HBox.setHgrow(input, Priority.ALWAYS);
        input.setOnAction(e -> send());
        Button sendBtn = new Button("发送");
        sendBtn.getStyleClass().add("btn");
        sendBtn.setOnAction(e -> send());
        HBox inputRow = new HBox(8, input, sendBtn);

        getChildren().addAll(head, scroll, inputRow);

        loadHistory();
    }

    public long getPeerId() {
        return peerId;
    }

    private void loadHistory() {
        UiUtil.async(() -> {
            List<PrivateMessage> history = messageApi.getPrivateMessage(0, peerId);
            history.sort(Comparator.comparing(PrivateMessage::getChatId,
                    Comparator.nullsFirst(Comparator.naturalOrder())));
            UiUtil.fx(() -> {
                history.forEach(this::appendMessage);
                scrollBottom();
            });
        }, UiUtil::error);
    }

    private void send() {
        String text = input.getText().trim();
        if (text.isEmpty()) {
            return;
        }
        input.setDisable(true);
        UiUtil.async(() -> {
            imApi.sendMessage(peerId, text);
            UiUtil.fx(() -> {
                PrivateMessage pm = new PrivateMessage();
                pm.setSenderId(myId);
                pm.setReceiverId(peerId);
                pm.setMessage(text);
                pm.setType((short) 0);
                appendMessage(pm);
                input.clear();
                input.setDisable(false);
                scrollBottom();
            });
        }, msg -> {
            UiUtil.error(msg);
            Platform.runLater(() -> input.setDisable(false));
        });
    }

    /** ImClient 收到新消息时由主窗口调用（已在 FX 线程） */
    public void onIncoming(PrivateMessage pm) {
        appendMessage(pm);
        scrollBottom();
    }

    private void appendMessage(PrivateMessage pm) {
        boolean mine = pm.getSenderId() != null && pm.getSenderId() == myId;
        Label bubble = new Label(pm.getMessage() == null ? "" : pm.getMessage());
        bubble.getStyleClass().add(mine ? "bubble-me" : "bubble-other");
        bubble.setWrapText(true);
        bubble.setMaxWidth(420);

        HBox row = new HBox();
        row.setAlignment(mine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        row.getChildren().add(bubble);
        messageBox.getChildren().add(row);
    }

    private void scrollBottom() {
        scroll.layout();
        scroll.setVvalue(1.0);
    }
}
