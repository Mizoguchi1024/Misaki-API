package org.mizoguchi.misaki.common.constant;

public class FailMessageConstant {

    // ===== 请求与安全控制 =====
    public static final String REQUEST_MISSING_HEADERS = "Request missing headers";
    public static final String REQUEST_EXPIRED = "Request expired";
    public static final String REPLAY_ATTACK_DETECTED = "Replay attack detected";

    public static final String INVALID_PARAMETER = "Invalid parameter";
    public static final String INVALID_FIELD_PATTERN = "Invalid field pattern";
    public static final String INVALID_ENUM_CODE = "Invalid enum code";
    public static final String INVALID_SORT_PARAMS = "Invalid sort params";


    // ===== 权限、认证、用户相关 =====
    public static final String USER_ALREADY_EXISTS = "User already exists";
    public static final String USER_NOT_EXISTS = "User not exists";
    public static final String WRONG_PASSWORD = "Wrong password";
    public static final String ACCOUNT_TEMPORARILY_LOCKED = "Account temporarily locked";
    public static final String FAILED_TO_SEND_EMAIL = "Failed to send email";

    public static final String VERIFICATION_CODE_EXPIRED = "Verification code expired";
    public static final String WRONG_VERIFICATION_CODE = "Wrong verification code";

    public static final String JWT_EXPIRED = "JWT expired";
    public static final String INVALID_JWT = "Invalid JWT";


    // ===== 业务资源不存在 =====
    public static final String RESOURCE_NOT_FOUND = "Resource not found";
    public static final String CHAT_NOT_EXISTS = "Chat not exists";
    public static final String MESSAGE_NOT_EXISTS = "Message not exists";
    public static final String ASSISTANT_NOT_EXISTS = "Assistant not exists";
    public static final String MODEL_NOT_EXISTS = "Model not exists";
    public static final String FEEDBACK_NOT_EXISTS = "Feedback not exists";
    public static final String WISH_NOT_EXISTS = "Wish not exists";
    public static final String INCOMPLETE_CHAT = "Incomplete chat";
    public static final String EMAIL_LOG_NOT_EXISTS = "Email log not exists";
    public static final String EXCEPTION_NOT_EXISTS = "Exception not exists";


    // ===== 业务冲突类（已存在、已完成）=====
    public static final String OPTIMISTIC_LOCK_FAILED = "Optimistic lock failed";
    public static final String CHAT_TITLE_ALREADY_EXISTS = "Chat title already exists";
    public static final String MODEL_ALREADY_OWNED = "Model already owned";
    public static final String MODEL_NOT_OWNED = "Model not owned";
    public static final String ALREADY_CHECKED_IN = "Already checked in";
    public static final String TOO_MANY_ASSISTANTS = "Too many assistants";
    public static final String AT_LEAST_ONE_ASSISTANT = "At least one assistant";
    public static final String MODEL_NOT_ON_SALE = "Model not on sale";


    // ===== 资源不足类 =====
    public static final String TOKEN_NOT_ENOUGH = "Token not enough";
    public static final String CRYSTAL_NOT_ENOUGH = "Crystal not enough";
    public static final String STARDUST_NOT_ENOUGH = "Stardust not enough";
    public static final String PUZZLE_NOT_ENOUGH = "Puzzle not enough";


    // ===== 系统级错误 =====
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";
    public static final String BAD_AI_OUTPUT = "Bad AI output";
}

