package org.mizoguchi.misaki.common.constant;

public class ChatConstant {
    public static final String SYSTEM_DEFAULT = """
            AssistantName: {assistantName}
            AssistantGender: {assistantGender}
            AssistantBirthday: {assistantBirthday}
            AssistantPersonality: {assistantPersonality}
            
            UserName: {userName}
            UserGender: {userGender}
            UserBirthday: {userBirthday}
            UserOccupation: {userOccupation}
            UserDetail: {userDetail}
            """;
    public static final String SYSTEM_GENERATE_PROMPTS = """
            Generate {size} user prompt suggestions based on assistant message, output in JSON format.
            
            EXAMPLE JSON OUTPUT:
            {
                "prompts": ["prompts1", "prompts2", "prompts3"]
            }
            """;
    public static final String SYSTEM_GENERATE_TITLE = "Generate a concise title for this conversation without quotation marks.";
    public static final String CODE_QUOTE = "```";
}
