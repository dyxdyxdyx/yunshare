package com.dyx.picturebackend.controller;

import com.dyx.picturebackend.common.BaseResponse;
import com.dyx.picturebackend.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class healthController {
    @GetMapping
    public BaseResponse<String> health() {
        return ResultUtils.success("ok");
    }
}

