package com.chatty.entity.chat;

import com.chatty.entity.BaseTimeEntity;
import com.chatty.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> chatMessages = new ArrayList<>();

    private boolean isExtend;

    @Builder
    public ChatRoom(final User sender, final User receiver, final List<ChatMessage> chatMessages, final boolean isExtend) {
        this.sender = sender;
        this.receiver = receiver;
        this.chatMessages = chatMessages;
        this.isExtend = isExtend;
    }

    public void updateExtend(final boolean extend) {
        this.isExtend = extend;
    }
}
