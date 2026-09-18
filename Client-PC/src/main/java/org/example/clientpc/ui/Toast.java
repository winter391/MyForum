package org.example.clientpc.ui;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.clientpc.util.UiUtil;

/**
 * 右下角轻提示（自动消失）。
 */
public final class Toast {

    private Toast() {}

    public static void show(String text) {
        UiUtil.fx(() -> {
            Label label = new Label(text);
            label.setStyle("-fx-background-color: rgba(30,32,36,0.85); -fx-text-fill: white;"
                    + "-fx-padding: 10 16; -fx-background-radius: 8; -fx-font-size: 13;"
                    + "-fx-max-width: 320; -fx-wrap-text: true;");
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.setAlwaysOnTop(true);
            Scene scene = new Scene(label);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            Rectangle2D screen = Screen.getPrimary().getVisualBounds();
            stage.show();
            // 右下角定位
            stage.setX(screen.getMaxX() - stage.getWidth() - 24);
            stage.setY(screen.getMaxY() - stage.getHeight() - 40);
            new Thread(() -> {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException ignored) {
                }
                UiUtil.fx(stage::hide);
            }, "toast").start();
        });
    }
}
