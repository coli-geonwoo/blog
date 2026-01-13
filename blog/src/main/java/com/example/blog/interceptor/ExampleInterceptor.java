package com.example.blog.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class ExampleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String test = request.getParameter("test");
        if("preHandle".equals(test)) {
            throw new RuntimeException("preHandle 에서 발생한 예외");
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        String test = request.getParameter("test");
        if("postHandle".equals(test)) {
            throw new RuntimeException("postHandle 에서 발생한 예외");
        }
//        String test = request.getParameter("test");
        System.out.println("응답 커밋 여부: " + response.isCommitted());
//        System.out.println(test + "test");
//
//        if("interceptor".equals(test)) {
//            throw new RuntimeException("interceptor 에서 발생한 예외");
//        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        String test = request.getParameter("test");
        if("afterCompletion".equals(test)) {
            throw new RuntimeException("afterCompletion 에서 발생한 예외");
        }
    }
}
