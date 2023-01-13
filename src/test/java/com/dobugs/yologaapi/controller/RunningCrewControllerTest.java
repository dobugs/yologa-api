package com.dobugs.yologaapi.controller;

import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrewCreateRequest;
import static org.hamcrest.Matchers.is;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrewResponse;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrewUpdateRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.dobugs.yologaapi.service.RunningCrewService;
import com.dobugs.yologaapi.service.dto.request.RunningCrewCreateRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewUpdateRequest;
import com.dobugs.yologaapi.service.dto.response.RunningCrewResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@WebMvcTest(RunningCrewController.class)
@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@DisplayName("RunningCrew 컨트롤러 테스트")
class RunningCrewControllerTest {

    private static final String BASIC_URL = "/api/v1/running-crews";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RunningCrewService runningCrewService;

    @DisplayName("러닝크루를 생성한다")
    @Test
    void create() throws Exception {
        final RunningCrewCreateRequest request = createRunningCrewCreateRequest();
        final String body = objectMapper.writeValueAsString(request);

        final long runningCrewId = 1L;
        given(runningCrewService.create(any())).willReturn(runningCrewId);

        mockMvc.perform(post(BASIC_URL)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(header().string("location", BASIC_URL + "/" + runningCrewId))
            .andDo(document(
                "running-crew/create",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()))
            )
        ;
    }

    @DisplayName("러닝크루의 상세정보를 조회한다")
    @Test
    void findById() throws Exception {
        final long runningCrewId = 1L;

        final RunningCrewResponse response = createRunningCrewResponse(runningCrewId);
        given(runningCrewService.findById(runningCrewId)).willReturn(response);

        mockMvc.perform(get(BASIC_URL + "/" + runningCrewId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("id", is(Integer.parseInt(String.valueOf(runningCrewId)))))
            .andDo(document(
                "running-crew/findById",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()))
            )
        ;
    }

    @DisplayName("러닝크루를 수정한다")
    @Test
    void update() throws Exception {
        final long runningCrewId = 1L;
        final RunningCrewUpdateRequest request = createRunningCrewUpdateRequest();
        final String body = objectMapper.writeValueAsString(request);

        mockMvc.perform(put(BASIC_URL + "/" + runningCrewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andDo(document(
                "running-crew/update",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()))
            )
        ;
    }

    @DisplayName("러닝크루를 삭제한다")
    @Test
    void deleteTest() throws Exception {
        final long runningCrewId = 1L;

        mockMvc.perform(delete(BASIC_URL + "/" + runningCrewId))
            .andExpect(status().isOk())
            .andDo(document(
                "running-crew/delete",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()))
            )
        ;
    }
}
