package org.mizoguchi.misaki.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LikesTargetTypeEnum {
    MISAKI(0),
    ASSISTANT(1),
    SCRIPT(2);

    private final int value;
}
