package com.chatty.entity.bookmark;

import com.chatty.entity.BaseTimeEntity;
import com.chatty.entity.post.Post;
import com.chatty.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Bookmark extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Builder
    public Bookmark(final Post post, final User user) {
        this.post = post;
        this.user = user;
    }

    public static Bookmark create(final Post post, final User user) {
        return Bookmark.builder()
                .post(post)
                .user(user)
                .build();
    }
}
