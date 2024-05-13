package com.chatty.service.block;

import com.chatty.constants.Authority;
import com.chatty.dto.block.response.BlockListResponse;
import com.chatty.dto.block.response.BlockResponse;
import com.chatty.entity.block.Block;
import com.chatty.entity.chat.ChatRoom;
import com.chatty.entity.user.Coordinate;
import com.chatty.entity.user.Gender;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.block.BlockRepository;
import com.chatty.repository.chat.ChatRoomRepository;
import com.chatty.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class BlockServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private BlockService blockService;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @AfterEach
    void tearDown() {
        chatRoomRepository.deleteAllInBatch();
        blockRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("유저를 차단합니다.")
    @Test
    void createBlock() {
        // given
        User blocker = createUser("박지성", "01012345678", "이미지");
        User blocked = createUser("강혜원", "01011112222", "이미지");
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

    @DisplayName("유저를 차단할 때, 이미 차단한 유저면 예외가 발생한다.")
    @Test
    void createBlockWithDuplicateBlock() {
        // given
        User blocker = createUser("박지성", "01012345678", "이미지");
        User blocked = createUser("강혜원", "01011112222", "이미지");
        userRepository.saveAll(List.of(blocker, blocked));

        Block block = createBlock(blocker, blocked);
        blockRepository.save(block);

        // when // then
        assertThatThrownBy(() -> blockService.createBlock(blocked.getId(), blocker.getMobileNumber()))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 차단한 유저입니다.");

    }

    @DisplayName("유저를 차단할 때, 채팅방이 존재하면 채팅방을 삭제한다.")
    @Test
    void createBlockWithChatRoomDelete() {
        // given
        User blocker = createUser("박지성", "01012345678", "이미지");
        User blocked = createUser("강혜원", "01011112222", "이미지");
        userRepository.saveAll(List.of(blocker, blocked));

        ChatRoom chatRoom = createChatRoom(blocker, blocked);
        chatRoomRepository.save(chatRoom);

        // when
        BlockResponse blockResponse = blockService.createBlock(blocked.getId(), blocker.getMobileNumber());

        // then
        assertThat(blockResponse.getBlockId()).isNotNull();
        assertThat(blockResponse)
                .extracting("blockerId", "blockedId")
                .containsExactly(
                        blocker.getId(), blocked.getId()
                );

        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        assertThat(chatRooms).hasSize(0);
    }

    @DisplayName("차단한 유저 목록을 불러온다.")
    @Test
    void getBlockList() {
        // given
        User user = createUser("박지성", "01012345678", "profile.jpg");
        User blocked1 = createUser("강혜원", "01011112222", "profile1.jpg");
        User blocked2 = createUser("김연아", "01022223333", "profile2.jpg");
        User blocked3 = createUser("손연재", "01033334444", "profile3.jpg");
        userRepository.saveAll(List.of(user, blocked1, blocked2, blocked3));

        Block block1 = createBlock(user, blocked1);
        Block block2 = createBlock(user, blocked2);
        Block block3 = createBlock(user, blocked3);
        blockRepository.saveAll(List.of(block1, block2, block3));

        // when
        List<BlockListResponse> blockListResponse = blockService.getBlockList(user.getMobileNumber());

        // then
        assertThat(blockListResponse).hasSize(3);
        assertThat(blockListResponse)
                .extracting("userId", "nickname", "imageUrl")
                .containsExactlyInAnyOrder(
                        tuple(blocked1.getId(), "강혜원", "profile1.jpg"),
                        tuple(blocked2.getId(), "김연아", "profile2.jpg"),
                        tuple(blocked3.getId(), "손연재", "profile3.jpg")
                );

    }


    private User createUser(final String nickname, final String mobileNumber, final String imageUrl) {
        return User.builder()
                .mobileNumber(mobileNumber)
                .deviceId("123456")
                .authority(Authority.USER)
//                .mbti(Mbti.ENFJ)
                .birth(LocalDate.now())
                .imageUrl(imageUrl)
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

    private Block createBlock(final User blocker, final User blocked) {
        return Block.builder()
                .blocker(blocker)
                .blocked(blocked)
                .build();
    }

    private ChatRoom createChatRoom(final User sender, final User receiver) {
        return ChatRoom.builder()
                .sender(sender)
                .receiver(receiver)
                .build();
    }
}