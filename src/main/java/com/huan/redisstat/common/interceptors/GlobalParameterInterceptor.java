package com.huan.redisstat.common.interceptors;

import com.huan.redisstat.common.NavHelper;
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
 *
 */
public class GlobalParameterInterceptor extends HandlerInterceptorAdapter {

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

		String requestURI = request.getRequestURI();
		UriComponents uriComponents = UriComponentsBuilder.fromUriString(requestURI).build();
		List<String> pathSegments = uriComponents.getPathSegments();

		if (modelAndView != null) {
			Map<String, Object> model = modelAndView.getModel();
			model.put("Path_Segments", pathSegments);
			model.put("NAV_TOP_NODES", NavHelper.topNavNodes);
			model.put("Nav_Current_Node", NavHelper.getNavNodeByPath(requestURI));
			model.put("Nav_Node_Link", NavHelper.getNavNodeLinkByPath(requestURI));
		}
	}

}
