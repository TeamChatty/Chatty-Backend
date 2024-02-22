package com.chatty.entity.user;

import com.chatty.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @DisplayName("닉네임을 변경한다.")
    @Test
    void updateNickname() {
        // given
        User user = createUser();

        // when
        user.updateNickname("닉네임수정");

        // then
        assertThat(user.getNickname()).isEqualTo("닉네임수정");
    }
    
    @DisplayName("내가 보유하고있는 캔디의 수량이 필요한 캔디의 수량보다 작은지 확인한다.")
    @Test
    void isCandyQuantityLessThan() {
        // given
        User user = createUser(6);

        // when
        boolean result = user.isCandyQuantityLessThan(7);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("내가 보유하고있는 캔디의 수량을 차감한다.")
    @Test
    void deductCandyQuantity() {
        // given
        User user = createUser(7);
        int candy = 7;

        // when
        user.deductCandyQuantity(candy);

        // then
        assertThat(user.getCandy()).isZero();
    }

    @DisplayName("내가 보유하고있는 캔디의 수량이 부족하면 예외가 발생한다.")
    @Test
    void deductCandyQuantity2() {
        // given
        User user = createUser(6);
        int candy = 7;

        // when // then
        assertThatThrownBy(() -> user.deductCandyQuantity(candy))
                .isInstanceOf(CustomException.class)
                .hasMessage("캔디의 개수가 부족합니다.");
    }

    private User createUser() {
        return User.builder()
                .nickname("닉네임")
                .build();
    }

    private User createUser(final int candy) {
        return User.builder()
                .nickname("닉네임")
                .candy(candy)
                .build();
    }
}