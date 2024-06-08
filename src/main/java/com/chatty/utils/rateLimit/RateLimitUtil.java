package com.chatty.utils.rateLimit;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

public class RateLimitUtil {
    private static final String[] IP_HEADER = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA"
    };

    public static String getClientIp(HttpServletRequest request){
        String ssss = Arrays.stream(IP_HEADER)
                .map(request::getHeader)
                .filter(ipAddress -> ipAddress != null && !ipAddress.isEmpty() && !"unknown".equalsIgnoreCase(ipAddress))
                .map(ipAddress -> ipAddress.split(":")[0])
                .findFirst()
                .orElseGet(request::getRemoteAddr);
        System.out.println("ssss = " + ssss);
        System.out.println("ssss = " + ssss);
        System.out.println("ssss = " + ssss);
        System.out.println("ssss = " + ssss);
        System.out.println("ssss = " + ssss);
        System.out.println("ssss = " + ssss);
        return Arrays.stream(IP_HEADER)
                .map(request::getHeader)
                .filter(ipAddress -> ipAddress != null && !ipAddress.isEmpty() && !"unknown".equalsIgnoreCase(ipAddress))
                .map(ipAddress -> ipAddress.split(",")[0])
                .findFirst()
                .orElseGet(request::getRemoteAddr);
    }
}
