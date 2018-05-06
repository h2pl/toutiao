package com.nowcoder.service;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectResult;
import com.nowcoder.controller.NewsController;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by 周杰伦 on 2018/5/5.
 */
@Service
public class AliService {
    private static final Logger logger = LoggerFactory.getLogger(AliService.class);

    // endpoint以杭州为例，其它region请按实际情况填写
    public static String url = "https://lightnote.oss-cn-shanghai.aliyuncs.com/";
    public static String endpoint = "oss-cn-shanghai.aliyuncs.com";
    // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号
    public static String accessKeyId = "LTAIAel4wbQD6svJ";
    public static String accessKeySecret = "err8nTBbNGz0egoahI1PoNXQ4AX6kU";
    public static String bucketName = "lightnote";
    //上传文件
    public String saveImage(MultipartFile file) throws IOException {
        int dotPos = file.getOriginalFilename().lastIndexOf(".");
        if (dotPos < 0) {
            return null;
        }
        String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
        if (!ToutiaoUtil.isFileAllowed(fileExt)) {
            return null;
        }

        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
//        Files.copy(file.getInputStream(), new File(ToutiaoUtil.IMAGE_DIR + fileName).toPath(),
//                StandardCopyOption.REPLACE_EXISTING);
        String url = upload(fileName, file);
        return url;
    }
    private String upload(String key, MultipartFile file) {

// 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
// 上传文件
        PutObjectResult res;
        try {
            res = ossClient.putObject(bucketName, key, file.getInputStream());
            if (res == null)return null;
            System.out.println(res.getETag());
            return url + key;
        } catch (IOException e) {
            logger.error("上传失败" + e.getMessage());
        }
// 关闭client
        ossClient.shutdown();
        return null;
    }
}
