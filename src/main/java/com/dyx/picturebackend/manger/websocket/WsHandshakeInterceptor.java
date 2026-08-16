package com.dyx.picturebackend.manger.websocket;

import cn.hutool.core.util.ObjUtil;
import com.dyx.picturebackend.manger.auth.SpaceUserAuthManager.SpaceUserAuthManager;
import com.dyx.picturebackend.manger.auth.model.SpaceUserPermissionConstant;
import com.dyx.picturebackend.model.eneity.Picture;
import com.dyx.picturebackend.model.eneity.Space;
import com.dyx.picturebackend.model.eneity.User;
import com.dyx.picturebackend.model.enums.SpaceTypeEnum;
import com.dyx.picturebackend.service.PictureService;
import com.dyx.picturebackend.service.SpaceService;
import com.dyx.picturebackend.service.UserService;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * websocket拦截器
 */
public class WsHandshakeInterceptor implements HandshakeInterceptor {

    @Resource
    private UserService userService;
    @Resource
    private PictureService pictureService;
    @Resource
    private SpaceService spaceService;
    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            String pictureId = httpServletRequest.getParameter("pictureId");
            if (StrUtil.isBlank(pictureId)) {
                return false;
            }

            User loginUser = userService.getLoginUser(httpServletRequest);
            if (ObjUtil.isEmpty(loginUser)) {
                return false;
            }
            Picture picture = pictureService.getById(pictureId);
            if (picture==null) {
                return false;
            }
            Long spaceId = picture.getSpaceId();
            Space space = null;
            if (spaceId != null) {
                space = spaceService.getById(spaceId);
                if (ObjUtil.isEmpty(space)) {
                    return false;
                }
                if (space.getSpaceType()!= SpaceTypeEnum.TEAM.getValue()) {
                    return false;
                }
            }
            List<String> permissionList = spaceUserAuthManager.getPermissionList(space, loginUser);

            if (!permissionList.contains(SpaceUserPermissionConstant.PICTURE_EDIT)) {
                return false;
            }
            //设置用户信息到websocket中
            attributes.put("user",loginUser);
            attributes.put("userId",loginUser.getId());
            attributes.put("pictureId",Long.valueOf(pictureId));

        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
