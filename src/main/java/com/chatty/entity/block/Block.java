package com.chatty.entity.block;

import com.chatty.entity.BaseTimeEntity;
import com.chatty.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Block extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "block_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id")
    private User blocker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id")
    private User blocked;

    @Builder
    public Block(final User blocker, final User blocked) {
        this.blocker = blocker;
        this.blocked = blocked;
    }

    public static Block create(final User blocker, final User blocked) {
        return Block.builder()
                .blocker(blocker)
                .blocked(blocked)
                .build();
    }
}
