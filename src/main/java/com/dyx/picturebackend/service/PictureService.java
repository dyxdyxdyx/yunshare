package com.dyx.picturebackend.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dyx.picturebackend.exception.BusinessException;
import com.dyx.picturebackend.exception.ErrorCode;
import com.dyx.picturebackend.model.dto.*;
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
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(Object inputSource,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request);
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);
    public void validPicture(Picture picture);
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);
    public void fillReviewParams(Picture picture, User loginUser);
    public void clearPictureFile(Picture oldPicture);
    public void checkPictureAuth(User loginUser, Picture picture);
    public void deletePicture(long pictureId, User loginUser);
    /**
     * 批量抓取和创建图片
     *
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return 成功创建的图片数
     */
    Integer uploadPictureByBatch(
            PictureUploadByBatchRequest pictureUploadByBatchRequest,
            User loginUser
    );
    public void editPicture(PictureEditRequest pictureEditRequest, User loginUser);

}
