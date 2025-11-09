package org.mizoguchi.misaki.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mizoguchi.misaki.common.constant.MessageConstant;
import org.mizoguchi.misaki.common.exception.InvalidEnumCodeException;

@Getter
@AllArgsConstructor
public enum AuthRoleEnum {
    USER(0, "ROLE_USER"),
    ADMIN(1, "ROLE_ADMIN");

    private final int code;
    private final String roleName;

    public static AuthRoleEnum fromCode(int code) {
        for (AuthRoleEnum authRoleEnum : AuthRoleEnum.values()) {
            if (authRoleEnum.code == code) {
                return authRoleEnum;
            }
        }
        throw new InvalidEnumCodeException(MessageConstant.INVALID_ENUM_CODE + code);
    }
}
