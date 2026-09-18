package org.example.clientpc.ui;

/**
 * 页面导航接口，由主窗口实现。
 */
public interface Router {

    /** 主页（关注流/推荐流） */
    void showHome();

    /** 搜索页 */
    void showSearch();

    /** 消息页（会话/联系人列表） */
    void showMessages();

    /** 通知页 */
    void showNotifications();

    /** 帖子详情页 */
    void openPost(long postId);

    /** 吧主页 */
    void openBar(long barId);

    /** 用户主页 */
    void openProfile(long userId);

    /** 与某人的聊天页 */
    void openChat(long userId, String nickName);

    /** 返回上一页 */
    void back();
}
