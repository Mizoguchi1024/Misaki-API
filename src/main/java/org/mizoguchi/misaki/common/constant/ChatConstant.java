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
    public static final String ASSISTANT_NAME = "assistantName";
    public static final String ASSISTANT_GENDER = "assistantGender";
    public static final String ASSISTANT_BIRTHDAY = "assistantBirthday";
    public static final String ASSISTANT_PERSONALITY = "assistantPersonality";
    public static final String USER_NAME = "userName";
    public static final String USER_GENDER = "userGender";
    public static final String USER_BIRTHDAY = "userBirthday";
    public static final String USER_OCCUPATION = "userOccupation";
    public static final String USER_DETAIL = "userDetail";
    public static final String SYSTEM_GENERATE_PROMPTS = """
            Generate {size} user prompt suggestions based on assistant message, output in JSON format.
            
            EXAMPLE JSON OUTPUT:
            {
                "prompts": ["prompts1", "prompts2", "prompts3"]
            }
            """;
    public static final String SIZE = "size";
    public static final String SYSTEM_GENERATE_TITLE = "Generate a concise title for this conversation without quotation marks.";
    public static final String CODE_QUOTE = "```";
    public static final String CONVERSATION_ID = "chat_memory_conversation_id";
    public static final String PARENT_ID = "chat_memory_parent_id";
}
