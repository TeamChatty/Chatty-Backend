package com.chatty.controller.chat;
import com.chatty.dto.ApiResponse;
import com.chatty.dto.chat.request.ChatMessageRequest;
import com.chatty.dto.chat.response.ChatMessageListResponse;
import com.chatty.dto.chat.response.SimpleMessageResponseDto;
import com.chatty.service.chat.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final ChatService chatService;

    @MessageMapping(value = "/chat/message/{roomId}")
    @SendTo(value = "/sub/chat/{roomId}")
    public ApiResponse<SimpleMessageResponseDto> message(@DestinationVariable Long roomId,
                                                         @Valid @RequestBody final ChatMessageRequest request,
                                                         SimpMessageHeaderAccessor accessor){
        log.info("메세지 전송");
        LocalDateTime now = LocalDateTime.now();
        String mobileNumber = accessor.getSessionAttributes().get("mobileNumber").toString();
        log.info("보내는 사람 mobileNumber = {}", mobileNumber);
        return ApiResponse.ok(chatService.saveMessage(roomId, request, mobileNumber, now));
    }

//    @MessageMapping(value = "/chat/message/{roomId}")
//    @SendTo(value = "/sub/chat/{roomId}")
//    public ApiResponse<SimpleMessageResponseDto> message(@DestinationVariable Long roomId,
//                                                         @Valid @RequestBody final ChatMessageRequest request,
//                                                         Authentication authentication){
//        log.info("메세지 전송");
//        LocalDateTime now = LocalDateTime.now();
//        return ApiResponse.ok(chatService.saveMessage(roomId, request, authentication.getName(), now));
//    }

//    @MessageMapping(value = "/chat/message/{roomId}")
//    @SendTo(value = "/sub/chat/{roomId}")
//    public ApiResponse<SimpleMessageResponseDto> message(@DestinationVariable Long roomId, MessageDto messageDto){
//        log.info("메세지 전송");
//        return ApiResponse.ok(chatService.saveMessage(roomId, messageDto));
//    }

//    @MessageMapping(value = "/readMessage/{messageId}")
//    public ApiResponse<MessageMarkResponseDto> readMessage(@DestinationVariable Long messageId){
//        log.info("메시지 읽음 처리 - 아이디 : {}",messageId);
//        return ApiResponse.ok(chatService.markMessageAsRead(messageId));
//    }

    @PutMapping("/chat/messages/acknowledge/{roomId}")
    public void acknowledge(@PathVariable Long roomId,
                            Authentication authentication) {
        log.info("상대방이 보낸 메세지를 전부 읽었습니다.");
        chatService.acknowledge(roomId, authentication.getName());
    }

//    @Operation(summary = "메시지 가져오기", description = "읽지 않은 메시지를 가져옵니다.")
//    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원가입 실패",
//            content = @Content(mediaType = "application/json",
//                    examples = {
//                            @ExampleObject(name = "E-001", value = """
//                                    {
//                                        "errorCode": "001",
//                                        "status": "400",
//                                        "message": "accessToken 유효성 검증을 실패했습니다."
//                                    }
//                                    """),
//                            @ExampleObject(name = "E-003", value = """
//                                    {
//                                        "errorCode": "003",
//                                        "status": "400",
//                                        "message": "존재하지 않는 유저 입니다."
//                                    }
//                                    """),
//                            @ExampleObject(name = "E-011", value = """
//                                    {
//                                        "errorCode": "011",
//                                        "status": "400",
//                                        "message": "채팅방이 존재하지 않습니다."
//                                    }
//                                    """),
//                    }
//            )
//    )
//    @PostMapping("/chat/messages")
//    public ApiResponse<MultipleMessageResponseDto> getMessages(@RequestBody UnreadMessageDto unreadMessageDto){
//        log.info("메시지 가져오기");
//        return ApiResponse.ok(chatService.getMessages(unreadMessageDto));
//    }

    @GetMapping("/chat/messages/{roomId}")
    public ApiResponse<List<ChatMessageListResponse>> getMessages(@PathVariable Long roomId,
                                                                  Authentication authentication) {
        log.info("채팅방 접속하여 특정 채팅방의 모든 메세지 불러오기");
        return ApiResponse.ok(chatService.getMessages(roomId, authentication.getName()));
    }
}
