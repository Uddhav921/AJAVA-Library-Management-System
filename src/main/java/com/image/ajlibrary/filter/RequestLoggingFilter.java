package com.image.ajlibrary.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * CO1 - Servlet Filter (Servlet & JSP API).
 *
 * Extends Spring's OncePerRequestFilter (which is a Servlet Filter) to
 * intercept every incoming HTTP request exactly once.
 *
 * Responsibilities:
 *  - Records request start time
 *  - Logs HTTP method, URI, and query string on arrival
 *  - Logs response status and total processing time on completion
 *
 * This is analogous to the traditional Servlet Filter registered via web.xml
 * but uses Spring's modern component-scan approach.
 */
@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        String method    = request.getMethod();
        String uri       = request.getRequestURI();
        String queryStr  = request.getQueryString();
        String client    = request.getRemoteAddr();

        String fullPath  = queryStr != null ? uri + "?" + queryStr : uri;

        log.info("[REQUEST ] --> {} {} | Client: {}", method, fullPath, client);

        try {
            // Pass control to the next filter / servlet
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int  status   = response.getStatus();

            log.info("[RESPONSE] <-- {} {} | Status: {} | Time: {} ms",
                    method, fullPath, status, duration);
        }
    }

    /**
     * Skip logging for static resources to reduce noise.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/css/")
                || uri.startsWith("/js/")
                || uri.startsWith("/images/")
                || uri.startsWith("/favicon");
    }
}
