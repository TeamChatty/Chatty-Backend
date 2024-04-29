package com.chatty.repository.chat;

import com.chatty.entity.chat.ChatMessage;
import java.util.List;
import java.util.Optional;

import com.chatty.entity.chat.ChatRoom;
import com.chatty.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<ChatMessage, Long> {

    Optional<ChatMessage> findChatMessagesByMessageId(Long messageId);

    Optional<List<ChatMessage>> findByIsReadFalseAndChatRoomRoomIdOrderBySendTimeDesc(Long chatRoomId);

    List<ChatMessage> findAllByChatRoomOrderByMessageIdDesc(ChatRoom chatRoom);

    List<ChatMessage> findAllByChatRoomAndSenderNotAndIsReadIsFalse(ChatRoom chatRoom, User receiver);

    Page<ChatMessage> findByChatRoomAndMessageIdLessThanOrderByMessageIdDesc(ChatRoom chatRoom, Long messageId, Pageable pageable);
}
