package Netty;

import Entity.UserSession;
import io.netty.util.AttributeKey;

public class SignKey
{
    public static final AttributeKey<Boolean> REJECT = AttributeKey.valueOf("reject");

    public static final AttributeKey<UserSession> session = AttributeKey.valueOf("session");

    public static final AttributeKey<Boolean> verified = AttributeKey.valueOf("verified");

    public static final AttributeKey<Boolean> Logined = AttributeKey.valueOf("Logined");
}
