package com.chatty.dto.block.response;

import com.chatty.entity.block.Block;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BlockResponse {

    private Long blockId;

    private Long blockerId;

    private Long blockedId;

    @Builder
    public BlockResponse(final Long blockId, final Long blockerId, final Long blockedId) {
        this.blockId = blockId;
        this.blockerId = blockerId;
        this.blockedId = blockedId;
    }

    public static BlockResponse of(final Block block) {
        return BlockResponse.builder()
                .blockId(block.getId())
                .blockerId(block.getBlocker().getId())
                .blockedId(block.getBlocked().getId())
                .build();
    }
}
