package com.dyx.picturebackend.model.eneity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 图片
 * @TableName picture
 */
@TableName(value ="picture")
@Data
public class Picture implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 图片 url
     */
    @TableField("url")
    private String url;
    /**
     * 缩略图 url
     */
    @TableField("thumbnailUrl")
    private String thumbnailUrl;
    /**
     * 空间 id
     */
    @TableField("spaceId")
    private Long spaceId;
    /**
     * 图片主色调
     */
    @TableField("picColor")
    private String picColor;

    /**
     * 图片名称
     */
    @TableField("name")
    private String name;

    /**
     * 简介
     */
    @TableField("introduction")
    private String introduction;

    /**
     * 分类
     */
    @TableField("category")
    private String category;

    /**
     * 标签（JSON 数组）
     */
    @TableField("")
    private String tags;

    /**
     * 图片体积
     */
    @TableField("picSize")
    private Long picSize;

    /**
     * 图片宽度
     */
    @TableField("picWidth")
    private Integer picWidth;

    /**
     * 图片高度
     */
    @TableField("picHeight")
    private Integer picHeight;

    /**
     * 图片宽高比例
     */
    @TableField("picScale")
    private Double picScale;

    /**
     * 图片格式
     */
    @TableField("picFormat")
    private String picFormat;

    /**
     * 创建用户 id
     */
    @TableField("userId")
    private Long userId;

    @TableField("createTime")
    private Date createTime;
    /**
     * 状态：0-待审核; 1-通过; 2-拒绝
     */
    @TableField("reviewStatus")
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    @TableField("reviewMessage")
    private String reviewMessage;

    /**
     * 审核人 id
     */
    @TableField("reviewerId")
    private Long reviewerId;

    /**
     * 审核时间
     */
    @TableField("reviewTime")
    private Date reviewTime;


    /**
     * 编辑时间
     */
    @TableField("editTime")
    private Date editTime;

    /**
     * 更新时间
     */
    @TableField("updateTime")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableField("isDelete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}