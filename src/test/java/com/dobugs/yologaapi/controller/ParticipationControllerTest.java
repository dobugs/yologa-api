package com.dobugs.yologaapi.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.dobugs.yologaapi.service.ParticipationService;
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
}
