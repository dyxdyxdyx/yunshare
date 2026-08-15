package com.dyx.picturebackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dyx.picturebackend.exception.BusinessException;
import com.dyx.picturebackend.exception.ErrorCode;
import com.dyx.picturebackend.exception.ThrowUtils;
import com.dyx.picturebackend.manger.CosManger;
import com.dyx.picturebackend.manger.FileManager;
import com.dyx.picturebackend.manger.sharding.DynamicShardingManager;
import com.dyx.picturebackend.manger.upload.FilePictureUpload;
import com.dyx.picturebackend.manger.upload.UrlPictureUpload;
import com.dyx.picturebackend.model.dto.SpaceAddRequest;
import com.dyx.picturebackend.model.dto.SpaceQueryRequest;
import com.dyx.picturebackend.model.eneity.Picture;
import com.dyx.picturebackend.model.eneity.Space;
import com.dyx.picturebackend.model.eneity.SpaceUser;
import com.dyx.picturebackend.model.eneity.User;
import com.dyx.picturebackend.model.enums.SpaceLevelEnum;
import com.dyx.picturebackend.model.enums.SpaceRoleEnum;
import com.dyx.picturebackend.model.enums.SpaceTypeEnum;
import com.dyx.picturebackend.model.vo.PictureVO;
import com.dyx.picturebackend.model.vo.SpaceVO;
import com.dyx.picturebackend.model.vo.UserVO;
import com.dyx.picturebackend.service.SpaceService;
import com.dyx.picturebackend.mapper.SpaceMapper;
import com.dyx.picturebackend.service.SpaceUserService;
import com.dyx.picturebackend.service.UserService;
import org.apache.ibatis.transaction.Transaction;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
* @author 杜雨轩
* @description 针对表【space(空间)】的数据库操作Service实现
* @createDate 2026-07-25 17:00:46
*/
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space>
    implements SpaceService{
    @Autowired
    private FileManager fileManager;

    @Autowired
    private UserService userService;

    @Resource
    private FilePictureUpload filePictureUpload;

    @Resource
    private UrlPictureUpload urlPictureUpload;
    @Resource
    private CosManger cosManger;

    @Resource
    private SpaceUserService spaceUserService;
    @Resource
    private TransactionTemplate transactionTemplate;

//    @Resource
//    @Lazy
//    private DynamicShardingManager dynamicShardingManager;

    private static final ConcurrentHashMap<Long, Object> lockMap = new ConcurrentHashMap<>();
    @Override
    @Transactional
    public long addSpace(SpaceAddRequest spaceAddRequest, User loginUser) {
        //校验参数
        Space space = new Space();
        BeanUtils.copyProperties(spaceAddRequest,space);
        if (StrUtil.isEmpty(spaceAddRequest.getSpaceName())) {
            space.setSpaceName("默认空间");
        }
        if (ObjUtil.isEmpty(spaceAddRequest.getSpaceLevel())) {
            space.setSpaceLevel(SpaceLevelEnum.COMMON.getValue());
        }
        if (space.getSpaceType()==null) {
            space.setSpaceType(SpaceTypeEnum.PRIVATE.getValue());
        }
        //填充空间大小，容量
        this.fillSpaceBySpaceLevel(space);
        validSpace(space,true);
        Long userId = loginUser.getId();
        space.setUserId(userId);
        if (SpaceLevelEnum.COMMON.getValue() != spaceAddRequest.getSpaceLevel() && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"无权限创建指定级别");
        }
        // 针对用户进行加锁
//        String lock = String.valueOf(userId).intern();
//        synchronized (lock) {
//            //声明式事务
//            Long newSpaceID = transactionTemplate.execute(status -> {
//                boolean exists = this.lambdaQuery().eq(Space::getUserId, userId)
//                        .exists();
//                ThrowUtils.throwif(exists, ErrorCode.OPERATION_ERROR, "用户只能有一个空间");
//                boolean save = this.save(space);
//                if (!save) {
//                    throw new BusinessException(ErrorCode.OPERATION_ERROR);
//                }
//                return space.getId();
//            });
//            return newSpaceID;
//        }
        Object lockObject = lockMap.computeIfAbsent(userId, k -> new Object());
        try {
            synchronized (lockObject) {
                Long newSpaceID = transactionTemplate.execute(status -> {
                    boolean exists = this.lambdaQuery().eq(Space::getUserId, userId).eq(Space::getSpaceType,space.getSpaceType()).exists();
                    ThrowUtils.throwif(exists, ErrorCode.OPERATION_ERROR, "用户只能有一个空间");
                    boolean save = this.save(space);
                    if (!save) {
                        throw new BusinessException(ErrorCode.OPERATION_ERROR);
                    }

// 如果是团队空间，关联新增团队成员记录
                    if (SpaceTypeEnum.TEAM.getValue() == spaceAddRequest.getSpaceType()) {
                        SpaceUser spaceUser = new SpaceUser();
                        spaceUser.setSpaceId(space.getId());
                        spaceUser.setUserId(userId);
                        spaceUser.setSpaceRole(SpaceRoleEnum.ADMIN.getValue());
                        save = spaceUserService.save(spaceUser);
                        ThrowUtils.throwif(!save, ErrorCode.OPERATION_ERROR, "创建团队成员记录失败");
                    }
// 创建分表
                    // dynamicShardingManager.createSpacePictureTable(space);
// 返回新写入的数据 id
                    return space.getId();

                });

                return newSpaceID;
            }
        } finally {
            lockMap.remove(userId,lockObject);
        }
    }

    @Override
    public void validSpace(Space space, boolean add) {
        ThrowUtils.throwif(space == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        String spaceName = space.getSpaceName();
        Integer spaceLevel = space.getSpaceLevel();
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(spaceLevel);
        Integer spaceType = space.getSpaceType();
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(spaceType);



        // 要创建
        if (add) {
            if (StrUtil.isBlank(spaceName)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称不能为空");
            }
            if (spaceLevel == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不能为空");
            }
            if (spaceType == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间类型不能为空");
            }

        }
        // 修改数据时，如果要改空间级别
        if (spaceLevel != null && spaceLevelEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不存在");
        }
        // 修改数据时，如果要改空间级别
        if (spaceType != null && spaceTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间类型不存在");
        }
        if (StrUtil.isNotBlank(spaceName) && spaceName.length() > 30) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称过长");
        }
    }
    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        // 根据空间级别，自动填充限额
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(space.getSpaceLevel());
        if (spaceLevelEnum != null) {
            long maxSize = spaceLevelEnum.getMaxSize();
            if (space.getMaxSize() == null) {
                space.setMaxSize(maxSize);
            }
            long maxCount = spaceLevelEnum.getMaxCount();
            if (space.getMaxCount() == null) {
                space.setMaxCount(maxCount);
            }
        }
    }

    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        if (spaceQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = spaceQueryRequest.getId();
        Long userId = spaceQueryRequest.getUserId();
        String sortField = spaceQueryRequest.getSortField();
        String sortOrder = spaceQueryRequest.getSortOrder();
        Integer spaceType = spaceQueryRequest.getSpaceType();
        String spaceName = spaceQueryRequest.getSpaceName();
        Integer spaceLevel = spaceQueryRequest.getSpaceLevel();
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(spaceName), "spaceName", spaceName);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceLevel), "spaceLevel", spaceLevel);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceType), "spaceType", spaceType);
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        // 对象转封装类
        SpaceVO spaceVO = SpaceVO.objToVo(space);
        // 关联查询用户信息
        Long userId = space.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            spaceVO.setUser(userVO);
        }
        return spaceVO;
    }

    @Override
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request) {
        List<Space> sapceList = spacePage.getRecords();
        Page<SpaceVO> spaceVOPage = new Page<>(spacePage.getCurrent(), spacePage.getSize(), spacePage.getTotal());
        if (CollUtil.isEmpty(sapceList)) {
            return spaceVOPage;
        }
        // 对象列表-》封装对象列表
        List<SpaceVO> spaceVOList = sapceList.stream().map(SpaceVO::objToVo).collect(Collectors.toList());
        //1 关联查询用户信息
        Set<Long> userIdSet = sapceList.stream()
                .map(Space::getUserId)
                .collect(Collectors.toSet());
        List<User> users = userService.listByIds(userIdSet);
        Map<Long, List<User>> userIdUserListMap = users.stream().collect(Collectors.groupingBy(User::getId));
        //2填充信息
        spaceVOList.forEach(spaceVO -> {
            Long userId = spaceVO.getUserId();
            User user=null;
            if (userIdUserListMap.containsKey(userId)) {
                user=userIdUserListMap.get(userId).get(0);
            }
            spaceVO.setUser(userService.getUserVO(user));
        });
        Page<SpaceVO> SpaceVOPage1 = spaceVOPage.setRecords(spaceVOList);
        return SpaceVOPage1;
    }

}




