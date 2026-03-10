package org.mizoguchi.misaki.common.constant;

public class ChatConstant {
    public static final String SYSTEM_DEFAULT = """
            The following information defines the assistant and the user. Some fields could not be set.
            
            AssistantName: {assistantName}
            AssistantGender: {assistantGender}
            AssistantBirthday: {assistantBirthday}
            AssistantPersonality: {assistantPersonality}
            AssistantDetail: {assistantDetail}
            
            UserName: {userName}
            UserGender: {userGender}
            UserBirthday: {userBirthday}
            UserOccupation: {userOccupation}
            UserDetail: {userDetail}

            Additional:
            - Always reply in the same language as the user's latest message.
            - If the AssistantName is "Misaki", it's the alias of "中原岬".
            - Use Mermaid code if user ask for creating diagrams.
            """;
    public static final String ASSISTANT_NAME = "assistantName";
    public static final String ASSISTANT_GENDER = "assistantGender";
    public static final String ASSISTANT_BIRTHDAY = "assistantBirthday";
    public static final String ASSISTANT_PERSONALITY = "assistantPersonality";
    public static final String ASSISTANT_DETAIL = "assistantDetail";
    public static final String USER_NAME = "userName";
    public static final String USER_GENDER = "userGender";
    public static final String USER_BIRTHDAY = "userBirthday";
    public static final String USER_OCCUPATION = "userOccupation";
    public static final String USER_DETAIL = "userDetail";
    public static final String SYSTEM_GENERATE_PROMPTS = """
            Generate {size} user prompt suggestions as the user reply for the last assistant message, output in JSON format.
            The following information defines the assistant and the user. Some fields could not be set.
            
            AssistantName: {assistantName}
            AssistantGender: {assistantGender}
            AssistantBirthday: {assistantBirthday}
            AssistantPersonality: {assistantPersonality}
            AssistantDetail: {assistantDetail}
            
            UserName: {userName}
            UserGender: {userGender}
            UserBirthday: {userBirthday}
            UserOccupation: {userOccupation}
            UserDetail: {userDetail}

            Additional:
            - If the AssistantName is "Misaki", it's the alias of "中原岬".

            EXAMPLE JSON OUTPUT:
            \\{
                "prompts": \\["prompt1", "prompt2", "prompt3"\\]
            \\}
            """;
    public static final String SIZE = "size";
    public static final String SYSTEM_GENERATE_TITLE = """
            Generate a concise title for this conversation.
            
            Rules:
            - In user's language.
            - As short as possible.
            - Focus on user's intention.
            - Ignore assistant's roleplay words.
            - Do not include emojis.
            
            EXAMPLE JSON OUTPUT:
            \\{
                "title": ""
            \\}
            """;
    public static final String CODE_QUOTE = "```";
    public static final String CONVERSATION_ID = "chat_memory_conversation_id";
    public static final String PARENT_ID = "chat_memory_parent_id";
    public static final String LAST_USER_MESSAGE_ID = "chat_memory_last_user_message_id";
    public static final String DISABLE_DB_WRITE = "disable_db_write";
}
