package com.chatty.service.comment;

import com.chatty.constants.Authority;
import com.chatty.dto.comment.request.CommentCreateRequest;
import com.chatty.dto.comment.request.CommentReplyCreateRequest;
import com.chatty.dto.comment.response.CommentListResponse;
import com.chatty.dto.comment.response.CommentReplyListResponse;
import com.chatty.dto.comment.response.CommentResponse;
import com.chatty.entity.alarm.Alarm;
import com.chatty.entity.alarm.AlarmType;
import com.chatty.entity.block.Block;
import com.chatty.entity.comment.Comment;
import com.chatty.entity.like.CommentLike;
import com.chatty.entity.notification.NotificationReceive;
import com.chatty.entity.post.Post;
import com.chatty.entity.user.Coordinate;
import com.chatty.entity.user.Gender;
import com.chatty.entity.user.User;
import com.chatty.repository.alarm.AlarmRepository;
import com.chatty.repository.block.BlockRepository;
import com.chatty.repository.comment.CommentRepository;
import com.chatty.repository.like.CommentLikeRepository;
import com.chatty.repository.notification.NotificationReceiveRepository;
import com.chatty.repository.post.PostImageRepository;
import com.chatty.repository.post.PostRepository;
import com.chatty.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private NotificationReceiveRepository notificationReceiveRepository;

    @AfterEach
    void tearDown() {
        alarmRepository.deleteAllInBatch();
        blockRepository.deleteAllInBatch();
        commentLikeRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
        postRepository.deleteAllInBatch();
        notificationReceiveRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("댓글을 등록한다. 본인이 작성한 게시글에는 알람이 생기지 않는다.")
    @Test
    void createComment() {
        // given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        Post post = createPost("내용", user);
        postRepository.save(post);

        CommentCreateRequest request = CommentCreateRequest.builder()
                .content("댓글")
                .build();

        // when
        CommentResponse commentResponse = commentService.createComment(post.getId(), request, user.getMobileNumber());

        // then
        assertThat(commentResponse.getCommentId()).isNotNull();
        assertThat(commentResponse)
                .extracting("postId", "userId", "content")
                .containsExactlyInAnyOrder(post.getId(), user.getId(), request.getContent());

        List<Alarm> alarms = alarmRepository.findAll();
        assertThat(alarms).hasSize(0);
    }

    @DisplayName("댓글을 등록한다. 본인이 작성한 게시글이 아니면 작성자에게 알람이 생성된다.")
    @Test
    void createCommentWithAlarm() {
        // given
        User writer = createUser("박지성", "01012345678");
        User user = createUser("강혜원", "01011112222");
        userRepository.saveAll(List.of(writer, user));

        NotificationReceive notificationReceive = createNotificationReceive(writer, true, true, true);
        notificationReceiveRepository.save(notificationReceive);

        Post post = createPost("내용", writer);
        postRepository.save(post);

        CommentCreateRequest request = CommentCreateRequest.builder()
                .content("댓글")
                .build();

        // when
        CommentResponse commentResponse = commentService.createComment(post.getId(), request, user.getMobileNumber());

        // then
        assertThat(commentResponse.getCommentId()).isNotNull();
        assertThat(commentResponse)
                .extracting("postId", "userId", "content")
                .containsExactlyInAnyOrder(post.getId(), user.getId(), request.getContent());

        List<Alarm> alarms = alarmRepository.findAllByUserAndIsReadIsFalse(writer);
        assertThat(alarms).hasSize(1)
                .extracting("alarmType", "fromUser", "postId")
                .containsExactlyInAnyOrder(
                        tuple(AlarmType.FEED, user.getId(), post.getId()));
    }

    @DisplayName("대댓글(Reply)을 등록한다.")
    @Test
    void createCommentReply() {
        // given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        Post post = createPost("내용", user);
        postRepository.save(post);

        Comment comment = createComment(post, user, "내용", null);
        commentRepository.save(comment);

        CommentReplyCreateRequest request = CommentReplyCreateRequest.builder()
                .content("댓글")
                .build();

        // when
        CommentResponse commentResponse = commentService.createCommentReply(post.getId(), comment.getId(), request, user.getMobileNumber());

        // then
        assertThat(commentResponse.getCommentId()).isNotNull();
        assertThat(commentResponse)
                .extracting("postId", "userId", "content")
                .containsExactlyInAnyOrder(post.getId(), user.getId(), request.getContent());
    }

    @DisplayName("등록된 댓글 목록을 조회한다.")
    @Test
    void getCommentList() {
        // given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        Post post = createPost("내용", user);
        postRepository.save(post);

        Comment comment1 = createComment(post, user, "내용1", null);
        Comment comment2 = createComment(post, user, "내용2", null);
        Comment comment3 = createComment(post, user, "내용3", null);
        commentRepository.saveAll(List.of(comment1, comment2, comment3));

//        CommentReplyCreateRequest request = CommentReplyCreateRequest.builder()
//                .content("댓글")
//                .build();

        // when
        List<CommentListResponse> commentList = commentService.getCommentList(post.getId(), user.getMobileNumber());

        // then
        assertThat(commentList).hasSize(3);
        assertThat(commentList)
                .extracting("postId", "userId", "content", "childCount")
                .containsExactlyInAnyOrder(
                        tuple(post.getId(), user.getId(), "내용1", 0),
                        tuple(post.getId(), user.getId(), "내용2", 0),
                        tuple(post.getId(), user.getId(), "내용3", 0)
                );
    }

    @DisplayName("등록된 댓글 목록을 조회한다. 좋아요 개수 및 여부, 댓글 작성자 여부를 알 수 있다.")
    @Test
    void getCommentListWithIsLikeAndIsOwner() {
        // given
        User user = createUser("닉네임", "01012345678");
        User user2 = createUser("강혜원", "01011112222");
        userRepository.saveAll(List.of(user, user2));

        Post post = createPost("내용", user);
        postRepository.save(post);

        Comment comment1 = createComment(post, user, "내용1", null);
        Comment comment2 = createComment(post, user, "내용2", null);
        Comment comment3 = createComment(post, user, "내용3", null);
        Comment comment4 = createComment(post, user2, "내용4", null);
        Comment comment5 = createComment(post, user2, "내용5", null);
        commentRepository.saveAll(List.of(comment1, comment2, comment3, comment4, comment5));

        // when
        List<CommentListResponse> commentList = commentService.getCommentList(post.getId(), user.getMobileNumber());

        // then
        assertThat(commentList).hasSize(5);
        assertThat(commentList)
                .extracting("postId", "userId", "content", "childCount", "likeCount", "isLike", "isOwner")
                .containsExactlyInAnyOrder(
                        tuple(post.getId(), user.getId(), "내용1", 0, 0L, false, true),
                        tuple(post.getId(), user.getId(), "내용2", 0, 0L, false, true),
                        tuple(post.getId(), user.getId(), "내용3", 0, 0L, false, true),
                        tuple(post.getId(), user2.getId(), "내용4", 0, 0L, false, false),
                        tuple(post.getId(), user2.getId(), "내용5", 0, 0L, false, false)
                );
    }

    @DisplayName("등록된 댓글을 페이징 처리하여 조회한다. 마지막 댓글은4, 사이즈는 3이다.")
    @Test
    void getCommentListPages() {
        // given
        User user = createUser("박지성", "01012345678");
        User user2 = createUser("강혜원", "01011112222");
        userRepository.saveAll(List.of(user, user2));

        Post post = createPost("내용", user);
        postRepository.save(post);

        Comment comment1 = createComment(post, user, "내용1", null);
        Comment comment2 = createComment(post, user, "내용2", null);
        Comment comment3 = createComment(post, user2, "내용3", null);
        Comment comment4 = createComment(post, user2, "내용4", null);
        Comment comment5 = createComment(post, user2, "내용5", null);
        commentRepository.saveAll(List.of(comment1, comment2, comment3, comment4, comment5));

        // when
        List<CommentListResponse> commentList =
                commentService.getCommentListPages(post.getId(), comment4.getId(), 3, user.getMobileNumber());

        // then
        assertThat(commentList).hasSize(3);
        assertThat(commentList)
                .extracting("postId", "userId", "content", "childCount", "likeCount", "isLike", "isOwner")
                .containsExactly(
                        tuple(post.getId(), user2.getId(), "내용3", 0, 0L, false, false),
                        tuple(post.getId(), user.getId(), "내용2", 0, 0L, false, true),
                        tuple(post.getId(), user.getId(), "내용1", 0, 0L, false, true)
                );
    }

    @DisplayName("등록된 댓글을 페이징 처리하여 조회할 때, 차단한 사람의 댓글은 보이지 않는다.")
    @Test
    void getCommentListPagesWithoutBlockedUser() {
        // given
        User user = createUser("박지성", "01012345678");
        User user2 = createUser("강혜원", "01011112222");
        User user3 = createUser("김연아", "01033333333");
        User user4 = createUser("손흥민", "01044444444");
        User blocked = createUser("이강인", "01055555555");
        userRepository.saveAll(List.of(user, user2, user3, user4, blocked));

        Post post = createPost("내용", user);
        postRepository.save(post);

        Comment comment1 = createComment(post, user, "내용1", null);
        Comment comment2 = createComment(post, user2, "내용2", null);
        Comment comment3 = createComment(post, user3, "내용3", null);
        Comment comment4 = createComment(post, user4, "내용4", null);
        Comment comment5 = createComment(post, blocked, "내용5", null);
        commentRepository.saveAll(List.of(comment1, comment2, comment3, comment4, comment5));

        Block block = createBlock(user, blocked);
        blockRepository.save(block);

        // when
        List<CommentListResponse> commentList =
                commentService.getCommentListPages(post.getId(), comment5.getId() + 1L, 5, user.getMobileNumber());

        // then
        assertThat(commentList).hasSize(4);
        assertThat(commentList)
                .extracting("postId", "userId", "content", "childCount", "likeCount", "isLike", "isOwner")
                .containsExactly(
                        tuple(post.getId(), user4.getId(), "내용4", 0, 0L, false, false),
                        tuple(post.getId(), user3.getId(), "내용3", 0, 0L, false, false),
                        tuple(post.getId(), user2.getId(), "내용2", 0, 0L, false, false),
                        tuple(post.getId(), user.getId(), "내용1", 0, 0L, false, true)
                );
    }

    @DisplayName("등록된 댓글 목록을 조회할 때, 대댓글이 존재하면 개수만큼 childCount가 존재한다.")
    @Test
    void getCommentListWithChildCount() {
        // given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        Post post = createPost("내용", user);
        postRepository.save(post);

        Comment comment1 = createComment(post, user, "내용1", null);
        Comment comment2 = createComment(post, user, "내용2", null);
        Comment comment3 = createComment(post, user, "내용3", null);
        commentRepository.saveAll(List.of(comment1, comment2, comment3));

        CommentReplyCreateRequest request = CommentReplyCreateRequest.builder()
                .content("댓글")
                .build();
        commentRepository.save(request.toEntity(post, user, comment1));

        // when
        List<CommentListResponse> commentList = commentService.getCommentList(post.getId(), user.getMobileNumber());

        // then
        assertThat(commentList).hasSize(3);
        assertThat(commentList)
                .extracting("postId", "userId", "content", "childCount")
                .containsExactlyInAnyOrder(
                        tuple(post.getId(), user.getId(), "내용1", 1),
                        tuple(post.getId(), user.getId(), "내용2", 0),
                        tuple(post.getId(), user.getId(), "내용3", 0)
                );
    }

    @DisplayName("등록된 댓글의 대댓글 목록을 조회한다.")
    @Test
    void getCommentReplyList() {
        // given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        Post post = createPost("내용", user);
        postRepository.save(post);

        Comment comment = createComment(post, user, "내용1", null);
        commentRepository.save(comment);

        Comment reply1 = createComment(post, user, "대댓글1", comment);
        Comment reply2 = createComment(post, user, "대댓글2", comment);
        commentRepository.saveAll(List.of(reply1, reply2));

        // when
        List<CommentReplyListResponse> commentReplyList =
                commentService.getCommentReplyList(post.getId(), comment.getId(), user.getMobileNumber());

        // then
        assertThat(commentReplyList).hasSize(2);
        assertThat(commentReplyList)
                .extracting("postId", "userId", "commentId", "content", "parentId")
                .containsExactlyInAnyOrder(
                        tuple(post.getId(), user.getId(), reply1.getId(), "대댓글1", comment.getId()),
                        tuple(post.getId(), user.getId(), reply2.getId(), "대댓글2", comment.getId())
                );
    }

    @DisplayName("등록된 댓글의 대댓글 목록을 조회한다. 좋아요 개수 및 여부, 작성자 여부를 알 수 있다.")
    @Test
    void getCommentReplyListWithIsLikeAndIsOwner() {
        // given
        User user = createUser("박지성", "01012345678");
        User user2 = createUser("강혜원", "01011112222");
        userRepository.saveAll(List.of(user, user2));

        Post post = createPost("내용", user);
        postRepository.save(post);

        Comment comment = createComment(post, user, "내용1", null);
        commentRepository.save(comment);

        Comment reply1 = createComment(post, user, "대댓글1", comment);
        Comment reply2 = createComment(post, user2, "대댓글2", comment);
        commentRepository.saveAll(List.of(reply1, reply2));

        // when
        List<CommentReplyListResponse> commentReplyList =
                commentService.getCommentReplyList(post.getId(), comment.getId(), user.getMobileNumber());

        // then
        assertThat(commentReplyList).hasSize(2);
        assertThat(commentReplyList)
                .extracting("postId", "userId", "commentId", "content", "parentId", "likeCount", "isLike", "isOwner")
                .containsExactlyInAnyOrder(
                        tuple(post.getId(), user.getId(), reply1.getId(), "대댓글1", comment.getId(), 0L, false, true),
                        tuple(post.getId(), user2.getId(), reply2.getId(), "대댓글2", comment.getId(), 0L, false, false)
                );
    }

    @DisplayName("대댓글을 페이징 처리하여 조회한다. 좋아요 개수 및 여부, 작성자 여부, 정렬 순서를 알 수 있다.")
    @Test
    void getCommentReplyListPagesWithIsLikeAndIsOwner() {
        // given
        User user = createUser("박지성", "01012345678");
        User user2 = createUser("강혜원", "01011112222");
        User user3 = createUser("김연아", "01022221111");
        userRepository.saveAll(List.of(user, user2, user3));

        Post post = createPost("내용", user);
        postRepository.save(post);

        Comment comment = createComment(post, user, "내용1", null);
        commentRepository.save(comment);

        Comment reply1 = createComment(post, user, "대댓글1", comment);
        Comment reply2 = createComment(post, user2, "대댓글2", comment);
        Comment reply3 = createComment(post, user3, "대댓글3", comment);
        Comment reply4 = createComment(post, user3, "대댓글4", comment);
        Comment reply5 = createComment(post, user3, "대댓글5", comment);
        commentRepository.saveAll(List.of(reply1, reply2, reply3, reply4, reply5));

        CommentLike commentLike1 = createCommentLike(user, reply1);
        CommentLike commentLike2 = createCommentLike(user, reply2);
        CommentLike commentLike3 = createCommentLike(user, reply3);
        CommentLike commentLike4 = createCommentLike(user2, reply3);
        commentLikeRepository.saveAll(List.of(commentLike1, commentLike2, commentLike3, commentLike4));

        // when
        List<CommentReplyListResponse> commentReplyListPages =
                commentService.getCommentReplyListPages(comment.getId(), reply1.getId(), 3, user.getMobileNumber());

        // then
        assertThat(commentReplyListPages).hasSize(3);
        assertThat(commentReplyListPages)
                .extracting("postId", "userId", "commentId", "content", "parentId", "likeCount", "isLike", "isOwner")
                .containsExactly(
                        tuple(post.getId(), user2.getId(), reply2.getId(), "대댓글2", comment.getId(), 1L, true, false),
                        tuple(post.getId(), user3.getId(), reply3.getId(), "대댓글3", comment.getId(), 2L, true, false),
                        tuple(post.getId(), user3.getId(), reply4.getId(), "대댓글4", comment.getId(), 0L, false, false)
                );
    }

    @DisplayName("대댓글을 페이징 처리하여 조회할 때, 차단한 사람의 댓글은 보이지 않는다.")
    @Test
    void getCommentReplyListPagesWithoutBlockedUser() {
        // given
        User user = createUser("박지성", "01012345678");
        User user2 = createUser("강혜원", "01011112222");
        User user3 = createUser("김연아", "01022221111");
        User user4 = createUser("손흥민", "01044444444");
        User blocked = createUser("윤병1", "01055555555");
        userRepository.saveAll(List.of(user, user2, user3, user4, blocked));

        Post post = createPost("내용", user);
        postRepository.save(post);

        Comment comment = createComment(post, user, "내용1", null);
        commentRepository.save(comment);

        Comment reply1 = createComment(post, user, "대댓글1", comment);
        Comment reply2 = createComment(post, user2, "대댓글2", comment);
        Comment reply3 = createComment(post, user3, "대댓글3", comment);
        Comment reply4 = createComment(post, user4, "대댓글4", comment);
        Comment reply5 = createComment(post, blocked, "대댓글5", comment);
        commentRepository.saveAll(List.of(reply1, reply2, reply3, reply4, reply5));

        Block block = createBlock(user, blocked);
        blockRepository.save(block);

        // when
        List<CommentReplyListResponse> commentReplyListPages =
                commentService.getCommentReplyListPages(comment.getId(), 0L, 5, user.getMobileNumber());

        // then
        assertThat(commentReplyListPages).hasSize(4);
        assertThat(commentReplyListPages)
                .extracting("postId", "userId", "commentId", "content", "parentId", "isLike", "isOwner")
                .containsExactly(
                        tuple(post.getId(), user.getId(), reply1.getId(), "대댓글1", comment.getId(), false, true),
                        tuple(post.getId(), user2.getId(), reply2.getId(), "대댓글2", comment.getId(), false, false),
                        tuple(post.getId(), user3.getId(), reply3.getId(), "대댓글3", comment.getId(), false, false),
                        tuple(post.getId(), user4.getId(), reply4.getId(), "대댓글4", comment.getId(), false, false)
                );
    }

    @DisplayName("내가 작성한 댓글 목록을 페이징하여 조회한다.")
    @Test
    void getMyCommentListPages() {
        // given
        User user = createUser("박지성", "01012345678");
        User user2 = createUser("강혜원", "01011112222");
        userRepository.saveAll(List.of(user, user2));

        Post post = createPost("내용", user2);
        postRepository.save(post);

        Comment comment1 = createComment(post, user, "내용1", null);
        Comment comment2 = createComment(post, user, "내용2", null);
        Comment comment3 = createComment(post, user, "내용3", null);
        Comment comment4 = createComment(post, user, "내용4", comment1);
        Comment comment5 = createComment(post, user, "내용5", null);
        commentRepository.saveAll(List.of(comment1, comment2, comment3, comment4, comment5));

        // when
        List<CommentListResponse> commentListResponse =
                commentService.getMyCommentListPages(comment5.getId(), 3, user.getMobileNumber());

        // then
        assertThat(commentListResponse).hasSize(3);
        assertThat(commentListResponse)
                .extracting("postId", "userId", "commentId", "content", "isOwner")
                .containsExactly(
                        tuple(post.getId(), user.getId(), comment4.getId(), "내용4", true),
                        tuple(post.getId(), user.getId(), comment3.getId(), "내용3", true),
                        tuple(post.getId(), user.getId(), comment2.getId(), "내용2", true)
                );

    }

    private User createUser(final String nickname, final String mobileNumber) {
        return User.builder()
                .mobileNumber(mobileNumber)
                .deviceId("123456")
                .authority(Authority.USER)
//                .mbti(Mbti.ENFJ)
                .birth(LocalDate.now())
                .imageUrl("이미지")
                .address("주소")
                .gender(Gender.MALE)
                .nickname(nickname)
                .location(User.createPoint(
                        Coordinate.builder()
                                .lat(37.1)
                                .lng(127.1)
                                .build()
                ))
                .build();
    }

    private Post createPost(final String content, final User user) {
        return Post.builder()
                .content(content)
                .user(user)
                .build();
    }

    private Comment createComment(final Post post, final User user, final String content, final Comment parent) {
        return Comment.builder()
                .user(user)
                .post(post)
                .content(content)
                .parent(parent)
                .build();
    }

    private CommentLike createCommentLike(final User user, final Comment comment) {
        return CommentLike.builder()
                .comment(comment)
                .user(user)
                .build();
    }

    private Block createBlock(final User blocker, final User blocked) {
        return Block.builder()
                .blocker(blocker)
                .blocked(blocked)
                .build();
    }

    private NotificationReceive createNotificationReceive(final User user, boolean chatting, boolean marketing, boolean feed) {
        return NotificationReceive.builder()
                .user(user)
                .chattingNotification(chatting)
                .marketingNotification(marketing)
                .feedNotification(feed)
                .build();
    }

}