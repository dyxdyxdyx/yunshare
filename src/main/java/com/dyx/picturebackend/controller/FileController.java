package com.dyx.picturebackend.controller;

import com.dyx.picturebackend.annotation.AuthCheck;
import com.dyx.picturebackend.common.BaseResponse;
import com.dyx.picturebackend.common.ResultUtils;
import com.dyx.picturebackend.constant.UserConstant;
import com.dyx.picturebackend.exception.BusinessException;
import com.dyx.picturebackend.exception.ErrorCode;
import com.dyx.picturebackend.manger.CosManger;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private CosManger cosManger;
    @PostMapping("/test/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> testUploadFile(@RequestPart("file") MultipartFile multipartFile) {
        String filename = multipartFile.getOriginalFilename();
        String filepath = String.format("/test/%s",filename);
        File file=null;
        try {
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManger.putObject(filepath,file);
            return ResultUtils.success(filepath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if (file!=null){
                boolean delete = file.delete();
            }
        }
    }

    /**
     * 测试文件下载
     *
     * @param filepath 文件路径
     * @param response 响应对象
     */
    /**
     * 测试文件下载
     *
     * @param filepath 文件路径
     * @param response 响应对象
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/test/download/")
    public void testDownloadFile(String filepath, HttpServletResponse response) throws IOException {
        COSObjectInputStream cosObjectInput = null;
        try {
            COSObject cosObject = cosManger.getObject(filepath);
            cosObjectInput = cosObject.getObjectContent();
            // 处理下载到的流
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filepath);
            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }







}
