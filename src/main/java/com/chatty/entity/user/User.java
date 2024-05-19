package com.chatty.entity.user;

import com.chatty.constants.Authority;
import com.chatty.constants.Code;
import com.chatty.entity.BaseTimeEntity;
import com.chatty.entity.CommonEntity;
import com.chatty.entity.alarm.Alarm;
import com.chatty.entity.block.Block;
import com.chatty.entity.bookmark.Bookmark;
import com.chatty.entity.chat.ChatRoom;
import com.chatty.entity.check.AuthCheck;
import com.chatty.entity.comment.Comment;
import com.chatty.entity.like.CommentLike;
import com.chatty.entity.like.PostLike;
import com.chatty.entity.match.Match;
import com.chatty.entity.match.MatchHistory;
import com.chatty.entity.notification.NotificationReceive;
import com.chatty.entity.post.Post;
import com.chatty.exception.CustomException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.*;

import lombok.*;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "mobile_number")
    @NotBlank
    private String mobileNumber;

    @Column(name = "device_id")
    @NotBlank
    private String deviceId;

    private String nickname;

    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Mbti mbti;

//    @Column(columnDefinition = "POINT SRID 4326")
    private Point location;

    private String address;

    // TODO: 주소, 직업, 학교, 관심사
    private String job;

    private String school;

    private String introduce;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserInterest> userInterests = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "blocked", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Block> blocked = new ArrayList<>();

    @OneToMany(mappedBy = "blocker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Block> blocker = new ArrayList<>();

//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private NotificationReceive notificationReceive;

    @OneToMany(mappedBy = "unlocker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProfileUnlock> unlocker = new ArrayList<>();

    @OneToMany(mappedBy = "unlockedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProfileUnlock> unlocked = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> commentLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Match> matches = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Alarm> alarms = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchHistory> matchHistoriesSender = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchHistory> matchHistoriesReceiver = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRoomsReceiver = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRoomsSender = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Authority authority;

    private String imageUrl;

    private String deviceToken;

    private boolean blueCheck;

    private int ticket;
    private int candy;

//    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private NotificationReceive notificationReceive;

    public void joinComplete(final User request) {
        this.nickname = request.getNickname();
        this.location = request.getLocation();
        this.gender = request.getGender();
        this.birth = request.getBirth();
        this.mbti = request.getMbti();
        this.authority = request.getAuthority();
    }

    public void updateNickname(final String nickname) {
        this.nickname = nickname;
    }

    public void updateGender(final Gender gender) {
        this.gender = gender;
        this.ticket = gender.getGender().equals("남") ? 5 : 11;
    }

    public void updateBirth(final LocalDate birth) {
        this.birth = birth;
    }

    public void updateMbti(final Mbti mbti) {
        this.mbti = mbti;
        this.authority = Authority.USER;
    }

    public void updateCoordinate(final Coordinate coordinate) {
        this.location = createPoint(coordinate);
    }

    public void updateImage(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateDeviceToken(final String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void updateDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }

    public static Point createPoint(final Coordinate coordinate) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(coordinate.getLng(), coordinate.getLat()));
    }

    public void updateInterests(final Set<UserInterest> userInterests) {
        this.userInterests = userInterests;
    }

    public void updateAddress(final String address) {
        this.address = address;
    }

    public void updateJob(final String job) {
        this.job = job;
    }

    public void updateSchool(final String school) {
        this.school = school;
    }

    public void updateIntroduce(final String introduce) {
        this.introduce = introduce;
    }

    public boolean isCandyQuantityLessThan(final int candy) {
        return this.candy < candy;
    }

    public void deductCandyQuantity(final int candy) {
        if (isCandyQuantityLessThan(candy)) {
            throw new CustomException(Code.INSUFFICIENT_CANDY);
        }

        this.candy -= candy;
    }

    public boolean isTicketQuantityLessThan(final int ticket) {
        return this.ticket < ticket;
    }

    public void deductTicketQuantity(final int ticket) {
        if (isTicketQuantityLessThan(ticket)) {
            throw new CustomException(Code.INSUFFICIENT_TICKET);
        }

        this.ticket -= ticket;
    }

    public void resetTicket() {
        this.ticket = this.gender.getGender().equals("남") ? 5 : 11;
    }

    public void changeNumber(final String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + authority.name()));
    }

    @Override
    public String getPassword() {
        return deviceId;
    }

    @Override
    public String getUsername() {
        return mobileNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
