package Code;

public class UserCode
{
    public static Integer banned = 1;

    public static Integer NotBanned = 0;

    //判断用户是否被封禁，User和UserSession的isBanned都是Short，不要直接用equals跨类型比较
    public static boolean isBanned(Short isBanned)
    {
        return isBanned != null && isBanned == banned.shortValue();
    }
}
