package com.chatty.service.comment;

import com.chatty.dto.comment.request.CommentCreateRequest;
import com.chatty.dto.comment.response.CommentResponse;
import com.chatty.entity.comment.Comment;
import com.chatty.entity.post.Post;
import com.chatty.entity.user.User;
import com.chatty.repository.comment.CommentRepository;
import com.chatty.repository.post.PostRepository;
import com.chatty.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse createComment(final Long postId, final CommentCreateRequest request, final String mobileNumber) {
        Post post = postRepository.getById(postId);

        User user = userRepository.getByMobileNumber(mobileNumber);

        Comment comment = commentRepository.save(request.toEntity(post, user));
        return CommentResponse.of(comment, post, user);
    }
}
