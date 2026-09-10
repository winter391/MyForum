package Code;

import org.springframework.beans.factory.annotation.Value;

public class OssCode
{
    public static String endPoint;

    public static String bucketName;

    public static String accessKeyId;

    public static String accessKeySecret;

    public static String filePath;

    public static String region;

    public static Integer maxKeys;

    @Value("${oss.endPoint}")
    public void setEndPoint(String endPoint) {
        OssCode.endPoint = endPoint;
    }

    @Value("${oss.bucketName}")
    public void setBucketName(String bucketName) {
        OssCode.bucketName = bucketName;
    }

    @Value("${oss.accessKeyId}")
    public void setAccessKey(String accessKeyId) {
        OssCode.accessKeyId = accessKeyId;
    }

    @Value("${oss.filePath}")
    public void setFilePath(String filePath) {
        OssCode.filePath = filePath;
    }

    @Value("${oss.region}")
    public void setRegion(String region) {
        OssCode.region = region;
    }

    @Value("${oss.accessKeySecret}")
    public void setAccessKeySecret(String accessKeySecret) {
        OssCode.accessKeySecret = accessKeySecret;
    }

    @Value("${oss.maxKeys}")
    public void setMaxKeys(Integer maxKeys) {
        OssCode.maxKeys = maxKeys;
    }
}
