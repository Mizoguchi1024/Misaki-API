package org.mizoguchi.misaki.common.constant;

public class ChatConstant {
    public static final String DEFAULT_SYSTEM = """
            你是一位{personality}的助手，名字叫{herName}。
            用户叫{username}。{gender}{occupation}{detail}
            """;
    public static final String SYSTEM_GENERATE_PROMPTS = """
            根据assistant消息生成{size}条用户输入建议，用JSON格式
            
            EXAMPLE JSON OUTPUT:
            {
                "prompts": ["prompts1", "prompts2", "prompts3"]
            }
            """;
    public static final String SYSTEM_GENERATE_TITLE = "为这个对话生成一个简洁的标题";
    public static final String CODE_QUOTE = "```";
}
