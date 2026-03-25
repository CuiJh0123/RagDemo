package cn.jiahao.minimumragdemo.Controller;

import cn.jiahao.minimumragdemo.AiService.IAskService;
import cn.jiahao.minimumragdemo.DTO.ChatRequestDTO;
import cn.jiahao.minimumragdemo.DTO.ChatResponseDTO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    @Resource
    private IAskService askService;
    @RequestMapping("/chat")
    public ChatResponseDTO chat(@RequestBody ChatRequestDTO chatRequestDTO){
        String chat = askService.chat(chatRequestDTO.getQuestion());
        return new ChatResponseDTO(chat);
    }
}
