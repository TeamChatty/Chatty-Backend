package com.chatty.service.user;

import com.chatty.constants.Authority;
import com.chatty.dto.interest.request.InterestRequest;
import com.chatty.dto.user.request.*;
import com.chatty.dto.user.response.UserResponse;
import com.chatty.entity.user.*;
import com.chatty.exception.CustomException;
import com.chatty.repository.interest.InterestRepository;
import com.chatty.repository.user.UserRepository;
import com.chatty.repository.userinterest.UserInterestRepository;
import com.chatty.utils.S3Service;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private S3Service s3Service;

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private UserInterestRepository userInterestRepository;

    @AfterEach
    void tearDown() {
        userInterestRepository.deleteAllInBatch();
        interestRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

//    @DisplayName("회원가입을 완료한다.")
//    @Test
//    void joinComplete() {
//        // given
//        User user = notCompleteJoinUser("01012345678");
//        userRepository.save(user);
//
//        Coordinate coordinate = new Coordinate(37.1, 127.1);
//        LocalDate now = LocalDate.now();
//
//        UserJoinRequest request = UserJoinRequest.builder()
//                .coordinate(coordinate)
//                .birth(now)
//                .nickname("닉네임")
//                .gender(Gender.MALE)
//                .mbti(Mbti.ISTJ)
//                .build();
//
//        // when
//        UserResponse userResponse = userService.joinComplete(user.getMobileNumber(), request);
//        System.out.println("userResponse.getAuthority() = " + userResponse.getAuthority());
//
//        // then
//        assertThat(userResponse).isNotNull();
//        assertThat(userResponse)
//                .extracting("nickname", "birth", "gender", "mbti", "authority")
//                .containsExactlyInAnyOrder(
//                        "닉네임", now, Gender.MALE, Mbti.ISTJ, Authority.USER
//                );
//        assertThat(userResponse.getCoordinate())
//                .extracting("lat", "lng")
//                .containsExactly(
//                        37.1, 127.1
//                );
//    }

    @DisplayName("유저 닉네임을 수정한다.")
    @Test
    void updateNickname() {
        // given
        User user = createUser("닉네임", "01012345678");
        User savedUser = userRepository.save(user);

        UserNicknameRequest request = UserNicknameRequest.builder()
                .nickname("바뀐닉네임")
                .build();

        // when
        UserResponse userResponse = userService.updateNickname(savedUser.getMobileNumber(), request);

        // then
        assertThat(userResponse.getId()).isNotNull();
        assertThat(userResponse.getNickname()).isEqualTo("바뀐닉네임");
    }

    @DisplayName("유저 닉네임을 수정할 때, 중복된 닉네임이 존재하면 예외가 발생한다.")
    @Test
    void updateNicknameWithDuplicateNickname() {
        // given
        User user = createUser("존재하는닉네임", "01012345678");
        User newUser = createUser("닉네임", "01087654321");
        userRepository.saveAll(List.of(user, newUser));

        UserNicknameRequest request = UserNicknameRequest.builder()
                .nickname("존재하는닉네임")
                .build();

        // when // then
        assertThatThrownBy(() -> userService.updateNickname(newUser.getMobileNumber(), request))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 존재 하는 닉네임 입니다.");
    }

    @DisplayName("성별을 수정한다.")
    @Test
    void updateGender() {
        // given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        UserGenderRequest request = UserGenderRequest.builder()
                .gender(Gender.MALE)
                .build();

        // when
        UserResponse userResponse = userService.updateGender(user.getMobileNumber(), request);

        // then
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getGender()).isEqualTo(Gender.MALE);
    }

    @DisplayName("성별을 수정할 때, 남성 - ticket 5장 / 여성 - ticket 11장을 받는다.")
    @CsvSource({"MALE, 5", "FEMALE, 11"})
    @ParameterizedTest
    void updateGenderAndTicket(Gender gender, int ticket) {
        // given
        User user = createUser("닉네임", "01012345678");

        userRepository.save(user);

        UserGenderRequest request = UserGenderRequest.builder()
                .gender(gender)
                .build();

        userService.updateGender(user.getMobileNumber(), request);

        // when
        User savedUser = userRepository.findUserByMobileNumber(user.getMobileNumber()).get();

        // then
        assertThat(savedUser.getGender()).isEqualTo(gender);
        assertThat(savedUser.getTicket()).isEqualTo(ticket);
    }

    @DisplayName("생년월일을 수정한다.")
    @Test
    void updateBirth() {
        // given
        LocalDate now = LocalDate.now();
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        UserBirthRequest request = UserBirthRequest.builder()
                .birth(now)
                .build();

        // when
        UserResponse userResponse = userService.updateBirth(user.getMobileNumber(), request);

        // then
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getBirth()).isEqualTo(now);
    }

    @DisplayName("MBTI를 수정한다.")
    @Test
    void updateMbti() {
        // given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        UserMbtiRequest request = UserMbtiRequest.builder()
                .mbti(Mbti.ESFJ)
                .build();

        // when
        UserResponse userResponse = userService.updateMbti(user.getMobileNumber(), request);

        // then
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getMbti()).isEqualTo(Mbti.ESFJ);
    }

//    @DisplayName("좌표 정보를 수정한다.")
//    @Test
//    void updateCoordinate() {
//        // given
//        User user = createUser("닉네임", "01012345678");
//        userRepository.save(user);
//
//        Coordinate coordinate = new Coordinate(37.1, 127.1);
//        UserCoordinateRequest request = UserCoordinateRequest.builder()
//                .coordinate(coordinate)
//                .build();
//
//        // when
//        UserResponse userResponse = userService.updateCoordinate(user.getMobileNumber(), request);
//
//        // then
//        assertThat(userResponse).isNotNull();
//        assertThat(userResponse.getCoordinate())
//                .extracting("lat", "lng")
//                .containsExactly(
//                        37.1, 127.1
//                );
//    }

    @DisplayName("프로필 이미지를 수정한다.")
    @Test
    void updateImage() throws IOException {
        // given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[]{123, 123});
        when(s3Service.uploadFileToS3(any(MultipartFile.class), anyString()))
                .thenReturn("image.jpg");

        // when
        UserResponse userResponse = userService.updateImage(user.getMobileNumber(), image);

        // then
        assertThat(userResponse.getId()).isNotNull();
        assertThat(userResponse.getImageUrl()).isEqualTo("image.jpg");
    }

    @DisplayName("프로필 이미지를 수정할 때, 이미지 파일을 넣지 않으면 기본 이미지로 저장이 된다.")
    @Test
    void updateImageWithoutFile() throws IOException {
        // given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[]{});

        // when
        UserResponse userResponse = userService.updateImage(user.getMobileNumber(), image);

        // then
        assertThat(userResponse.getId()).isNotNull();
        assertThat(userResponse.getImageUrl()).isEqualTo("profile.jpg");
    }

    @DisplayName("프로필 이미지를 수정할 때, 파일 확장자가 jpg, png, jpeg가 아니면 예외가 발생한다.")
    @Test
    void updateImageWithInvalidExtension() throws IOException {
        // given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        MockMultipartFile image = new MockMultipartFile("image", "image.pdf", "image/jpeg", new byte[]{123});

        // when // then
        assertThatThrownBy(() -> userService.updateImage(user.getMobileNumber(), image))
                .isInstanceOf(CustomException.class)
                .hasMessage("올바르지 않은 확장자입니다.");
    }

    @Test
    @DisplayName("deviceToken을 수정한다.")
    void updateDeviceToken() throws Exception{
        //given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        UserDeviceTokenRequest request = UserDeviceTokenRequest.builder().deviceToken("abcdefg").build();

        //when
        String message = userService.updateDeviceToken(user.getMobileNumber(), request);

        //then
        assertThat(message).isEqualTo("deviceToken이 업데이트 되었습니다.");
    }

    @Test
    @DisplayName("deviceToken을 수정할때, 유저가 존재하지 않는 경우 예외가 발생한다.")
    void updateDeviceTokenWithoutUser() throws Exception{
        //given
        User user = createUser("닉네임", "01012345678");
        UserDeviceTokenRequest request = UserDeviceTokenRequest.builder().deviceToken("abcdefg").build();
        //when,then
        assertThatThrownBy(() -> userService.updateDeviceToken(user.getMobileNumber(), request))
                .isInstanceOf(CustomException.class)
                .hasMessage("존재 하지 않는 유저 입니다.");
    }

    @DisplayName("관심사를 수정한다.")
    @Test
    void updateInterests() throws IOException {
        // given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        Interest interest1 = createInterest("여행");
        Interest interest2 = createInterest("맛집");
        Interest interest3 = createInterest("카페");
        interestRepository.saveAll(List.of(interest1, interest2, interest3));
        List<Long> interests = List.of(interest1.getId(), interest2.getId(), interest3.getId());

        InterestRequest request = InterestRequest.builder()
                .interests(interests)
                .build();

        // when
        UserResponse userResponse = userService.updateInterests(request, user.getMobileNumber());

        // then
        assertThat(userResponse.getId()).isNotNull();
        assertThat(userResponse.getInterests()).hasSize(3)
                .extracting("id", "name")
                .containsExactlyInAnyOrder(
                        tuple(interest1.getId(), "여행"),
                        tuple(interest2.getId(), "맛집"),
                        tuple(interest3.getId(), "카페")
                );
    }

    @DisplayName("관심사를 수정할 때, 존재하지 않는 관심사 값을 넣으면 예외가 발생한다.")
    @Test
    void updateInterestsWithoutInterest() throws IOException {
        // given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        Long interest1 = 1L;
        Long interest2 = 2L;
        Long interest3 = 3L;
        List<Long> interests = List.of(interest1, interest2, interest3);
        InterestRequest request = InterestRequest.builder()
                .interests(interests)
                .build();

        // when // then
        assertThatThrownBy(() -> userService.updateInterests(request, user.getMobileNumber()))
                .isInstanceOf(CustomException.class)
                .hasMessage("존재하지 않는 관심사입니다.");
    }

    @DisplayName("주소를 수정한다")
    @Test
    void updateAddress() throws IOException {
        // given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        UserAddressRequest request = UserAddressRequest.builder()
                .address("주소")
                .build();

        // when
        UserResponse userResponse = userService.updateAddress(request, user.getMobileNumber());

        // then
        assertThat(userResponse.getId()).isNotNull();
        assertThat(userResponse.getAddress()).isEqualTo("주소");
    }

    @DisplayName("직업을 수정한다")
    @Test
    void updateJob() throws IOException {
        // given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        UserJobRequest request = UserJobRequest.builder()
                .job("직업")
                .build();

        // when
        UserResponse userResponse = userService.updateJob(request, user.getMobileNumber());

        // then
        assertThat(userResponse.getId()).isNotNull();
        assertThat(userResponse.getJob()).isEqualTo("직업");
    }

    @DisplayName("학교를 수정한다")
    @Test
    void updateSchool() throws IOException {
        // given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        UserSchoolRequest request = UserSchoolRequest.builder()
                .school("학교")
                .build();

        // when
        UserResponse userResponse = userService.updateSchool(request, user.getMobileNumber());

        // then
        assertThat(userResponse.getId()).isNotNull();
        assertThat(userResponse.getSchool()).isEqualTo("학교");
    }

    @DisplayName("자기소개를 수정한다")
    @Test
    void updateIntroduce() throws IOException {
        // given
        User user = createUser("닉네임", "01012345678");
        userRepository.save(user);

        UserIntroduceRequest request = UserIntroduceRequest.builder()
                .introduce("자기소개")
                .build();

        // when
        UserResponse userResponse = userService.updateIntroduce(request, user.getMobileNumber());

        // then
        assertThat(userResponse.getId()).isNotNull();
        assertThat(userResponse.getIntroduce()).isEqualTo("자기소개");
    }

    private User notCompleteJoinUser(final String mobileNumber) {
        return User.builder()
                .mobileNumber(mobileNumber)
                .deviceId("123456")
                .build();
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

    private Interest createInterest(final String name) {
        return Interest.builder()
                .name(name)
                .build();
    }
}