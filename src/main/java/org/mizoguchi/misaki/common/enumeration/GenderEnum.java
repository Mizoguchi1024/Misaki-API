package org.mizoguchi.misaki.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.InvalidEnumCodeException;

@Getter
@AllArgsConstructor
public enum GenderEnum {
    UNKNOWN(0, "未知"),
    MALE(1, "男性"),
    FEMALE(2, "女性");

    private final int code;
    private final String gender;

    public static GenderEnum fromCode(int code) {
        for (GenderEnum genderEnum : values()) {
            if (genderEnum.code == code){
                return genderEnum;
            }
        }
        throw new InvalidEnumCodeException(FailMessageConstant.INVALID_ENUM_CODE + code);
    }
}
