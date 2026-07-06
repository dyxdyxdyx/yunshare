package com.dyx.picturebackend.model.eneity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.dyx.picturebackend.model.enums.UserRoleEnum;
import lombok.Data;

/**
 * 用户
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    @TableField("userAccount")
    private String userAccount;
    @TableField("userPassword")
    private String userPassword;
    @TableField("userName")
    private String userName;
    @TableField("userAvatar")
    private String userAvatar;
    @TableField("userProfile")
    private String userProfile;
    @TableField("userRole")
    private String userRole;
    @TableField("editTime")
    private Date editTime;
    @TableField("createTime")
    private Date createTime;
    @TableField("updateTime")
    private Date updateTime;
    /**
     * 是否删除
     */
    @TableLogic
    @TableField("isDelete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    public static User getUserEneity(String userAccount,String encryptPassword ){
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("无名");
        user.setUserRole(UserRoleEnum.USER.getValue());
        return user;
    }
}