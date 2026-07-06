package com.dyx.picturebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dyx.picturebackend.constant.UserConstant;
import com.dyx.picturebackend.exception.BusinessException;
import com.dyx.picturebackend.exception.ErrorCode;
import com.dyx.picturebackend.exception.ThrowUtils;
import com.dyx.picturebackend.model.eneity.User;
import com.dyx.picturebackend.model.enums.UserRoleEnum;
import com.dyx.picturebackend.model.vo.LoginUserVo;
import com.dyx.picturebackend.service.UserService;
import com.dyx.picturebackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.jws.Oneway;
import javax.servlet.http.HttpServletRequest;

/**
* @author 杜雨轩
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2026-07-06 21:02:08
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    /**
     * 用户注释代码
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1，校验参数
        if (StrUtil.hasBlank(userAccount ,userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数错误");
        }
        ThrowUtils.throwif(userAccount.length()<4,ErrorCode.PARAMS_ERROR,"账号太短");
        ThrowUtils.throwif(userPassword.length()<8||checkPassword.length()<8,ErrorCode.PARAMS_ERROR,"密码太短");
        ThrowUtils.throwif(!checkPassword.equals(userPassword),ErrorCode.PARAMS_ERROR,"两次密码不一样");
        //2，检测用户账号是否和数据库已有数据重复
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserAccount,userAccount);
        User one = this.getOne(wrapper);
        if (!ObjUtil.isEmpty(one)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已存在");
        }
        //3，密码加密
        String encryptPassword = getEncryptPassword(userPassword);
        //4.插入数据到数据库
        User userEneity = User.getUserEneity(userAccount, encryptPassword);
        boolean save = this.save(userEneity);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"注册失败，数据库错误");
        }
        return userEneity.getId();
    }
    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "yupi";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    @Override
    public LoginUserVo userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1 校验参数
        if (StrUtil.hasBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        ThrowUtils.throwif(userAccount.length()<4,ErrorCode.PARAMS_ERROR,"账号错误");
        ThrowUtils.throwif(userPassword.length()<4,ErrorCode.PARAMS_ERROR,"密码错误");
        //2，对用户传递的密码就行加密
        String encryptPassword = getEncryptPassword(userPassword);
        //3.查询数据库用户是不是存在，不存在抛异常
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserAccount,userAccount);
        wrapper.eq(User::getUserPassword,encryptPassword);
        User user = this.getOne(wrapper);
        if (ObjUtil.isEmpty(user)){
            log.info("用户不存在或者密码错误");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在或者密码错误");
        }
        //4，保存用户的登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE,user);
        LoginUserVo loginUserVo = new LoginUserVo();
        BeanUtil.copyProperties(user,loginUserVo);
        return loginUserVo;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接返回上述结果）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }



}




