package com.dyx.picturebackend.manger;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.dyx.picturebackend.config.CosClientConfig;
import com.dyx.picturebackend.exception.BusinessException;
import com.dyx.picturebackend.exception.ErrorCode;
import com.dyx.picturebackend.model.dto.UploadPictureResult;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
public class FileManager {
    @Resource
    private CosClientConfig cosClientConfig;
    @Resource
    private COSClient cosClient;
    @Resource
    private CosManger cosManger;



    public UploadPictureResult uploadPictureResult(MultipartFile multipartFile,String uploadPathPrefix) {
        //校验图片
        validPicture(multipartFile);
        //图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originalFilename = multipartFile.getOriginalFilename();
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);
        File file=null;

        try {
            file= File.createTempFile(uploadPath, null);
            multipartFile.transferTo(file);
            PutObjectResult putObjectResult = cosManger.putPictureObject(uploadPath, file);

            //封装返回结果
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
            uploadPictureResult.setPicWidth(imageInfo.getWidth());
            uploadPictureResult.setPicHeight(imageInfo.getHeight());
            uploadPictureResult.setPicScale(NumberUtil.round(imageInfo.getWidth()*1.0 / imageInfo.getHeight(),2).doubleValue());
            uploadPictureResult.setPicFormat(imageInfo.getFormat());
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setUrl(cosClientConfig.getHost()+"/"+uploadPath);
            return uploadPictureResult;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            this.deleteTempFile(file);
        }


    }

    private void deleteTempFile(File file) {
        if (file==null)
            return;
        file.delete();
    }


    public void validPicture(MultipartFile multipartFile) {
        if (multipartFile==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"文件不能为空");
        }
        long fileSize = multipartFile.getSize();
        final long ONE_M=1024*1024L;
        if (fileSize>2*ONE_M) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"文件大小不能超过2M");
        }
        String filename = multipartFile.getOriginalFilename();
        if (!filename.contains(".")){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"文件格式错误");
        }
        int i = filename.lastIndexOf(".");
        String substring = FileUtil.getSuffix(filename);
        String suffix = FileUtil.getSuffix(filename);
        final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "jpg", "png", "webp");
        if (!ALLOW_FORMAT_LIST.contains(substring)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"文件格式错误");
        }
    }




}
