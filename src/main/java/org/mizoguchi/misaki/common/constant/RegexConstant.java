package org.mizoguchi.misaki.common.constant;

public class RegexConstant {
    public static final String NOT_BLANK = ".*\\S.*";
    public static final String HEX_COLOR = "^#(?:[0-9a-fA-F]{3}){1,2}$";
    public static final String PURE_NUMBER = "^[0-9]*$";
    public static final String QUOTE_PREFIX = "^[\"'“”‘’《（(【「『]+";
    public static final String QUOTE_SUFFIX = "[\"'“”‘’》）)】」』]+$";
    public static final String TITLE_INDICATION = "^标题[:：]\\s*";
    public static final String CJK_CHARACTER = "\\p{IsHan}";
    public static final String ENGLISH_WORD = "[A-Za-z]+";
}
