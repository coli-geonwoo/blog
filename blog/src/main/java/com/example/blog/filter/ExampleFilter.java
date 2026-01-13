package com.example.blog.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class ExampleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String test = servletRequest.getParameter("test");

        if(test.equals("filter")) {
            throw new RuntimeException("Filter에서 발생한 예외");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
