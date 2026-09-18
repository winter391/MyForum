package org.example.clientpc;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 客户端入口。本模块不是 Web 服务，Spring Boot 仅用于依赖注入与配置管理。
 * 真正的 JavaFX 生命周期由 {@link FxApp} 管理。
 */
@SpringBootApplication
public class ClientPcApplication {

    public static void main(String[] args) {
        Application.launch(FxApp.class, args);
    }

    static SpringApplication buildSpring() {
        SpringApplication app = new SpringApplication(ClientPcApplication.class);
        app.setWebApplicationType(org.springframework.boot.WebApplicationType.NONE);
        return app;
    }
}
