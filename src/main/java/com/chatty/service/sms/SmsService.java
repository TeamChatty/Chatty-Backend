package com.chatty.service.sms;

import static com.chatty.utils.sms.SmsUtils.makeSignature;

import com.chatty.constants.Code;
import com.chatty.dto.sms.request.MessageRequestDto;
import com.chatty.dto.sms.request.NaverSmsRequestDto;
import com.chatty.dto.sms.request.UserSmsRequestDto;
import com.chatty.dto.sms.response.SmsResponseDto;
import com.chatty.dto.sms.response.SmsUserResponseDto;
import com.chatty.exception.CustomException;
import com.chatty.repository.auth.AuthNumberRepository;
import com.chatty.utils.sms.SmsUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    @Value("${naver-cloud-sms-access-key}")
    private String accessKey;

    @Value("${naver-cloud-sms-secret-key}")
    private String secretKey;

    @Value("${naver-cloud-sms-service-id}")
    private String serviceId;

    @Value("${naver-cloud-sms-sender-phone-number}")
    private String phone;

    private static final int AUTH_NUMBER_LIMIT = 5;
    private static final String LIMIT_FIRST_NUMBER = "1";

    private final AuthNumberRepository authNumberRepository;

    public SmsResponseDto sendSms(MessageRequestDto messageRequestDto)
            throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Long time = System.currentTimeMillis();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", accessKey);
        headers.set("x-ncp-apigw-signature-v2", makeSignature(accessKey, serviceId, secretKey, time));

        List<MessageRequestDto> messages = new ArrayList<>();
        messages.add(messageRequestDto);

        NaverSmsRequestDto request = NaverSmsRequestDto.builder()
                .type("SMS")
                .contentType("COMM")
                .countryCode("82")
                .from(phone)
                .content(messageRequestDto.getContent())
                .messages(messages)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(request);
        HttpEntity<String> httpBody = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        SmsResponseDto response = restTemplate.postForObject(
                new URI("https://sens.apigw.ntruss.com/sms/v2/services/" + serviceId + "/messages"), httpBody,
                SmsResponseDto.class);

        return response;
    }

    @Transactional
    public SmsUserResponseDto saveSms(UserSmsRequestDto userSmsRequestDto) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {

        String authNumber = SmsUtils.generateNumber();
        String key = userSmsRequestDto.getMobileNumber();
        authNumberRepository.save(key, authNumber);

        String limitValue = checkAuthLimitNumber(key);

        log.info("번호 인증 요청 정보 저장 완료 : {}", authNumber);
        sendSms(MessageRequestDto.builder().to(userSmsRequestDto.getMobileNumber()).content(authNumber).build());
        return SmsUserResponseDto.of(authNumber, Integer.parseInt(limitValue));
    }

    private String checkAuthLimitNumber(final String authNumber){

        String limitValue = authNumberRepository.findAuthLimitNumber(authNumber);

        if(limitValue == null) {
            authNumberRepository.saveLimitNumber(authNumber);
            return LIMIT_FIRST_NUMBER;
        }

        if(Integer.parseInt(limitValue) == AUTH_NUMBER_LIMIT) {
            throw new CustomException(Code.AUTH_NUMBER_LIMIT);
        }

        authNumberRepository.updateAuthLimitNumber(authNumber, limitValue);
        int result = Integer.parseInt(limitValue) + 1;

        return String.valueOf(result);
    }

    public boolean checkAuthNumber(String key, String authNumber) {
        String auth = authNumberRepository.findAuthNumber(key);
        log.info("확인이 필요한 인증번호 : {}", auth);

        return auth != null && auth.equals(authNumber);
    }
}
