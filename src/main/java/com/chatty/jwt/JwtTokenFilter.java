package com.chatty.jwt;

import com.chatty.constants.Code;
import com.chatty.exception.CustomException;
import com.chatty.service.user.UserDetailsServiceImpl;
import com.chatty.utils.jwt.JwtTokenUtils;
import com.chatty.validator.TokenValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenValidator tokenValidator;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("요청 URI: {}", request.getRequestURI());
        log.info("요청 IP: {}", request.getRemoteAddr());
        String accessToken = jwtTokenProvider.resolveAccessToken(request);

        try {
            tokenValidator.validateAccessToken(accessToken);
        }catch (CustomException e){
            log.error("valid 에러 코드 발생");
            request.setAttribute("errorCode",e);
        }

        accessToken = JwtTokenUtils.getAccessToken(accessToken);

        String userMobileNumber = jwtTokenProvider.getMobileNumber(accessToken);
        String deviceId = jwtTokenProvider.getDeviceId(accessToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userMobileNumber);

        try {
            if(!userDetails.getPassword().equals(deviceId)){ // 기기번호 검증도 같이 해준다.
                log.error("기기번호가 일치하지 않아 유효하지 않은 토큰입니다.");
                request.setAttribute("exception",Code.INVALID_TOKEN);
            }
        }catch (CustomException e){
            log.error("valid 에러 코드 발생");
            request.setAttribute("errorCode",e);
        }


        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        log.info("doFilter로 넘어가기");

        GrantedAuthority grantedAuthority = userDetails.getAuthorities().stream().findFirst().get();
        String authority = grantedAuthority.getAuthority();
        request.setAttribute("role", authority.split("_")[1]);

        filterChain.doFilter(request, response);
    }
}
