package com.chatty.service.chat;

import com.chatty.constants.Authority;
import com.chatty.dto.chat.request.ChatRoomCreateRequest;
import com.chatty.dto.chat.request.ChatRoomUpdateExtendRequest;
import com.chatty.dto.chat.response.ChatRoomListResponse;
import com.chatty.dto.chat.response.ChatRoomResponse;
import com.chatty.entity.chat.ChatMessage;
import com.chatty.entity.chat.ChatRoom;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.chat.ChatRoomRepository;
import com.chatty.repository.chat.MessageRepository;
import com.chatty.repository.user.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ChatRoomServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomService chatRoomService;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository chatMessageRepository;

    @AfterEach
    void tearDown() {
        chatMessageRepository.deleteAllInBatch();
        chatRoomRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("채팅방 목록을 조회한다. '채팅방만 생성되었을 때'")
    @Test
    void getRoomList() {
        // given
        User user1 = createUser("박지성", "01011112222", "profile1.jpg", true, 5, 7);
        User user2 = createUser("김연아", "01012345678", "profile2.jpg", true, 5, 7);
        userRepository.saveAll(List.of(user1, user2));

        ChatRoom chatRoom = createChatRoomWithMatching(user1, user2);
        chatRoomRepository.save(chatRoom);

        // when
        List<ChatRoomListResponse> roomList = chatRoomService.getChatRoomList(user1.getMobileNumber());

        // then
        assertThat(roomList).hasSize(1)
                .extracting("senderId", "senderNickname", "senderImageUrl", "blueCheck")
                .containsExactlyInAnyOrder(
                        tuple(user2.getId(), "김연아", "profile2.jpg", true)
                );
    }

    /** user1 을 기준으로 조회하기 때문에, 채팅방 목록에는 user2 닉네임, 사진이 뜨는게 맞다. */
    @DisplayName("채팅방 목록을 조회한다. '채팅 내역이 존재할 때 마지막 메세지, 시간, 읽지 않은 개수도 같이 조회한다.'")
    @Test
    void getRoomListWithLastMessageAndTimeAndUnreadCount() {
        // given
        User user1 = createUser("박지성", "01011112222", "profile1.jpg", true, 5, 7);
        User user2 = createUser("김연아", "01012345678", "profile2.jpg", true, 5, 7);
        userRepository.saveAll(List.of(user1, user2));

        ChatRoom chatRoom = createChatRoomWithMatching(user1, user2);
        chatRoomRepository.save(chatRoom);

        final LocalDateTime now = LocalDateTime.now();
        ChatMessage message1 = createChatMessage(chatRoom, "안녕하세요1", user2, user1, now);
        ChatMessage message2 = createChatMessage(chatRoom, "반가워요2", user2, user1, now);
        ChatMessage message3 = createChatMessage(chatRoom, "잘있어요3", user2, user1, now);
        ChatMessage message4 = createChatMessage(chatRoom, "다시만나요4", user2, user1, now);
        chatMessageRepository.saveAll(List.of(message1, message2, message3, message4));

        // when
        List<ChatRoomListResponse> roomList = chatRoomService.getChatRoomList(user1.getMobileNumber());

        // then
        assertThat(roomList).hasSize(1)
                .extracting("senderId", "senderNickname", "senderImageUrl", "blueCheck", "lastMessage", "unreadMessageCount")
                .containsExactlyInAnyOrder(
                        tuple(user2.getId(), "김연아", "profile2.jpg", true, "다시만나요4", 4)
                );
    }

    @DisplayName("채팅방을 생성한다.")
    @Test
    void createChatRoomWithMatching() {
        // given
        User sender = createUser("박지성", "01011112222", "profile1.jpg", true, 5, 7);
        User receiver = createUser("김연아", "01012345678", "profile2.jpg", true, 5, 7);
        userRepository.saveAll(List.of(sender, receiver));

        ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
                .receiverId(receiver.getId())
                .build();

        // when
        ChatRoomResponse chatRoomResponse = chatRoomService.createRoom(request, sender.getMobileNumber());

        // then
        assertThat(chatRoomResponse.getRoomId()).isNotNull();
        assertThat(chatRoomResponse)
                .extracting("roomId", "senderId", "receiverId", "extend")
                .containsExactlyInAnyOrder(chatRoomResponse.getRoomId(), sender.getId(), receiver.getId(), false);

    }

    @DisplayName("채팅방을 생성할 때, user1이 user2에게 신청하여 만든 채팅방이 존재하면 예외가 발생하는 시나리오")
    @TestFactory
    Collection<DynamicTest> createChatRoomWithExistChatRoomWithMatching() {
        // given
        User user1 = createUser("박지성", "01011112222", "profile1.jpg", true, 5, 7);
        User user2 = createUser("김연아", "01012345678", "profile2.jpg", true, 5, 7);
        userRepository.saveAll(List.of(user1, user2));

        ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
                .receiverId(user2.getId())
                .build();
        chatRoomRepository.save(request.toEntity(user1, user2));

        return List.of(
                DynamicTest.dynamicTest("user1이 채팅방을 생성할 때, 이미 존재하기 때문에 예외가 발생한다.", () -> {
                    // given
                    ChatRoomCreateRequest userRequest = ChatRoomCreateRequest.builder()
                            .receiverId(user2.getId())
                            .build();

                    // when // then
                    assertThatThrownBy(() -> chatRoomService.createRoom(userRequest, user1.getMobileNumber()))
                            .isInstanceOf(CustomException.class)
                            .hasMessage("채팅방이 이미 존재합니다.");
                }),

                DynamicTest.dynamicTest("user2가 채팅방을 생성할 때, 이미 존재하기 때문에 예외가 발생한다.", () -> {
                    // given
                    ChatRoomCreateRequest userRequest = ChatRoomCreateRequest.builder()
                            .receiverId(user1.getId())
                            .build();

                    // when // then
                    assertThatThrownBy(() -> chatRoomService.createRoom(userRequest, user2.getMobileNumber()))
                            .isInstanceOf(CustomException.class)
                            .hasMessage("채팅방이 이미 존재합니다.");
                })
        );
    }

    @DisplayName("채팅방을 티켓을 사용하여 연장한다.")
    @Test
    void updateChatRoomExtendWithTicket() {
        // given
        User sender = createUser("박지성", "01011112222", "profile1.jpg", true, 5, 7);
        User receiver = createUser("김연아", "01012345678", "profile2.jpg", true, 5, 7);
        userRepository.saveAll(List.of(sender, receiver));

        ChatRoom chatRoom = createChatRoomWithMatching(sender, receiver);
        chatRoomRepository.save(chatRoom);

        ChatRoomUpdateExtendRequest request = ChatRoomUpdateExtendRequest.builder()
                .unlockMethod("ticket")
                .build();

        // when
        ChatRoomResponse chatRoomResponse = chatRoomService.updateRoomExtend(chatRoom.getRoomId(), request, sender.getMobileNumber());

        // then
        assertThat(chatRoomResponse.getRoomId()).isNotNull();
        assertThat(chatRoomResponse)
                .extracting("roomId", "senderId", "receiverId", "extend")
                .containsExactlyInAnyOrder(chatRoomResponse.getRoomId(), sender.getId(), receiver.getId(), true);
    }

    @DisplayName("채팅방을 캔디를 사용하여 연장한다.")
    @Test
    void updateChatRoomExtendWithCandy() {
        // given
        User sender = createUser("박지성", "01011112222", "profile1.jpg", true, 5, 7);
        User receiver = createUser("김연아", "01012345678", "profile2.jpg", true, 5, 7);
        userRepository.saveAll(List.of(sender, receiver));

        ChatRoom chatRoom = createChatRoomWithMatching(sender, receiver);
        chatRoomRepository.save(chatRoom);

        ChatRoomUpdateExtendRequest request = ChatRoomUpdateExtendRequest.builder()
                .unlockMethod("candy")
                .build();

        // when
        ChatRoomResponse chatRoomResponse = chatRoomService.updateRoomExtend(chatRoom.getRoomId(), request, sender.getMobileNumber());

        // then
        assertThat(chatRoomResponse.getRoomId()).isNotNull();
        assertThat(chatRoomResponse)
                .extracting("roomId", "senderId", "receiverId", "extend")
                .containsExactlyInAnyOrder(chatRoomResponse.getRoomId(), sender.getId(), receiver.getId(), true);
    }

    @DisplayName("채팅방을 티켓을 사용하여 연장할 때, 티켓 개수(1개)가 부족하면 예외가 발생한다.")
    @Test
    void updateChatRoomExtendWithoutCandy() {
        // given
        User sender = createUser("박지성", "01011112222", "profile1.jpg", true, 0, 7);
        User receiver = createUser("김연아", "01012345678", "profile2.jpg", true, 0, 7);
        userRepository.saveAll(List.of(sender, receiver));

        ChatRoom chatRoom = createChatRoomWithMatching(sender, receiver);
        chatRoomRepository.save(chatRoom);

        ChatRoomUpdateExtendRequest request = ChatRoomUpdateExtendRequest.builder()
                .unlockMethod("ticket")
                .build();

        // when // then
        assertThatThrownBy(() -> chatRoomService.updateRoomExtend(chatRoom.getRoomId(), request, sender.getMobileNumber()))
                .isInstanceOf(CustomException.class)
                .hasMessage("티켓의 개수가 부족합니다.");
    }

    @DisplayName("채팅방을 연장할 때, 본인이 속해있는 채팅방이 아니면 예외가 발생한다.")
    @Test
    void updateChatRoomExtendWithoutInChatRoom() {
        // given
        User sender = createUser("박지성", "01011112222", "profile1.jpg", true, 5, 7);
        User receiver = createUser("김연아", "01012345678", "profile2.jpg", true, 5, 7);

        User user3 = createUser("강혜원", "01022222222", "profile3.jpg", true, 5, 7);
        userRepository.saveAll(List.of(sender, receiver, user3));

        ChatRoom chatRoom = createChatRoomWithMatching(sender, receiver);
        chatRoomRepository.save(chatRoom);

        ChatRoomUpdateExtendRequest request = ChatRoomUpdateExtendRequest.builder()
                .unlockMethod("ticket")
                .build();

        // when // then
        assertThatThrownBy(() -> chatRoomService.updateRoomExtend(chatRoom.getRoomId(), request, user3.getMobileNumber()))
                .isInstanceOf(CustomException.class)
                .hasMessage("유저가 채팅방에 존재하지 않습니다.");
    }

    private User createUser(final String nickname, final String mobileNumber, final String imageUrl, final boolean blueCheck, final int ticket, final int candy) {
        return User.builder()
                .mobileNumber(mobileNumber)
                .ticket(ticket)
                .candy(candy)
                .nickname(nickname)
                .deviceId("123456")
                .authority(Authority.USER)
                .imageUrl(imageUrl)
                .blueCheck(blueCheck)
                .build();
    }

    private ChatRoom createChatRoomWithMatching(final User sender, final User receiver) {
        return ChatRoom.builder()
                .sender(sender)
                .receiver(receiver)
                .extend(false)
                .build();
    }

    private ChatMessage createChatMessage(final ChatRoom chatRoom, final String content, final User sender, final User receiver, final LocalDateTime sendTime) {
        return ChatMessage.builder()
                .chatRoom(chatRoom)
                .content(content)
                .sender(sender)
                .receiver(receiver)
                .isRead(false)
                .sendTime(sendTime)
                .build();
    }
}