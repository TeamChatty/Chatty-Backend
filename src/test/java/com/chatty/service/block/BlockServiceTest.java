package com.chatty.service.block;

import com.chatty.constants.Authority;
import com.chatty.dto.block.response.BlockResponse;
import com.chatty.entity.user.Coordinate;
import com.chatty.entity.user.Gender;
import com.chatty.entity.user.User;
import com.chatty.repository.block.BlockRepository;
import com.chatty.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BlockServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private BlockService blockService;

    @AfterEach
    void tearDown() {
        blockRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("유저를 차단합니다.")
    @Test
    void createBlock() {
        // given
        User blocker = createUser("박지성", "01012345678");
        User blocked = createUser("강혜원", "01011112222");
        userRepository.saveAll(List.of(blocker, blocked));

        // when
        BlockResponse blockResponse = blockService.createBlock(blocked.getId(), blocker.getMobileNumber());

        // then
        assertThat(blockResponse.getBlockId()).isNotNull();
        assertThat(blockResponse)
                .extracting("blockerId", "blockedId")
                .containsExactly(
                        blocker.getId(), blocked.getId()
                );
    }


    private User createUser(final String nickname, final String mobileNumber) {
        return User.builder()
                .mobileNumber(mobileNumber)
                .deviceId("123456")
                .authority(Authority.USER)
//                .mbti(Mbti.ENFJ)
                .birth(LocalDate.now())
                .imageUrl("이미지")
                .address("주소")
                .gender(Gender.MALE)
                .nickname(nickname)
                .location(User.createPoint(
                        Coordinate.builder()
                                .lat(37.1)
                                .lng(127.1)
                                .build()
                ))
                .build();
    }
}