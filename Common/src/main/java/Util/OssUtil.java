package Util;


import Code.OssCode;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.ListObjectsV2Request;
import com.aliyun.oss.model.ListObjectsV2Result;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Set;




@Component
@Slf4j
public class OssUtil
{
    public static String upLoadFile(InputStream file,String fileName,String filePath)
    {
        ClientBuilderConfiguration client = new ClientBuilderConfiguration();
        client.setSignatureVersion(SignVersion.V4);
        OSS ossClient = OSSClientBuilder.create()
                .endpoint(OssCode.endPoint)
                .credentialsProvider(new DefaultCredentialProvider(OssCode.accessKeyId,OssCode.accessKeySecret))
                .clientConfiguration(client)
                .region(OssCode.region)
                .build();
        try
        {
            String res_filePath = String.join("/",OssCode.filePath,filePath,fileName);
            PutObjectRequest request = new PutObjectRequest(OssCode.bucketName,res_filePath,file);
            ossClient.putObject(request);
            return "https://"+ OssCode.bucketName+"."+OssCode.endPoint+"/"+res_filePath;
        }
        catch (Exception e)
        {
            log.error("上传文件时错误：{}",e.toString());
            return null;
        }
        finally
        {
            ossClient.shutdown();
        }
    }



    public static void ForEachDelete(Set<String> set,String directoryPath)
    {
        ClientBuilderConfiguration client = new ClientBuilderConfiguration();
        client.setSignatureVersion(SignVersion.V4);
        OSS ossClient = OSSClientBuilder.create()
                .endpoint(OssCode.endPoint)
                .credentialsProvider(new DefaultCredentialProvider(OssCode.accessKeyId,OssCode.accessKeySecret))
                .clientConfiguration(client)
                .region(OssCode.region)
                .build();
        try
        {
            String nextContinueToken = null;
            ListObjectsV2Result result =null;

            do {
                ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request(OssCode.bucketName).withMaxKeys(OssCode.maxKeys);
                listObjectsV2Request.setContinuationToken(nextContinueToken);
                listObjectsV2Request.setPrefix(OssCode.filePath+"/"+directoryPath);
                result = ossClient.listObjectsV2(listObjectsV2Request);

                List<OSSObjectSummary> sums = result.getObjectSummaries();
                for (OSSObjectSummary s : sums) {
                    String url = "https://" + OssCode.bucketName + "." + OssCode.endPoint + "/" + s.getKey();
                    if(!set.contains(url))
                    {
                        ossClient.deleteObject(OssCode.bucketName,s.getKey());
                    }
                }

                nextContinueToken = result.getNextContinuationToken();

            } while (result.isTruncated());
        } catch (Exception e) {
            log.error("删除文件时错误：{}",e.toString());
        }
        finally
        {
            ossClient.shutdown();
        }
    }
}
