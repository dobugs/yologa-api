package com.dobugs.yologaapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.dobugs.yologaapi.service.ParticipationService;
import com.dobugs.yologaapi.service.dto.response.ParticipantResponse;
import com.dobugs.yologaapi.service.dto.response.ParticipantsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@WebMvcTest(ParticipationController.class)
@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@DisplayName("Participation 컨트롤러 테스트")
class ParticipationControllerTest {

    private static final String BASIC_URL = "/api/v1/running-crews";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ParticipationService participationService;

    @DisplayName("러닝크루 참여자 목록 정보를 조회한다")
    @Test
    void findParticipants() throws Exception {
        final long runningCrewId = 1L;
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", String.valueOf(0));
        params.add("size", String.valueOf(10));

        final ParticipantResponse participant1 = new ParticipantResponse(1L, "유콩");
        final ParticipantResponse participant2 = new ParticipantResponse(2L, "건");
        final ParticipantsResponse response = new ParticipantsResponse(
            2, 0, 10, List.of(participant1, participant2)
        );

        given(participationService.findParticipants(eq(runningCrewId), any())).willReturn(response);

        mockMvc.perform(get(BASIC_URL + "/" + runningCrewId + "/participants")
                .params(params))
            .andExpect(status().isOk())
            .andDo(document(
                "participation/findParticipants",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()))
            )
        ;
    }

    @DisplayName("러닝크루에 참여 요청을 한다")
    @Test
    void participate() throws Exception {
        final String accessToken = "accessToken";
        final long runningCrewId = 1L;

        mockMvc.perform(post(BASIC_URL + "/" + runningCrewId + "/participate")
                .header("Authorization", accessToken))
            .andExpect(status().isOk())
            .andDo(document(
                "participation/participate",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()))
            )
        ;
    }

    @DisplayName("러닝크루에 참여 요청을 취소한다")
    @Test
    void cancel() throws Exception {
        final String accessToken = "accessToken";
        final long runningCrewId = 1L;

        mockMvc.perform(post(BASIC_URL + "/" + runningCrewId + "/cancel")
                .header("Authorization", accessToken))
            .andExpect(status().isOk())
            .andDo(document(
                "participation/cancel",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()))
            )
        ;
    }

    @DisplayName("러닝크루에 탈퇴한다")
    @Test
    void withdraw() throws Exception {
        final String accessToken = "accessToken";
        final long runningCrewId = 1L;

        mockMvc.perform(post(BASIC_URL + "/" + runningCrewId + "/withdraw")
                .header("Authorization", accessToken))
            .andExpect(status().isOk())
            .andDo(document(
                "participation/withdraw",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()))
            )
        ;
    }

    @DisplayName("러닝크루 참여 요청을 승인한다")
    @Test
    void accept() throws Exception {
        final String accessToken = "accessToken";
        final long runningCrewId = 1L;
        final long memberId = 1L;

        mockMvc.perform(post(BASIC_URL + "/" + runningCrewId + "/accept/" + memberId)
                .header("Authorization", accessToken))
            .andExpect(status().isOk())
            .andDo(document(
                "participation/accept",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()))
            )
        ;
    }

    @DisplayName("러닝크루 참여 요청을 거절한다")
    @Test
    void reject() throws Exception {
        final String accessToken = "accessToken";
        final long runningCrewId = 1L;
        final long memberId = 1L;

        mockMvc.perform(post(BASIC_URL + "/" + runningCrewId + "/reject/" + memberId)
                .header("Authorization", accessToken))
            .andExpect(status().isOk())
            .andDo(document(
                "participation/reject",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()))
            )
        ;
    }
}
