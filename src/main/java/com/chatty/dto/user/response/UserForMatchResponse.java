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
public class UserForMatchResponse {

    private Long id;
    private String mobileNumber;
    private String nickname;
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

    @Builder
    public UserForMatchResponse(final Long id, final String mobileNumber, final String nickname, final Gender gender, final Mbti mbti, final String address, final Authority authority, final String imageUrl, final List<InterestResponse> interests, final String job, final String introduce, final String school, final boolean blueCheck) {
        this.id = id;
        this.mobileNumber = mobileNumber;
        this.nickname = nickname;
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
    }

    public static UserForMatchResponse of(final User user) {
        return UserForMatchResponse.builder()
                .id(user.getId())
                .mobileNumber(user.getMobileNumber())
                .nickname(user.getNickname())
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
                .build();
    }
}
