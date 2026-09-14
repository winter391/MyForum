package Util;

import Code.OssCode;
import org.springframework.stereotype.Component;

//这个工具类创建的目的，是为了在用户上传帖子草稿时，检验图片url是否正确，防止用户上传错误的url，以此来对其他用户的图片做出各种操作
//该工具类当前只支持jpg文件，如果是其他文件类型，不保证不出错
@Component
public class PostUtil
{
    public static boolean verifyPostPath(String url,Long userId,Long postId)
    {
        String correct_head = "https://"+OssCode.bucketName+"."+OssCode.endPoint+"/"+OssCode.filePath+"/"+"Post"+"/"+userId+"/"+postId+"/";
        return url.startsWith(correct_head)&&url.endsWith(".jpg");
    }
}
