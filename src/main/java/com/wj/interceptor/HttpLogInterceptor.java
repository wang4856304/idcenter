package com.wj.interceptor;

import com.wj.filter.BodyReaderHttpServletRequestWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * @author wangjun
 * @date 18-2-23 下午2:41
 * @description
 * @modified by
 */
public class HttpLogInterceptor implements HandlerInterceptor {
    private static Log logger = LogFactory.getLog(HttpLogInterceptor.class);

    private ThreadLocal<Long> startTime = new ThreadLocal<>();
    @Override
    public boolean preHandle(@NonNull HttpServletRequest httpServletRequest, @NonNull HttpServletResponse httpServletResponse, @NonNull Object handler) throws Exception {
        startTime.set(System.currentTimeMillis());
        String url = httpServletRequest.getRequestURL().toString();
        String queryString = httpServletRequest.getQueryString();
        String methodName = httpServletRequest.getMethod();
        if (!"get".equalsIgnoreCase(methodName)&&!"post".equalsIgnoreCase(methodName)) {
            return true;
        }
        String contentType = httpServletRequest.getHeader("Content-Type");
        if (StringUtils.isEmpty(queryString)) {
            Enumeration<String> params = httpServletRequest.getParameterNames();
            if (params.hasMoreElements()) {
                StringBuffer sb = new StringBuffer();
                while (params.hasMoreElements()) {
                    String paramName = params.nextElement();
                    String value = httpServletRequest.getParameter(paramName);
                    sb.append(paramName).append("=").append(value).append("&");
                }
                logger.info("request url=" + url + ", method=" + methodName + " ,contentType=" + contentType + ", param: " + sb.toString().substring(0, sb.toString().length()-1));
                return true;
            }
            String data = new BodyReaderHttpServletRequestWrapper(httpServletRequest).getBodyString(httpServletRequest);
            if (StringUtils.isEmpty(data)) {
                logger.info("request url=" + url + ", method=" + methodName + " ,contentType=" + contentType);
            }
            else {
                logger.info("request url=" + url + ", method=" + methodName + " ,contentType=" + contentType + ", param: " + data);
            }

        }
        else {
            logger.info("request url=" + url + ", method=" + methodName + ", param: " + queryString);
        }
        return true;
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest httpServletRequest, @NonNull HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest httpServletRequest, @NonNull HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        logger.info("interface cost times：" + (System.currentTimeMillis()-startTime.get()) + "ms");
    }
}
