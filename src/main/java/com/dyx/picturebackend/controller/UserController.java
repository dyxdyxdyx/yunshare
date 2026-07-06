package com.dyx.picturebackend.controller;

import cn.hutool.core.bean.BeanUtil;
import com.dyx.picturebackend.common.BaseResponse;
import com.dyx.picturebackend.common.ResultUtils;
import com.dyx.picturebackend.exception.ErrorCode;
import com.dyx.picturebackend.exception.ThrowUtils;
import com.dyx.picturebackend.model.dto.UserLoginRequest;
import com.dyx.picturebackend.model.dto.UserRegisterRequest;
import com.dyx.picturebackend.model.eneity.User;
import com.dyx.picturebackend.model.vo.LoginUserVo;
import com.dyx.picturebackend.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwif(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<LoginUserVo> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwif(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        LoginUserVo loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVo> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        LoginUserVo loginUserVo = new LoginUserVo();
        BeanUtil.copyProperties(loginUser,loginUserVo);
        return ResultUtils.success(loginUserVo);
    }

}
