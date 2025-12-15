package org.mizoguchi.misaki.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mizoguchi.misaki.common.constant.FailMessageConstant;
import org.mizoguchi.misaki.common.exception.InvalidEnumCodeException;

@Getter
@AllArgsConstructor
public enum GenderEnum {
    UNKNOWN(0, "Unknown"),
    MALE(1, "Male"),
    FEMALE(2, "Female");

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
