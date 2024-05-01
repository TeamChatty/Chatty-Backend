package com.chatty.dto.block.response;

import com.chatty.entity.block.Block;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BlockListResponse {

    private Long blockId;

    private Long userId;

    private String nickname;

    private String imageUrl;

    private LocalDateTime registeredDateTime;

    @Builder
    public BlockListResponse(final Long blockId, final Long userId, final String nickname, final String imageUrl, final LocalDateTime registeredDateTime) {
        this.blockId = blockId;
        this.userId = userId;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.registeredDateTime = registeredDateTime;
    }

    public static BlockListResponse of(final Block block) {
        return BlockListResponse.builder()
                .blockId(block.getId())
                .userId(block.getBlocked().getId())
                .nickname(block.getBlocked().getNickname())
                .imageUrl(block.getBlocked().getImageUrl())
                .registeredDateTime(block.getCreatedAt())
                .build();
    }
}
