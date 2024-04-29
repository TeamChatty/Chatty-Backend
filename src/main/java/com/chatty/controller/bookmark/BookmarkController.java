package com.chatty.controller.bookmark;

import com.chatty.dto.ApiResponse;
import com.chatty.dto.bookmark.response.BookmarkListResponse;
import com.chatty.dto.bookmark.response.BookmarkResponse;
import com.chatty.service.bookmark.BookmarkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/v1/{postId}/bookmark")
    public ApiResponse<BookmarkResponse> createBookmark(@PathVariable Long postId,
                                                        Authentication authentication) {
        return ApiResponse.ok(bookmarkService.createBookmark(postId, authentication.getName()));
    }

    @DeleteMapping("/v1/{postId}/bookmark")
    public ApiResponse<BookmarkResponse> deleteBookmark(@PathVariable Long postId,
                                                        Authentication authentication) {
        return ApiResponse.ok(bookmarkService.deleteBookmark(postId, authentication.getName()));
    }

    @GetMapping("/v1/my-bookmarks")
    public ApiResponse<List<BookmarkListResponse>> getMyBookmarkListPages(@RequestParam Long lastBookmarkId,
                                                                                   @RequestParam int size,
                                                                                   Authentication authentication) {
        return ApiResponse.ok(bookmarkService.getMyBookmarkListPages(lastBookmarkId, size, authentication.getName()));
    }
}
