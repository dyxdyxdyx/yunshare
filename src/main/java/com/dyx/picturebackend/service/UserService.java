package com.dyx.picturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dyx.picturebackend.model.dto.UserQueryRequest;
import com.dyx.picturebackend.model.eneity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dyx.picturebackend.model.vo.LoginUserVo;
import com.dyx.picturebackend.model.vo.UserVO;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.PublicKey;
import java.util.List;

/**
* @author 杜雨轩
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2026-07-06 21:02:08
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    public String getEncryptPassword(String userPassword);
    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVo userLogin(String userAccount, String userPassword, HttpServletRequest request);
    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);
    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);


    /**
     * 获得脱敏的用户信息
     * @param user
     * @return
     */
    UserVO getUserVO(User user);
    /**
     * 获得脱敏的用户信息
     * @param user
     * @return
     */
    List<UserVO> getUserVOList(List<User> user);

    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

}
