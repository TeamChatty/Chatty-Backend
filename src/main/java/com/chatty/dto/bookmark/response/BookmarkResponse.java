package com.chatty.dto.bookmark.response;

import com.chatty.entity.bookmark.Bookmark;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BookmarkResponse {

    private Long bookmarkId;

    private Long postId;

    private Long writerId;

    private Long userId;

    @Builder
    public BookmarkResponse(final Long bookmarkId, final Long postId, final Long writerId, final Long userId) {
        this.bookmarkId = bookmarkId;
        this.postId = postId;
        this.writerId = writerId;
        this.userId = userId;
    }

    public static BookmarkResponse of(final Bookmark bookmark) {
        return BookmarkResponse.builder()
                .bookmarkId(bookmark.getId())
                .postId(bookmark.getPost().getId())
                .writerId(bookmark.getPost().getUser().getId())
                .userId(bookmark.getUser().getId())
                .build();
    }
}
