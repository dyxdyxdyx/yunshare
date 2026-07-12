package com.dyx.picturebackend.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class PictureTagCategory {
    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 分页列表
     */
    private List<String> categoryList;

}
