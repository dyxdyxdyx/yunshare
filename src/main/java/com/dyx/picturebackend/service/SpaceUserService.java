package com.dyx.picturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dyx.picturebackend.model.dto.SpaceUserAddRequest;
import com.dyx.picturebackend.model.dto.SpaceUserQueryRequest;
import com.dyx.picturebackend.model.eneity.SpaceUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dyx.picturebackend.model.vo.SpaceUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 杜雨轩
* @description 针对表【space_user(空间用户关联)】的数据库操作Service
* @createDate 2026-08-15 14:40:12
*/
public interface SpaceUserService extends IService<SpaceUser> {
    public long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);
    public void validSpaceUser(SpaceUser spaceUser, boolean add);
    public QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);
    public SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);
    public List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);
}
