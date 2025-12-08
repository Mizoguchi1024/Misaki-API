package org.mizoguchi.misaki.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageTypeEnum {
    USER("USER"),
    ASSISTANT("ASSISTANT"),
    SYSTEM("SYSTEM"),
    TOOL("TOOL"),;

    private final String value;
}
