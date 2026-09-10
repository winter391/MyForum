package Util;

import Code.OssCode;
import org.springframework.stereotype.Component;

//这个工具类创建的目的，是为了在用户上传帖子草稿时，检验图片url是否正确，防止用户上传错误的url，以此来对其他用户的图片做出各种操作
//该工具类当前只支持jpg文件，如果是其他文件类型，不保证不出错
@Component
public class PostUtil
{
    public static boolean verifyPostPath(String url,Long userId)
    {
        Integer head_length =8+ OssCode.bucketName.length()+1+OssCode.endPoint.length()+1+OssCode.filePath.length()+6;
        Integer id_length = userId.toString().length()+1;
        Integer timeStampLength = 13;
        Integer tailLength = 4;
        if(head_length+id_length+timeStampLength+tailLength!=url.length()) return false;
        String head = url.substring(0,head_length);
        String id = url.substring(head_length,id_length+head_length);
        String timeStamp = url.substring(head_length+id_length,head_length+id_length+timeStampLength);
        String tail = url.substring(head_length+id_length+timeStampLength,head_length+id_length+timeStampLength+tailLength);

        String correct_head = "https://"+OssCode.bucketName+"."+OssCode.endPoint+"/"+OssCode.filePath+"/"+"Post"+"/";
        if(!head.equals(correct_head))
        {
            return false;
        }
        if(!id.equals(userId.toString()+"/"))
        {
            return false;
        }
        for(char num:timeStamp.toCharArray())
        {
            if(!Character.isDigit(num))
            {
                return false;
            }
        }
        if(!tail.equals(".jpg"))
        {
            return false;
        }
        return true;
    }
}
