package com.chatty.service.block;

import com.chatty.constants.Code;
import com.chatty.dto.block.response.BlockResponse;
import com.chatty.entity.block.Block;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.block.BlockRepository;
import com.chatty.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BlockService {

    private final BlockRepository blockRepository;
    private final UserRepository userRepository;

    @Transactional
    public BlockResponse createBlock(final Long userId, final String mobileNumber) {
        User blocker = userRepository.getByMobileNumber(mobileNumber);

        User blocked = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));

        if (blockRepository.existsByBlockerIdAndBlockedId(blocker.getId(), blocked.getId())) {
            throw new CustomException(Code.ALREADY_BLOCK_USER);
        }

        Block block = Block.create(blocker, blocked);
        Block savedBlock = blockRepository.save(block);

        return BlockResponse.of(savedBlock);
    }
}
