package org.example.clientpc.ui;

import org.example.clientpc.api.BarApi;
import org.example.clientpc.api.CommentApi;
import org.example.clientpc.api.FeedApi;
import org.example.clientpc.api.ImApi;
import org.example.clientpc.api.MessageApi;
import org.example.clientpc.api.PostApi;
import org.example.clientpc.api.SearchApi;
import org.example.clientpc.api.AuthService;
import org.example.clientpc.session.SessionContext;
import org.example.clientpc.ui.view.LoginView;
import org.springframework.stereotype.Component;

/**
 * 视图工厂：页面不是 Spring Bean（每次导航重建），由这里集中注入依赖。
 */
@Component
public class ViewFactory {

    final AuthService authService;
    final FeedApi feedApi;
    final PostApi postApi;
    final BarApi barApi;
    final CommentApi commentApi;
    final SearchApi searchApi;
    final ImApi imApi;
    final MessageApi messageApi;
    final SessionContext session;

    public ViewFactory(AuthService authService, FeedApi feedApi, PostApi postApi,
                       BarApi barApi, CommentApi commentApi, SearchApi searchApi,
                       ImApi imApi, MessageApi messageApi, SessionContext session) {
        this.authService = authService;
        this.feedApi = feedApi;
        this.postApi = postApi;
        this.barApi = barApi;
        this.commentApi = commentApi;
        this.searchApi = searchApi;
        this.imApi = imApi;
        this.messageApi = messageApi;
        this.session = session;
    }

    public LoginView login(Runnable onLoginSuccess) {
        return new LoginView(authService, onLoginSuccess);
    }
}
