package com.cakequake.cakequakeback.security.filter;

import com.cakequake.cakequakeback.common.exception.BusinessException;
import com.cakequake.cakequakeback.common.exception.ErrorCode;
import com.cakequake.cakequakeback.common.exception.ErrorResponseDTO;
import com.cakequake.cakequakeback.common.utils.JWTUtil;
import com.cakequake.cakequakeback.member.entities.Member;
import com.cakequake.cakequakeback.member.entities.MemberStatus;
import com.cakequake.cakequakeback.member.repo.MemberRepository;
import com.cakequake.cakequakeback.security.domain.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    public JWTAuthenticationFilter(JWTUtil jwtUtil, MemberRepository memberRepository, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
        this.objectMapper = objectMapper;
    }

    public enum JWTErrorCode {

        NO_ACCESS_TOKEN(401, "No access token"),
        EXPIRED_TOKEN(401, "Expired token"),
        BAD_SIGNATURE(401, "Bad signature"),
        MALFORMED_TOKEN(401, "Malformed token");

        private int code;
        private String message;

        JWTErrorCode(int code, String message) {
            this.code = code;
            this.message = message;
        }
        public int getCode() {
            return code;
        }
        public String getMessage() {
            return message;
        }

        public ErrorCode toGlobalErrorCode() {
            return switch (this) {
                case NO_ACCESS_TOKEN -> ErrorCode.MISSING_JWT;
                case EXPIRED_TOKEN -> ErrorCode.EXPIRED_JWT;
                case BAD_SIGNATURE, MALFORMED_TOKEN -> ErrorCode.INVALID_JWT;
            };
        }

    }

    private static final List<String> EXCLUDE_PATHS = List.of(
            "/api/v1/auth/signin",
            "/api/v1/auth/signup",
            "/api/v1/auth/otp/send", 
            "/api/v1/auth/otp/verify",
            "/api/v1/auth/business/verify",
            "/api/v1/auth/refresh",
            "/css/",
            "/js/",
            "/images/",
            "/favicon.ico",
            "/ws",
            "/ws/",          
            "/websocket/",   
            "/sockjs-node/"  
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        log.info("=============JWT-shouldNotFilter======================");

        String path = request.getServletPath();
        boolean exclude = EXCLUDE_PATHS.stream().anyMatch(path::startsWith);
        log.debug("필터 상태 - 경로: '{}', 적용 여부: {}", path, exclude ? "제외됨" : "적용됨");
        return exclude;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("============= JWT-doFilterInternal ================");
        log.debug("requestURI: {}", request.getRequestURI());

        String accessToken = null;
        
        String headerStr = request.getHeader("Authorization");
        if (headerStr != null && headerStr.startsWith("Bearer ")) {
            accessToken = headerStr.substring(7); // "Bearer " 제거
            log.debug("accessToken from header");
        } else {
            // 2. Authorization 헤더 없으면 쿠키에서 accessToken 추출
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("accessToken".equals(cookie.getName())) {
                        accessToken = cookie.getValue();
                        log.debug("accessToken from cookie");
                        break;
                    }
                } 
            } 
        } 

        if (accessToken == null || accessToken.contains("undefined")) {
            handleException(response, JWTErrorCode.NO_ACCESS_TOKEN);
            return;
        }

        try {
            Map<String, Object> tokenMap = jwtUtil.validateToken(accessToken);
            String userId = (String) tokenMap.get("userId");

            Member member = memberRepository.findByUserIdAndStatus(userId, MemberStatus.ACTIVE)
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

            CustomUserDetails userDetails = new CustomUserDetails(member);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
                log.error("++++++++++doFilterInternal++++++++++++++");
                String message = e.getMessage();
                if(message.startsWith("JWT signature")){
                    handleException(response, JWTErrorCode.BAD_SIGNATURE);
                }else if(message.startsWith("JWT malformed")){
                    handleException(response, JWTErrorCode.MALFORMED_TOKEN);
                }else if(message.startsWith("JWT expired")){
                    handleException(response, JWTErrorCode.EXPIRED_TOKEN);
                }else {
                    log.error("JWT 외의 에러: ", e);
                }
            }
    }


    private void handleException(HttpServletResponse response, JWTErrorCode jwtErrorCode) throws IOException {

        ErrorCode globalCode = jwtErrorCode.toGlobalErrorCode();

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(globalCode);

        response.setStatus(globalCode.getHttpStatus()); // 보통 401
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
