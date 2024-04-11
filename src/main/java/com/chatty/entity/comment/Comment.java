package com.chatty.entity.comment;

import com.chatty.entity.BaseTimeEntity;
import com.chatty.entity.post.Post;
import com.chatty.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "update Comment SET deleted_at = current_timestamp where comment_id = ?")
//@SQLRestriction("deleted_at is null") // 왜 에러가 발생하지?
@Getter
@Entity
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private List<Comment> children = new ArrayList<>();

    @Builder
    public Comment(final Post post, final User user, final String content, final Comment parent, final List<Comment> children) {
        this.post = post;
        this.user = user;
        this.content = content;
        this.parent = parent;
        this.children = children;
    }
}
