package org.example.clientpc.ui;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.clientpc.session.SessionContext;
import org.example.clientpc.util.UiUtil;
import org.example.clientpc.ws.ImClient;
import org.springframework.stereotype.Component;

/**
 * 管理登录窗口与主窗口的切换。
 */
@Component
public class WindowManager {

    private final ViewFactory vf;
    private final ImClient imClient;
    private final NotificationCenter notifCenter;
    private final SessionContext session;

    public WindowManager(ViewFactory vf, ImClient imClient, NotificationCenter notifCenter,
                         SessionContext session) {
        this.vf = vf;
        this.imClient = imClient;
        this.notifCenter = notifCenter;
        this.session = session;
    }

    public void showLogin(Stage stage) {
        Scene scene = new Scene(vf.login(() -> showMain(stage)), 420, 520);
        applyCss(scene);
        stage.setTitle("MyForum - 登录");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void showMain(Stage stage) {
        // 建立 IM 长连接
        UiUtil.async(() -> imClient.connect(session.getAccessToken()), UiUtil::error);

        MainView main = new MainView(vf, imClient, notifCenter, session, () -> logout(stage));
        Scene scene = new Scene(main, 1100, 750);
        applyCss(scene);
        stage.setTitle("MyForum - " + session.getNickName());
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(620);
        stage.setResizable(true);
        stage.show();
    }

    private void logout(Stage stage) {
        imClient.close();
        notifCenter.clear();
        session.clear();
        showLogin(stage);
    }

    private void applyCss(Scene scene) {
        String css = WindowManager.class.getResource("/css/app.css").toExternalForm();
        if (!scene.getStylesheets().contains(css)) {
            scene.getStylesheets().add(css);
        }
    }
}
