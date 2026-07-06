package com.dyx.picturebackend.model.vo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.dyx.picturebackend.model.enums.UserRoleEnum;
import lombok.Data;

/**
 * 用户
 * @TableName user
 */
@Data
public class LoginUserVo implements Serializable {
    private Long id;
    private String userAccount;
    private String userName;
    private String userAvatar;
    private String userProfile;
    private String userRole;
    private Date editTime;
    private Date createTime;
    private Date updateTime;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}