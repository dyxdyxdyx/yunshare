package com.dyx.picturebackend.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dyx.picturebackend.model.dto.PictureQueryRequest;
import com.dyx.picturebackend.model.dto.PictureUploadRequest;
import com.dyx.picturebackend.model.eneity.Picture;
import com.dyx.picturebackend.model.eneity.User;
import com.dyx.picturebackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author 杜雨轩
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2026-07-11 18:02:54
*/
public interface PictureService extends IService<Picture> {
    /**
     * 上传图片
     *
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request);
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);
    public void validPicture(Picture picture);
}
