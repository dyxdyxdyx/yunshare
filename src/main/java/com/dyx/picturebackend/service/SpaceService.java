package com.dyx.picturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dyx.picturebackend.model.dto.SpaceAddRequest;
import com.dyx.picturebackend.model.dto.SpaceQueryRequest;
import com.dyx.picturebackend.model.eneity.Space;
import com.dyx.picturebackend.model.eneity.User;
import com.dyx.picturebackend.model.vo.SpaceVO;
import com.baomidou.mybatisplus.extension.service.IService;


import javax.servlet.http.HttpServletRequest;

/**
* @author 杜雨轩
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2026-07-25 17:00:46
*/
public interface SpaceService extends IService<Space> {

    public long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    public void validSpace(Space space, boolean add);
    public void fillSpaceBySpaceLevel(Space space);


    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request);
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);
}
