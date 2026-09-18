package org.example.clientpc;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import org.example.clientpc.ui.WindowManager;

/**
 * JavaFX 生命周期入口。Spring 容器在 init() 中启动，界面由 {@link WindowManager} 组织。
 */
public class FxApp extends javafx.application.Application {

    private ConfigurableApplicationContext spring;

    @Override
    public void init() {
        spring = ClientPcApplication.buildSpring().run();
    }

    @Override
    public void start(Stage primaryStage) {
        WindowManager windowManager = spring.getBean(WindowManager.class);
        windowManager.showLogin(primaryStage);
    }

    @Override
    public void stop() {
        spring.close();
        Platform.exit();
    }
}
