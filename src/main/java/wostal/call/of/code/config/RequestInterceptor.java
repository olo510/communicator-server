package wostal.call.of.code.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class RequestInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (request.getRequestURL().toString().contains("/error")) {
			return true;
		}
		String userAgent = request.getHeader("User-Agent");
		if (!userAgent.contains("JavaFX")) {
			//response.sendRedirect("error");
		}
		return true;
	}
}