package org.mizoguchi.misaki.common.constant;

public class ChatConstant {
    public static final String DEFAULT_SYSTEM = """
            你是一位{personality}的助手，名字叫{herName}。
            用户叫{username}。{gender}{occupation}{detail}
            """;
    public static final String SYSTEM_GENERATE_TITLE = "为这个对话生成一个简洁的标题";
    public static final String CODE_QUOTE = "```";
}
