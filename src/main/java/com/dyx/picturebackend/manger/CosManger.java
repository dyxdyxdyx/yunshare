package com.dyx.picturebackend.manger;

import cn.hutool.core.io.FileUtil;
import com.dyx.picturebackend.config.CosClientConfig;
import com.dyx.picturebackend.model.dto.UploadPictureResult;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

@Component
public class CosManger {
    @Resource
    private CosClientConfig cosClientConfig;
    @Resource
    private COSClient cosClient;

    public PutObjectResult putObject(String key, File file){
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        try {
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            return putObjectResult;
        } catch (CosServiceException cse) {
            cse.printStackTrace();
        } catch (CosClientException cce) {
            cce.printStackTrace();
        }
        return null;
    }


    /**
     * 下载对象
     *
     * @param key 唯一键
     */
    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }

    public PutObjectResult putPictureObject(String key, File file){
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        try {
            PicOperations picOperations = new PicOperations();
            picOperations.setIsPicInfo(1);
            putObjectRequest.setPicOperations(picOperations);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);



            return putObjectResult;
        } catch (CosServiceException cse) {
            cse.printStackTrace();
        } catch (CosClientException cce) {
            cce.printStackTrace();
        }
        return null;
    }

}
