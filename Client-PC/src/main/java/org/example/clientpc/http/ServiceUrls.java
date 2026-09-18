package org.example.clientpc.http;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 按模块拼出各微服务的 baseUrl。
 */
@Component
@Getter
public class ServiceUrls {

    @Value("${myforum.host}")
    private String host;

    @Value("${myforum.ports.im-platform}")
    private int imPlatformPort;

    @Value("${myforum.ports.im-server}")
    private int imServerPort;

    @Value("${myforum.ports.login-register}")
    private int loginRegisterPort;

    @Value("${myforum.ports.forum-bar}")
    private int forumBarPort;

    @Value("${myforum.ports.forum-comment}")
    private int forumCommentPort;

    @Value("${myforum.ports.forum-feed}")
    private int forumFeedPort;

    @Value("${myforum.ports.fourm-search}")
    private int fourmSearchPort;

    @Value("${myforum.ports.forum-post}")
    private int forumPostPort;

    @Value("${myforum.websocket-port}")
    private int websocketPort;

    private String base(int port) {
        return "http://" + host + ":" + port;
    }

    public String loginRegister() { return base(loginRegisterPort); }
    public String imPlatform()   { return base(imPlatformPort); }
    public String imServer()     { return base(imServerPort); }
    public String forumBar()     { return base(forumBarPort); }
    public String forumComment() { return base(forumCommentPort); }
    public String forumFeed()    { return base(forumFeedPort); }
    public String fourmSearch()  { return base(fourmSearchPort); }
    public String forumPost()    { return base(forumPostPort); }
    public String websocket()   { return "ws://" + host + ":" + websocketPort + "/ws"; }
}
