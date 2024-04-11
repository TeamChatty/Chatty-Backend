package com.chatty.service.comment;

import com.chatty.dto.comment.request.CommentCreateRequest;
import com.chatty.dto.comment.request.CommentReplyCreateRequest;
import com.chatty.dto.comment.response.CommentListResponse;
import com.chatty.dto.comment.response.CommentReplyListResponse;
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

import java.util.List;
import java.util.stream.Collectors;

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

    @Transactional
    public CommentResponse createCommentReply(final Long postId, final Long commentId, final CommentReplyCreateRequest request, final String mobileNumber) {
        Post post = postRepository.getById(postId);

        User user = userRepository.getByMobileNumber(mobileNumber);

        Comment parent = commentRepository.getById(commentId);

        Comment comment = commentRepository.save(request.toEntity(post, user, parent));
        return CommentResponse.of(comment, post, user);
    }

    public List<CommentListResponse> getCommentList(final Long postId, final String mobileNumber) {
//        Post post = postRepository.getById(postId);

//        User user = userRepository.getByMobileNumber(mobileNumber);

        List<Comment> result = commentRepository.findAllByPostIdAndParentIsNullOrderByIdDesc(postId);

        return result.stream()
                .map(CommentListResponse::of)
                .collect(Collectors.toList());
    }

    public List<CommentReplyListResponse> getCommentReplyList(final Long postId, final Long commentId, final String mobileNumber) {
//        Post post = postRepository.getById(postId);

//        User user = userRepository.getByMobileNumber(mobileNumber);

//        Comment comment = commentRepository.getById(commentId);

        List<Comment> result = commentRepository.findAllByParentIdOrderByIdAsc(commentId);

        return result.stream()
                .map(CommentReplyListResponse::of)
                .collect(Collectors.toList());
    }
}
