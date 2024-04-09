package com.chatty.repository.chat;

import com.chatty.entity.chat.ChatRoom;
import com.chatty.entity.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    Optional<ChatRoom> findChatRoomBySenderAndReceiver(User sender, User receiver);
    Optional<ChatRoom> findChatRoomByReceiverAndSender(User receiver, User sender);
    Optional<ChatRoom> findChatRoomByRoomId(Long roomId);
    List<ChatRoom> findAllBySender(User sender);
    List<ChatRoom> findAllBySenderOrReceiverOrderByChatMessagesSendTimeDesc(User sender, User receiver);

    Optional<ChatRoom> findChatRoomByRoomIdAndSenderOrReceiver(Long roomId, User sender, User receiver);
    boolean existsByRoomIdAndSenderOrReceiver(Long roomId, User sender, User receiver);

    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);

//    @Query(value = "select c.* " +
//            "from chat_room c " +
//            "where c.sender_id = :user or c.receiver_id = :user " +
//            "order by (" +
//            "select MAX(send_time) " +
//            "from chat_message m " +
//            "where m.room_id = c.room_Id " +
//            "and (m.sender_id = :user or m.receiver_id = :user) " +
//            ") desc", nativeQuery = true)
//    List<ChatRoom> findAllBySenderIdOrReceiverId(@Param("user") Long user);
}
