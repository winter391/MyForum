package V0;

import lombok.Data;

//用户搜索结果的返回值，只包含展示需要的字段，不包含密码等敏感信息
@Data
public class UserV0
{
    private Long id;

    private String nickName;

    private String headImage;

    private String headImageThumb;
}
