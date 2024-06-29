package com.chatty.service.match;

import com.chatty.config.WebSocketConnectionManager;
import com.chatty.dto.chat.request.ChatRoomCreateRequest;
import com.chatty.dto.chat.response.ChatRoomResponse;
import com.chatty.dto.match.response.MatchResponse;
import com.chatty.dto.user.response.UserForMatchResponse;
import com.chatty.entity.match.MatchHistory;
import com.chatty.entity.user.Gender;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.block.BlockRepository;
import com.chatty.repository.chat.ChatRoomRepository;
import com.chatty.repository.match.MatchHistoryRepository;
import com.chatty.repository.user.UserRepository;
import com.chatty.service.chat.RoomService;
import com.chatty.service.user.UserService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.chatty.constants.Code.NOT_EXIST_USER;

@RequiredArgsConstructor
@Slf4j
@Component
public class MatchHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new ArrayList<>();
    private final Gson gson;
    private final UserRepository userRepository;
    private final MatchService matchService;
    private final RoomService roomService;
    private final MatchHistoryRepository matchHistoryRepository;

    private final ChatRoomRepository chatRoomRepository;
    private final BlockRepository blockRepository;
    
    private final UserService userService;

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
        sessions.add(session);
        String mobileNumber = session.getAttributes().get("mobileNumber").toString();
        WebSocketConnectionManager.addConnection(mobileNumber, session.getId());

        log.info("sessionId = {}, sessions size = {}", session.getId(), sessions.size());
    }

    @Synchronized
    @Override
    public void handleTextMessage(final WebSocketSession session, final TextMessage message) throws Exception {
        String payload = message.getPayload();
        WebSocketSession destination = null;
        if (payload.equals("accept") || payload.equals("reject")) {
            log.info("들어오나?");
            session.getAttributes().put("answer", payload);
            for (WebSocketSession connected : sessions) {
                if (session.getAttributes().get("destination").equals(connected.getId())) {
                    destination = connected;
                    break;
                }
            }

            String myAnswer = session.getAttributes().get("answer").toString();
            if (destination != null) {
                String destinationAnswer = String.valueOf(destination.getAttributes().get("answer"));
                log.info("myAnswer ={}, destinationAnswer={}", myAnswer, destinationAnswer);

                if (myAnswer.equals("accept")) {
                    if (destinationAnswer.equals("null")) {
                        return;
                    } else if (destinationAnswer.equals("accept")) {
                        String mobileNumber = session.getAttributes().get("mobileNumber").toString();
                        Long sessionMatchId = Long.parseLong(session.getAttributes().get("matchId").toString());
                        Long connectedMatchId = Long.parseLong(destination.getAttributes().get("matchId").toString());

                        Long sessionUserId = Long.parseLong(session.getAttributes().get("userId").toString());
                        Long connectedUserId = Long.parseLong(destination.getAttributes().get("userId").toString());
                        // 채팅방 생성
                        createChatroom(session, destination, connectedUserId, mobileNumber);
                        //

                        // 매치 성공하면 true
                        matchService.successMatch(sessionMatchId);
                        matchService.successMatch(connectedMatchId);

                        // 매치 성공하면 history에 저장
                        createMatchHistory(sessionUserId, connectedUserId);

                        log.info("매칭 완료!!!!");
                        sessions.remove(session);
                        sessions.remove(destination);
                        session.close();
                        destination.close();
                        return;
                    }
                }
            }

            if (myAnswer.equals("reject")) {
                Long sessionUserId = Long.parseLong(session.getAttributes().get("userId").toString());
                Long connectedUserId = Long.parseLong(destination.getAttributes().get("userId").toString());
                createMatchHistory(sessionUserId, connectedUserId);

                sessions.remove(session);
                sessions.remove(destination);

                session.close();
                destination.close();
            }
        } else {
            MatchResponse matchResponse = gson.fromJson(payload, MatchResponse.class);

            // TODO: matchService 로 넘길 필요가 없다.
            matchService.createUserSession(session, matchResponse);

            Object myRequestGender = session.getAttributes().get("requestGender");
            Object myGender = session.getAttributes().get("gender");

            for (WebSocketSession connected : sessions) {
                log.info("session에 저장되어있는 requestGender 값 = {}", session.getAttributes().get("requestGender"));
                if (connected.getAttributes().containsKey("destination")) {
                    log.info("목적지가 존재하기 때문에 매칭을 하지 않습니다.");
                    continue;
                }

                if (session == connected || connected.getAttributes().get("nickname") == null) {
                    continue;
                }


                Long senderId = Long.parseLong(session.getAttributes().get("userId").toString());
                Long receiverId = Long.parseLong(connected.getAttributes().get("userId").toString());

                if (isBlocked(senderId, receiverId)) {
                    continue;
                }

                // 이미 매칭 기록이 존재하면 Skip
                if (hasExistingMatch(senderId, receiverId)) {
                    continue;
                }

                // 이미 채팅 기록이 존재하면 Skip
                if (hasExistingChatroom(senderId, receiverId)) {
                    continue;
                }

                // session requestBlueCheck 값 체크
                Object requestBlueCheck = session.getAttributes().get("requestBlueCheck");
                Object connectedBlueCheck = connected.getAttributes().get("isBlueCheck");
                if (requestBlueCheck.equals(true)) {
                    if (connectedBlueCheck.equals(false)) {
//                    log.info("BlueCheck 사람과 매칭하고 싶은데 상대방은 BlueCheck 없다. -> continue");
                        continue;
                    }
                }

                // 내 성별, 내가 원하는 성별, 상대 성별, 상대가 원하는 성별 체크
                Object yourGender = connected.getAttributes().get("gender");
                Object yourRequestGender = connected.getAttributes().get("requestGender");
                if (!matchGenderConditions(myRequestGender, yourRequestGender, myGender, yourGender)) {
                    continue;
                }

                // 1. 내가 원하는 최소 나이와 최대 나이, 그리고 상대방의 나이를 비교한다.
                int myRequestMinAge = Integer.parseInt(session.getAttributes().get("requestMinAge").toString());
                int myRequestMaxAge = Integer.parseInt(session.getAttributes().get("requestMaxAge").toString());
                int myAge = Integer.parseInt(session.getAttributes().get("age").toString());

                int yourRequestMinAge = Integer.parseInt(connected.getAttributes().get("requestMinAge").toString());
                int yourRequestMaxAge = Integer.parseInt(connected.getAttributes().get("requestMaxAge").toString());
                int yourAge = Integer.parseInt(connected.getAttributes().get("age").toString());

                if (!matchAgeConditions(myRequestMinAge, yourAge, myRequestMaxAge, yourRequestMinAge, myAge, yourRequestMaxAge)) {
                    log.info("매칭 실패?");
                    continue;
                }

                UserForMatchResponse sessionUser = userService.getMyProfileForMatch(session.getAttributes().get("mobileNumber").toString());
                UserForMatchResponse connectedUser = userService.getMyProfileForMatch(connected.getAttributes().get("mobileNumber").toString());
                String me = gson.toJson(sessionUser);
                String you = gson.toJson(connectedUser);
                TextMessage textMessage2 = new TextMessage(me);
                TextMessage textMessage3 = new TextMessage(you);

                session.sendMessage(textMessage3);
                connected.sendMessage(textMessage2);

                session.getAttributes().put("destination", connected.getId());
                connected.getAttributes().put("destination", session.getId());

                break;
            }
        }

    }

    private void createMatchHistory(final Long sessionUserId, final Long connectedUserId) {
        User sender = userRepository.findById(sessionUserId)
                .orElseThrow(() -> new CustomException(NOT_EXIST_USER));
        User receiver = userRepository.findById(connectedUserId)
                .orElseThrow(() -> new CustomException(NOT_EXIST_USER));

        MatchHistory matchHistory = MatchHistory.builder()
                .sender(sender)
                .receiver(receiver)
                .build();
        matchHistoryRepository.save(matchHistory);
    }

    private void createChatroom(final WebSocketSession session, final WebSocketSession connected, final Long connectedUserId, final String mobileNumber) throws IOException {
        ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
                .receiverId(connectedUserId)
                .build();
        ChatRoomResponse room = roomService.createRoomForMatching(request, mobileNumber);
        String json = gson.toJson(room);
        TextMessage textMessage2 = new TextMessage(json);
        session.sendMessage(textMessage2);
        connected.sendMessage(textMessage2);
    }

    private boolean matchAgeConditions(final int myRequestMinAge, final int yourAge, final int myRequestMaxAge, final int yourRequestMinAge, final int myAge, final int yourRequestMaxAge) {
//        if (!(myRequestMinAge <= yourAge && yourAge <= myRequestMaxAge)) { // 내가 원하는 나이대에 상대방 나이가 아니라면
//            log.info("내가 정한 최소 나이 = {}", myRequestMinAge);
//            log.info("상대방의 나이 = {}", yourAge);
//            log.info("내가 정한 최대 나이 = {}", myRequestMaxAge);
//            log.info("내가 정한 최소 나이와 최대 나이 사이에 상대방의 나이가 들어가지 않습니다.");
//            return true;
//        }
//
//        if (!(yourRequestMinAge <= myAge && myAge <= yourRequestMaxAge)) {
//            log.info("상대방이 정한 최소 나이와 최대 나이 사이에 내 나이가 들어가지 않습니다.");
//            return true;
//        }

//        log.info("나이 조건이 만족합니다. 최종 매칭 성공!");
//        return false;
        return (myRequestMinAge <= yourAge && yourAge <= myRequestMaxAge) &&
                (yourRequestMinAge <= myAge && myAge <= yourRequestMaxAge);
    }

    private boolean matchGenderConditions(final Object myRequestGender, final Object yourRequestGender, final Object myGender, final Object yourGender) {
        if (myRequestGender.equals(Gender.ALL)) {
            return yourRequestGender.equals(Gender.ALL) || yourRequestGender.equals(myGender);
        }

        if (myRequestGender.equals(Gender.MALE)) {
            return yourGender.equals(Gender.MALE) && (yourRequestGender.equals(Gender.ALL) || yourRequestGender.equals(myGender));
        }

        if (myRequestGender.equals(Gender.FEMALE)) {
            return yourGender.equals(Gender.FEMALE) && (yourRequestGender.equals(Gender.ALL) || yourRequestGender.equals(myGender));
        }
        log.info("성별 조건이 만족합니다.");
        return false;
    }

    private boolean hasExistingChatroom(final Long senderId, final Long receiverId) {
        if (chatRoomRepository.existsBySenderIdAndReceiverId(senderId, receiverId) ||
                chatRoomRepository.existsBySenderIdAndReceiverId(receiverId, senderId)) {
            log.info("이미 채팅 내역이 존재합니다.");
            return true;
        }
        return false;
    }

    private boolean hasExistingMatch(final Long senderId, final Long receiverId) {
        if (matchHistoryRepository.existsBySenderIdAndReceiverId(senderId, receiverId) ||
                matchHistoryRepository.existsBySenderIdAndReceiverId(receiverId, senderId)) {
            log.info("이미 매칭 기록이 존재합니다.");
            return true;
        }
        return false;
    }

    private boolean isBlocked(final Long senderId, final Long receiverId) {
        if (blockRepository.existsByBlockerIdAndBlockedId(senderId, receiverId) ||
                blockRepository.existsByBlockerIdAndBlockedId(receiverId, senderId)) {
            log.info("{}번 유저와 {}번 유저는 차단되어있습니다. ", senderId, receiverId);
            return true;
        }
        return false;
    }

    private boolean isInvalidGenderMatch(final WebSocketSession session, final WebSocketSession connected) {
        Object connectedUserRequestGender = connected.getAttributes().get("requestGender");
        Object sessionUserGender = session.getAttributes().get("gender");

        return !connectedUserRequestGender.equals(Gender.ALL) && !connectedUserRequestGender.equals(sessionUserGender);
    }

    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) throws Exception {
        String mobileNumber = session.getAttributes().get("mobileNumber").toString();
        WebSocketConnectionManager.removeConnection(mobileNumber);
        log.info("세션 삭제 완료 소켓 연결 끊음.");
        sessions.remove(session);
    }

}
