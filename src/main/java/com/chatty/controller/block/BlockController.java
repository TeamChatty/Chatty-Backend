package com.chatty.controller.block;

import com.chatty.dto.ApiResponse;
import com.chatty.dto.block.response.BlockResponse;
import com.chatty.service.block.BlockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BlockController {

    private final BlockService blockService;

    @PostMapping("/v1/block/{userId}")
    public ApiResponse<BlockResponse> createBlock(@PathVariable Long userId,
                                                  Authentication authentication) {
        return ApiResponse.ok(blockService.createBlock(userId, authentication.getName()));
    }

}
