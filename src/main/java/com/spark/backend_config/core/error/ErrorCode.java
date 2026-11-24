package com.spark.backend_config.core.error;

import lombok.Getter;

@Getter
public enum ErrorCode { ;
/*
    //HTTP 400 BAD Request (code:4000~4099)
    INVALID_USER_ID(400, 4000, "아이디가 조건에 불충족할 경우"),
    INVALID_PASSWORD(400, 4001, "비밀번호가 조건에 불충족할 경우"),


    //HTTP 401 Unauthorized (code:4100~4199)
    INVALID_CREDENTIALS(401, 4100, "아이디 또는 패스워드가 일치하지 않습니다."),
    MISSING_JWT(401, 4101, "JWT 토큰이 존재하지 않거나 형식이 올바르지 않습니다."),
    INVALID_JWT(401, 4102, "유효하지 않은 JWT 토큰입니다."),
    EXPIRED_JWT(401, 4103, "만료된 JWT 토큰입니다."),


    //HTTP 403 Forbidden (code:4300~4399)
    ALREADY_LOGGED_OUT(403, 4300, "이미 로그아웃된 토큰입니다"),
    NO_ADMIN_ROLE(403, 4301, "관리자 권한이 필요합니다"),


    //HTTP 404 Not Found (code:4400~4499)
    MEMBER_NOT_FOUND(404, 4401, "해당 회원을 찾을 수 없습니다."),
    NOT_FOUND_QNA_ID(404, 4402, "QnA를 찾을 수 없습니다"),

    //HTTP 409 Conflict (code:4900~4999)
    ALREADY_EXIST_USER_ID(409, 701, "이미 존재하는 아이디입니다"),
    ALREADY_EXIST_PHONE(409, 702, "이미 가입된 전화번호입니다"),


    //HTTP 429 Too Many Request (code:9000)
    EXCEEDED_SMS_LIMIT(429, 9000,"SMS 요청 한도를 초과했습니다"),
    TOO_MANY_REQUESTS_IN_SHORT_TIME(429, 9001, "인증번호는 1분 후에 다시 요청할 수 있습니다."),

    //HTTP 500 InternalServer Error & 502 Bad Gateway (code:5000~5099)
    INTERNAL_SERVER_ERROR(500, 5000,"서버 내부 오류입니다"),
    BAD_GATEWAY(502, 5001,"외부 서비스(PG)와 연결이 끊겼습니다"),
*/


    private final int httpStatus;
    private final int code;
    private final String message;

    ErrorCode(int httpStatus, int code, String message){
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}