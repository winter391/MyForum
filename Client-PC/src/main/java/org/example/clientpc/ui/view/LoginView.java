package org.example.clientpc.ui.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.clientpc.api.AuthService;
import org.example.clientpc.util.UiUtil;

/**
 * 登录/注册页。登录成功后回调 onLoginSuccess。
 */
public class LoginView extends VBox {

    private final TabPane tabs = new TabPane();
    private final VBox registerBox;

    public LoginView(AuthService auth, Runnable onLoginSuccess) {
        setPrefSize(420, 520);
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(28, 32, 28, 32));
        setStyle("-fx-background-color: white;");

        Label title = new Label("MyForum");
        title.getStyleClass().add("h1");
        Label subtitle = new Label("PC 客户端");
        subtitle.getStyleClass().add("muted");
        VBox head = new VBox(2, title, subtitle);
        head.setPadding(new Insets(0, 0, 18, 0));

        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        registerBox = buildRegisterBox(auth);
        tabs.getTabs().addAll(
                new Tab("登录", buildLoginBox(auth, onLoginSuccess)),
                new Tab("注册", registerBox));
        VBox.setVgrow(tabs, Priority.ALWAYS);

        getChildren().addAll(head, tabs);
    }

    private VBox buildLoginBox(AuthService auth, Runnable onLoginSuccess) {
        TextField user = new TextField();
        user.getStyleClass().add("field");
        user.setPromptText("用户名（3-15 位）");

        PasswordField pass = new PasswordField();
        pass.getStyleClass().add("field");
        pass.setPromptText("密码（5-20 位）");

        Label error = new Label();
        error.setStyle("-fx-text-fill: #f54a45; -fx-font-size: 12;");

        Button btn = new Button("登 录");
        btn.getStyleClass().add("btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        // disable 不能用 bind：点击处理里还要手动 setDisable，绑定后 set 会抛异常导致按钮"无反应"
        final boolean[] busy = {false};
        Runnable refreshBtn = () -> btn.setDisable(busy[0]
                || user.getText().isEmpty() || pass.getText().isEmpty());
        user.textProperty().addListener((o, n, v) -> refreshBtn.run());
        pass.textProperty().addListener((o, n, v) -> refreshBtn.run());
        refreshBtn.run();

        btn.setOnAction(e -> {
            String name = user.getText().trim();
            String pwd = pass.getText();
            if (name.length() < 3 || name.length() > 15) {
                error.setText("用户名长度需在 3-15 位之间");
                return;
            }
            if (pwd.length() < 5 || pwd.length() > 20) {
                error.setText("密码长度需在 5-20 位之间");
                return;
            }
            error.setText("");
            busy[0] = true;
            refreshBtn.run();
            UiUtil.async(() -> {
                auth.login(name, pwd);
                UiUtil.fx(onLoginSuccess::run);
            }, msg -> {
                busy[0] = false;
                refreshBtn.run();
                error.setText(msg);
                UiUtil.error(msg);
            });
        });

        return new VBox(10, user, pass, error, btn);
    }

    private VBox buildRegisterBox(AuthService auth) {
        TextField user = new TextField();
        user.getStyleClass().add("field");
        user.setPromptText("用户名（3-15 位）");

        PasswordField pass = new PasswordField();
        pass.getStyleClass().add("field");
        pass.setPromptText("密码（5-20 位）");

        TextField nick = new TextField();
        nick.getStyleClass().add("field");
        nick.setPromptText("昵称（1-15 位）");

        Label error = new Label();
        error.setStyle("-fx-text-fill: #f54a45; -fx-font-size: 12;");

        Button btn = new Button("注 册");
        btn.getStyleClass().add("btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        final boolean[] busy = {false};
        Runnable refreshBtn = () -> btn.setDisable(busy[0] || user.getText().isEmpty()
                || pass.getText().isEmpty() || nick.getText().isEmpty());
        user.textProperty().addListener((o, n, v) -> refreshBtn.run());
        pass.textProperty().addListener((o, n, v) -> refreshBtn.run());
        nick.textProperty().addListener((o, n, v) -> refreshBtn.run());
        refreshBtn.run();

        btn.setOnAction(e -> {
            String name = user.getText().trim();
            String pwd = pass.getText();
            String nickname = nick.getText().trim();
            if (name.length() < 3 || name.length() > 15) {
                error.setText("用户名长度需在 3-15 位之间");
                return;
            }
            if (pwd.length() < 5 || pwd.length() > 20) {
                error.setText("密码长度需在 5-20 位之间");
                return;
            }
            if (nickname.length() < 1 || nickname.length() > 15) {
                error.setText("昵称长度需在 1-15 位之间");
                return;
            }
            error.setText("");
            busy[0] = true;
            refreshBtn.run();
            UiUtil.async(() -> {
                auth.register(name, pwd, nickname);
                UiUtil.fx(() -> {
                    busy[0] = false;
                    refreshBtn.run();
                    UiUtil.info("注册成功，请登录");
                    user.clear();
                    pass.clear();
                    nick.clear();
                    tabs.getSelectionModel().selectFirst();
                });
            }, msg -> {
                busy[0] = false;
                refreshBtn.run();
                error.setText(msg);
                UiUtil.error(msg);
            });
        });

        return new VBox(10, user, pass, nick, error, btn);
    }
}
