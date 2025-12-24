package org.mizoguchi.misaki.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.InvalidEnumCodeException;

@Getter
@AllArgsConstructor
public enum AuthRoleEnum {
    USER(0, "USER", "ROLE_USER"),
    ADMIN(1, "ADMIN", "ROLE_ADMIN");

    private final int code;
    private final String role;
    private final String roleWithPrefix;

    public static AuthRoleEnum fromCode(int code) {
        for (AuthRoleEnum authRoleEnum : AuthRoleEnum.values()) {
            if (authRoleEnum.code == code) {
                return authRoleEnum;
            }
        }
        throw new InvalidEnumCodeException(FailMessageConstant.INVALID_ENUM_CODE + code);
    }
}
