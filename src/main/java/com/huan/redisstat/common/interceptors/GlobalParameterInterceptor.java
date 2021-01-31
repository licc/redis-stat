package com.huan.redisstat.common.interceptors;

import com.huan.redisstat.common.NavHelper;
import com.huan.redisstat.common.SystemUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author lihuan
 * 参数注入拦截器
 */
public class GlobalParameterInterceptor extends HandlerInterceptorAdapter {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

        String requestUri = request.getRequestURI();
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(requestUri).build();
        List<String> pathSegments = uriComponents.getPathSegments();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof AnonymousAuthenticationToken) && modelAndView != null) {
            Map<String, Object> model = modelAndView.getModel();
            model.put("Path_Segments", pathSegments);
            model.put("NAV_TOP_NODES", NavHelper.topNavNodes);
            model.put("Nav_Current_Node", NavHelper.getNavNodeByPath(requestUri));
            model.put("Nav_Node_Link", NavHelper.getNavNodeLinkByPath(requestUri));

            model.put("currUser", SystemUtils.getCurrLoginUser());


        }
    }

}

