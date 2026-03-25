package cn.jiahao.minimumragdemo.Controller;

import cn.jiahao.minimumragdemo.AiService.IAskService;
import cn.jiahao.minimumragdemo.AiService.IAskServiceWithoutRAG;
import cn.jiahao.minimumragdemo.DTO.ChatRequestDTO;
import cn.jiahao.minimumragdemo.DTO.ChatResponseDTO;
import dev.langchain4j.model.chat.request.ChatRequest;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    @Resource
    private IAskService askService;
    @Resource
    private IAskServiceWithoutRAG askServiceWithoutRAG;
    @RequestMapping("/chat")
    public ChatResponseDTO chat(@RequestBody ChatRequestDTO chatRequestDTO){
        log.info("进入RAG检索增强接口 /chat");
        String chat = askService.chat(chatRequestDTO.getQuestion());
        return new ChatResponseDTO(chat);
    }
    @RequestMapping("without")
    public ChatResponseDTO reply(@RequestBody ChatRequestDTO chatRequestDTO){
        log.info("进入普通接口 /without");
        String chat = askServiceWithoutRAG.chat(chatRequestDTO.getQuestion());
        return new ChatResponseDTO(chat);
    }
}
