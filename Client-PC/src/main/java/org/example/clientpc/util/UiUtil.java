package org.example.clientpc.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.example.clientpc.http.ApiException;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/** UI 线程与对话框辅助 */
public final class UiUtil {

    /** 后台执行 API 调用等阻塞任务的线程池 */
    private static final ExecutorService POOL = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "client-worker");
        t.setDaemon(true);
        return t;
    });

    private UiUtil() {}

    /** 可抛异常的任务 */
    @FunctionalInterface
    public interface Task {
        void run() throws Exception;
    }

    /**
     * 在后台线程执行任务；异常信息（ApiException 取后端 msg）回到 FX 线程交给 onError。
     */
    public static void async(Task task, Consumer<String> onError) {
        POOL.submit(() -> {
            try {
                task.run();
            } catch (Exception e) {
                String msg = e instanceof ApiException ae ? ae.getMessage()
                        : (e.getMessage() != null ? e.getMessage() : e.toString());
                fx(() -> onError.accept(msg));
            }
        });
    }

    /** 在 UI 线程执行（若当前已在 UI 线程则直接执行） */
    public static void fx(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
    }

    public static void info(String message) {
        fx(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
            alert.setHeaderText(null);
            alert.showAndWait();
        });
    }

    public static void error(String message) {
        fx(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            alert.setHeaderText(null);
            alert.showAndWait();
        });
    }

    public static boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
        alert.setHeaderText(null);
        Optional<ButtonType> r = alert.showAndWait();
        return r.isPresent() && r.get() == ButtonType.OK;
    }
}
