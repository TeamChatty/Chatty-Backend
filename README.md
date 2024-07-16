# 💡 Chatty

![img.png](img/thumbnail.png)

<br />

## 📌 목차
  <ul>
    <li><a href="#-프로젝트-소개">프로젝트 소개</a></li>
    <li><a href="#-팀원">팀원</a></li>
    <li><a href="#-기술-스택">기술 스택</a></li>
    <li><a href="#-구현-기능">구현 기능</a></li>
    <li><a href="#-아키텍처">아키텍처</a></li>
    <li><a href="#-ui">UI</a></li>
  </ul>

<br />

## 💁‍♂ 프로젝트 소개

1:1 채팅 및 커뮤니티 서비스입니다.

> 매칭 시스템을 이용하여 원하는 상대와 채팅을 나눌 수 있습니다.

<br />

## 🌟 팀원

|                백엔드                 |                   백엔드                   |
|:----------------------------------:|:---------------------------------------:|
| [윤병일](https://github.com/YunByungil) | [박상윤](https://github.com/sangyunpark99) |                                          [안세준](https://github.com/asjjun)                                           |||
  
|   IOS   |   IOS   |    
|:-------:|:-------:| 
| [이훈희](https://github.com/hunhee98) | [윤지호](https://github.com/yoonjiho37) |
<br />

## 🛠 기술 스택

<br />

- BE ![Java](https://img.shields.io/badge/Java17-%23ED8B00.svg?style=square&logo=openjdk&logoColor=white) <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=square&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=square&logo=Spring Security&logoColor=white"> ![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=square&logo=Spring&logoColor=white) ![JWT](https://img.shields.io/badge/JWT-black?style=square&logo=JSON%20web%20tokens) ![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=square&logo=Gradle&logoColor=white)
- DB ![MySQL](https://img.shields.io/badge/MySQL-4479A1.svg?style=square&logo=mysql&logoColor=white) ![AmazonDynamoDB](https://img.shields.io/badge/Amazon%20RDS-527FFF?style=square&logo=AmazonRDS&logoColor=white)
- Infra ![Ubuntu](https://img.shields.io/badge/Ubuntu-E95420?style=square&logo=ubuntu&logoColor=white) <img src="https://img.shields.io/badge/Redis-DC382D?style=square&logo=Redis&logoColor=white"> <img src="https://img.shields.io/badge/Amazon%20EC2-FF9900?style=square&logo=Amazon%20EC2&logoColor=white"> <img src="https://img.shields.io/badge/Amazon%20S3-569A31?style=square&logo=Amazon%20S3&logoColor=white">
- CI/CD <img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=square&logo=GitHub Actions&logoColor=white"> <img src="https://img.shields.io/badge/Docker-%230db7ed.svg?style=square&logo=docker&logoColor=white">
- Tools ![GitHub](https://img.shields.io/badge/Github-%23121011.svg?style=square&logo=github&logoColor=white) ![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-000000.svg?style=square&logo=intellij-idea&logoColor=white) ![Postman](https://img.shields.io/badge/Postman-FF6C37?style=square&logo=postman&logoColor=white) ![Notion](https://img.shields.io/badge/Notion-%23000000.svg?style=square&logo=notion&logoColor=white) ![Figma](https://img.shields.io/badge/Figma-%23F24E1E.svg?style=square&logo=figma&logoColor=white) ![Discord](https://img.shields.io/badge/Discord-%235865F2.svg?style=square&logo=discord&logoColor=white) <br/><br/>

<br />

## 📱 구현 기능

- 🔐 계정
    - 로그인
    - 회원 정보 수정
    - 인증 번호 유효성 검증
    - 토큰 검증
  

- 📌 공통
  - 실시간 FCM 알람
  - 알림 조회 / 읽기
  - 유저 신고 및 차단 
  

- 🔍 매칭
    - 조건에 맞는 사람과 매칭 시스템
    - 원하는 나이, 성별, MBTI 설정
   
  
- 📦 게시판
    - 게시글 등록 / 수정 / 삭제
    - 댓글 등록 / 수정 / 삭제
    - 게시글, 댓글 좋아요 / 취소
  
  
- 💬 채팅
    - 1:1 채팅
    - 채팅방 신고 / 나가기
  

- 👤 프로필
    - 프로필 수정
    - 프로필 이미지 파일 업로드 / 수정 / 미리보기
    - 작성한 글 / 작성한 댓글 / 북마크

<br />

## 🗺️ 아키텍처

<br />

![img.png](img/arc.png)

<br />

## 🎨 UI



| 시작화면                                                                                         | 로그인| 회원가입 | 
|----------------------------------------------------------------------------------------------|---|---|
| ![img_3.png](img/img_3.png) |![img_4.png](img/img_4.png)|![img_5.png](img/img_5.png)|

|홈 화면| 매칭 준비                                                                                       | 매칭                                                                                      |
|---|---------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------|
|![img.png](img/img.png)| ![img_1.png](img/img_1.png) | ![img_2.png](img/img_2.png) |

| 채팅 목록                                                                                   | 채팅방                                                                                       | 신고 및 차단                                                                                     |
|-----------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------|
| ![img_6.png](img/img_6.png) | ![img_7.png](img/img_7.png) | ![img_8.png](img/img_8.png) |

| 게시글 작성                                                                                        | 최신글 목록                                                                                  | 추천순 목록                    |
|-----------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------|---------------------------|
| ![img_9.png](img/img_9.png) | ![img_10.png](img/img_10.png) | ![img_11.png](img/img_11.png) |

| 댓글 작성                                                                                       | 댓글 목록                                                                                               | 답글 목록                                                                                        |
|---------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------|
| ![img_18.png](img/img_18.png) | ![img_19.png](img/img_19.png) | ![img_20.png](img/img_20.png) |

| 작성한 글                                                                                      | 내 댓글                                                                                          | 저장한 글                     |
|---------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------|---------------------------|
| ![img_12.png](img/img_12.png) | ![img_13.png](img/img_13.png) | ![img_14.png](img/img_14.png) |

| 프로필 사진 변경                                                                                | 프로필 | 상세 프로필 |
|------------------------------------------------------------------------------------------|---|---|
| ![img_15.png](img/img_15.png) |![img_16.png](img/img_16.png)|![img_17.png](img/img_17.png)|

| 직접 채팅 신청                                                                                 | 메세지 전송                                                                                           | 채팅방 생성                                                                                 |
|------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------|
| ![img_21.png](img/img_21.png) | ![img_22.png](img/img_22.png) | ![img_23.png](img/img_23.png) |

| 실시간 알람 설정                                                                                      | 댓글 알람                     | 좋아요 알람                                                                                         |
|------------------------------------------------------------------------------------------------|---------------------------|----------------------------------------------------------------------------------------------------|
| ![img_24.png](img/img_24.png) | ![img_25.png](img/img_25.png) | ![img_26.png](img/img_26.png) |

|회원 신고 시 admin에게 실시간 알람 전송
|---|
|![img_27.png](img/img_27.png)|



<br />
<br />
