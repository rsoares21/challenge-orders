package com.challenge.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final Map<String, AtomicInteger> requestCountsPerIpAddress = new ConcurrentHashMap<>();
    private final int MAX_REQUESTS_PER_MINUTE = 10;
    private final long ONE_MINUTE = 60 * 1000L;
    private final Map<String, Long> timestampPerIpAddress = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = getClientIP(request);
        long currentTime = System.currentTimeMillis();

        timestampPerIpAddress.putIfAbsent(clientIp, currentTime);
        requestCountsPerIpAddress.putIfAbsent(clientIp, new AtomicInteger(0));

        long timeSinceFirstRequest = currentTime - timestampPerIpAddress.get(clientIp);

        if (timeSinceFirstRequest > ONE_MINUTE) {
            timestampPerIpAddress.put(clientIp, currentTime);
            requestCountsPerIpAddress.get(clientIp).set(0);
        }

        int requests = requestCountsPerIpAddress.get(clientIp).incrementAndGet();

        if (requests > MAX_REQUESTS_PER_MINUTE) {
            response.setStatus(429);
            response.getWriter().write("Too many requests - Rate limit exceeded");
            return false;
        }

        return true;
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
