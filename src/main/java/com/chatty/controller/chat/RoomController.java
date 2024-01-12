package com.chatty.controller.chat;

import com.chatty.dto.ApiResponse;
import com.chatty.dto.chat.request.DeleteRoomDto;
import com.chatty.dto.chat.request.RoomDto;
import com.chatty.dto.chat.response.RoomResponseDto;
import com.chatty.service.chat.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("chat")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "채팅방 생성", description = "채팅방을 생성해줍니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원가입 실패",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "E-004", value = """
                                    {
                                        "errorCode": "004",
                                        "status": "400",
                                        "message": "존재하지 않는 유저 입니다."
                                    }
                                    """),
                            @ExampleObject(name = "E-007", value = """
                                    {
                                        "errorCode": "007",
                                        "status": "400",
                                        "message": "accessToken 유효성 검증을 실패했습니다."
                                    }
                                    """),
                            @ExampleObject(name = "E-012", value = """
                                    {
                                        "errorCode": "012",
                                        "status": "400",
                                        "message": "채팅방이 이미 존재합니다."
                                    }
                                    """)
                    }
            )
    )
    @PostMapping("/room")
    public ApiResponse<RoomResponseDto> createRoom(@Valid @RequestBody RoomDto roomDto){
        log.info("채팅방 생성");
        return ApiResponse.ok(roomService.createRoom(roomDto));
    }

    @Operation(summary = "채팅방 삭제", description = "채팅방을 삭제해줍니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원가입 실패",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "E-007", value = """
                                    {
                                        "errorCode": "007",
                                        "status": "400",
                                        "message": "accessToken 유효성 검증을 실패했습니다."
                                    }
                                    """),
                            @ExampleObject(name = "E-011", value = """
                                    {
                                        "errorCode": "011",
                                        "status": "400",
                                        "message": "채팅방이 존재하지 않습니다."
                                    }
                                    """),
                    }
            )
    )
    @DeleteMapping("/room")
    public ApiResponse<RoomResponseDto> deleteRoom(@Valid @RequestBody DeleteRoomDto deleteRoomDto){
        log.info("채팅방 삭제");
        return ApiResponse.ok(roomService.deleteRoom(deleteRoomDto));
    }

    @Operation(summary = "채팅방 찾기", description = "채팅방을 찾아 채팅방에 대한 정보를 획득합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원가입 실패",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "E-007", value = """
                                    {
                                        "errorCode": "007",
                                        "status": "400",
                                        "message": "accessToken 유효성 검증을 실패했습니다."
                                    }
                                    """),
                            @ExampleObject(name = "E-011", value = """
                                    {
                                        "errorCode": "011",
                                        "status": "400",
                                        "message": "채팅방이 존재하지 않습니다."
                                    }
                                    """),
                    }
            )
    )
    @GetMapping("/room/{roomId}")
    public ApiResponse<RoomResponseDto> getRoom(@PathVariable Long roomId){
        log.info("채팅방 찾기");
        return ApiResponse.ok(roomService.findChatRoom(roomId));
    }
}
