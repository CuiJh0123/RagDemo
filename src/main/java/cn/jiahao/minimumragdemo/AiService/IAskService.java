package cn.jiahao.minimumragdemo.AiService;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService(chatModel = "chat-model")
public interface IAskService {
    @SystemMessage(fromResource = "prompts/SystemPrompts.md")
    public String chat(@UserMessage String message);
}
