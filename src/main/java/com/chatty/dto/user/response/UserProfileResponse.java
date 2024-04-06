package com.chatty.dto.user.response;

import com.chatty.constants.Authority;
import com.chatty.dto.interest.response.InterestResponse;
import com.chatty.entity.user.Gender;
import com.chatty.entity.user.Mbti;
import com.chatty.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserProfileResponse {

    private Long id;
    private String nickname;
    private LocalDate birth;
    private Gender gender;
    private Mbti mbti;
    private String address;
    private Authority authority;
    private String imageUrl;
    private List<InterestResponse> interests;
    private String job;
    private String introduce;
    private String school;
    private boolean blueCheck;
    private boolean unlock;

    @Builder
    public UserProfileResponse(final Long id, final String nickname, final LocalDate birth, final Gender gender, final Mbti mbti, final String address, final Authority authority, final String imageUrl, final List<InterestResponse> interests, final String job, final String introduce, final String school, final boolean blueCheck, final boolean unlock) {
        this.id = id;
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender;
        this.mbti = mbti;
        this.address = address;
        this.authority = authority;
        this.imageUrl = imageUrl;
        this.interests = interests;
        this.job = job;
        this.introduce = introduce;
        this.school = school;
        this.blueCheck = blueCheck;
        this.unlock = unlock;
    }

    public static UserProfileResponse of(final User user, final boolean unlock) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .birth(user.getBirth())
                .gender(user.getGender())
                .mbti(user.getMbti())
                .address(user.getAddress())
                .authority(user.getAuthority())
                .imageUrl(user.getImageUrl())
                .interests(user.getUserInterests().stream()
                        .map(userInterest -> InterestResponse.of(userInterest.getInterest()))
                        .collect(Collectors.toList()))
                .job(user.getJob())
                .introduce(user.getIntroduce())
                .school(user.getSchool())
                .blueCheck(user.isBlueCheck())
                .unlock(unlock)
                .build();
    }

}
