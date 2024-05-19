package com.chatty.entity.check;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_check_id")
    private Long id;

    private Long userId;

    private Boolean checkNickname;

    private Boolean checkBirth;

    private LocalDate registeredTime;

    private int tryCount;

    public void updateCheckNicknameToCorrect(Boolean isCorrect){
        this.checkNickname = isCorrect;
    }

    public void updateCheckNicknameToIncorrect(Boolean isCorrect){
        this.checkNickname = isCorrect;
        this.tryCount++;
    }

    public void updateCheckBirthToCorrect(Boolean isCorrect){
        this.checkBirth = isCorrect;
    }

    public void updateCheckBirthToIncorrect(Boolean isCorrect){
        this.checkBirth = isCorrect;
        this.tryCount++;
    }

    public static AuthCheck of(Long userId, Boolean checkNickname, Boolean checkBirth, final LocalDate registeredTime, final int tryCount){
        return AuthCheck.builder()
                .userId(userId)
                .checkNickname(checkNickname)
                .checkBirth(checkBirth)
                .registeredTime(registeredTime)
                .tryCount(tryCount)
                .build();
    }
}
