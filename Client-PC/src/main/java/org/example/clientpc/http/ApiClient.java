package org.example.clientpc.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.example.clientpc.model.Result;
import org.example.clientpc.session.SessionContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 统一的 HTTP 访问入口：自动附带 accessToken 头、解析统一返回体 Result，
 * 在收到"token失效"时自动用 refreshToken 换新令牌并重试一次。
 */
@Slf4j
@Component
public class ApiClient {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient http = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    private final ObjectMapper mapper;
    private final ServiceUrls urls;
    private final SessionContext session;

    public ApiClient(ObjectMapper mapper, ServiceUrls urls, SessionContext session) {
        this.mapper = mapper;
        this.urls = urls;
        this.session = session;
    }

    /** POST JSON，返回 Result.data */
    public <T> T postForData(String url, Object body, TypeReference<Result<T>> type) {
        Result<T> result = withTokenRetry(() -> doJsonPost(url, body, type));
        check(result);
        return result.getData();
    }

    /** POST JSON，只关心成败 */
    public void post(String url, Object body) {
        Result<Object> result = withTokenRetry(
                () -> doJsonPost(url, body, new TypeReference<Result<Object>>() {}));
        check(result);
    }

    /** GET + query 参数 */
    public <T> T getForData(String url, Map<String, String> query, TypeReference<Result<T>> type) {
        HttpUrl.Builder ub = HttpUrl.parse(url).newBuilder();
        if (query != null) {
            query.forEach(ub::addQueryParameter);
        }
        Request.Builder rb = new Request.Builder().url(ub.build()).get();
        if (session.isLoggedIn()) {
            rb.header("accessToken", session.getAccessToken());
        }
        Result<T> result = withTokenRetry(() -> execute(rb.build(), type));
        check(result);
        return result.getData();
    }

    /** multipart 文件上传（文件 part 名固定为 request），返回 data 字符串（图片 URL） */
    public String postMultipartImage(String url, File file, Map<String, String> fields) {
        MultipartBody.Builder mb = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("request", file.getName(),
                        RequestBody.create(file, MediaType.parse("application/octet-stream")));
        if (fields != null) {
            fields.forEach(mb::addFormDataPart);
        }
        Request.Builder rb = new Request.Builder().url(url).post(mb.build());
        if (session.isLoggedIn()) {
            rb.header("accessToken", session.getAccessToken());
        }
        Result<String> result = withTokenRetry(() -> execute(rb.build(),
                new TypeReference<Result<String>>() {}));
        check(result);
        return result.getData();
    }

    // ------------------------------------------------------------------

    private <T> Result<T> doJsonPost(String url, Object body, TypeReference<Result<T>> type) {
        try {
            String json = body == null ? "" : mapper.writeValueAsString(body);
            Request.Builder rb = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(json, JSON));
            if (session.isLoggedIn()) {
                rb.header("accessToken", session.getAccessToken());
            }
            return execute(rb.build(), type);
        } catch (IOException e) {
            throw new ApiException(-1, "无法连接服务器：" + e.getMessage());
        }
    }

    private <T> Result<T> execute(Request request, TypeReference<Result<T>> type) {
        try (Response resp = http.newCall(request).execute()) {
            String text = resp.body().string();
            return mapper.readValue(text, type);
        } catch (IOException e) {
            log.warn("请求失败 url={} : {}", request.url(), e.toString());
            throw new ApiException(-1, "无法连接服务器：" + e.getMessage());
        }
    }

    /**
     * 执行一次调用；若返回"token失效"则刷新令牌后重试一次。
     * call 本身不抛 IOException（内部已包装为 ApiException）。
     */
    private <T> Result<T> withTokenRetry(Supplier<Result<T>> call) {
        Result<T> result = call.get();
        if (result != null && !result.isSuccess()
                && result.getMsg() != null && result.getMsg().contains("token失效")
                && session.isLoggedIn() && session.getRefreshToken() != null) {
            refreshToken();
            result = call.get();
        }
        return result;
    }

    private void check(Result<?> result) {
        if (result == null || !result.isSuccess()) {
            throw new ApiException(
                    result == null || result.getCode() == null ? -1 : result.getCode(),
                    result == null || result.getMsg() == null ? "未知错误" : result.getMsg());
        }
    }

    /** 用 refreshToken 换新的 accessToken（Login-Register，无需 token） */
    private void refreshToken() {
        try {
            RequestBody rb = RequestBody.create(
                    mapper.writeValueAsString(Map.of("refreshToken", session.getRefreshToken())), JSON);
            Request request = new Request.Builder()
                    .url(urls.loginRegister() + "/MyForum/refreshToken")
                    .post(rb)
                    .build();
            try (Response resp = http.newCall(request).execute()) {
                JsonNode node = mapper.readTree(resp.body().string());
                JsonNode data = node.get("data");
                if (node.path("code").asInt() == Result.SUCCESS
                        && data != null && data.hasNonNull("accessToken")) {
                    session.setAccessToken(data.get("accessToken").asText());
                    return;
                }
                throw new ApiException(Result.FAILED, node.path("msg").asText("登录已过期，请重新登录"));
            }
        } catch (IOException e) {
            throw new ApiException(-1, "刷新登录状态失败：" + e.getMessage());
        }
    }
}
