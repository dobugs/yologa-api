package com.dobugs.yologaapi.controller;

import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.LATITUDE;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.LONGITUDE;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrewCreateRequest;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrewResponse;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrewSummaryResponse;
import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrewUpdateRequest;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.dobugs.yologaapi.domain.runningcrew.ProgressionType;
import com.dobugs.yologaapi.service.RunningCrewService;
import com.dobugs.yologaapi.service.dto.request.PagingRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewCreateRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewStatusRequest;
import com.dobugs.yologaapi.service.dto.request.RunningCrewUpdateRequest;
import com.dobugs.yologaapi.service.dto.response.RunningCrewFindNearbyResponse;
import com.dobugs.yologaapi.service.dto.response.RunningCrewResponse;
import com.dobugs.yologaapi.service.dto.response.RunningCrewsResponse;

@WebMvcTest(RunningCrewController.class)
@DisplayName("RunningCrew 컨트롤러 테스트")
class RunningCrewControllerTest extends ControllerTest {

    private static final String BASIC_URL = "/api/v1/running-crews";

    @MockBean
    private RunningCrewService runningCrewService;

    @DisplayName("러닝크루를 생성한다")
    @Test
    void create() throws Exception {
        final RunningCrewCreateRequest request = createRunningCrewCreateRequest();
        final String body = objectMapper.writeValueAsString(request);

        final String accessToken = "accessToken";
        final long runningCrewId = 1L;
        given(runningCrewService.create(any(), any())).willReturn(runningCrewId);

        mockMvc.perform(post(BASIC_URL)
                .header("Authorization", accessToken)
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

    @DisplayName("내 주변 러닝크루 목록을 조회한다")
    @Test
    void findNearby() throws Exception {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("latitude", String.valueOf(LATITUDE));
        params.add("longitude", String.valueOf(LONGITUDE));
        params.add("radius", String.valueOf(3000));
        params.add("page", String.valueOf(0));
        params.add("size", String.valueOf(10));

        final RunningCrewFindNearbyResponse response = new RunningCrewFindNearbyResponse(
            1, 0, 10, List.of(createRunningCrewSummaryResponse())
        );
        given(runningCrewService.findNearby(any())).willReturn(response);

        mockMvc.perform(get(BASIC_URL)
                .params(params))
            .andExpect(status().isOk())
            .andDo(document(
                "running-crew/findNearby",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()))
            )
        ;
    }

    @DisplayName("현재 진행중인 내 러닝크루 목록을 조회한다")
    @Test
    void findInProgress() throws Exception {
        final int page = 0;
        final int size = 10;
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", String.valueOf(page));
        params.add("size", String.valueOf(size));

        final PagingRequest request = new PagingRequest(page, size);
        final RunningCrewsResponse response = new RunningCrewsResponse(1, page, size, List.of(createRunningCrewResponse(1L)));
        given(runningCrewService.findInProgress(any(), eq(request))).willReturn(response);

        mockMvc.perform(get(BASIC_URL + "/in-progress")
                .header("Authorization", "accessToken")
                .params(params))
            .andExpect(status().isOk())
            .andDo(document(
                "running-crew/findInProgress",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()))
            )
        ;
    }

    @DisplayName("내가 주최한 러닝크루 목록을 조회한다")
    @Test
    void findHosted() throws Exception {
        final String status = ProgressionType.CREATED.getSavedName();
        final int page = 0;
        final int size = 10;
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("status", status);
        params.add("page", String.valueOf(page));
        params.add("size", String.valueOf(size));

        final RunningCrewStatusRequest request = new RunningCrewStatusRequest(status, page, size);
        final RunningCrewsResponse response = new RunningCrewsResponse(1, page, size, List.of(createRunningCrewResponse(1L)));
        given(runningCrewService.findHosted(any(), eq(request))).willReturn(response);

        mockMvc.perform(get(BASIC_URL + "/hosted")
                .header("Authorization", "accessToken")
                .params(params))
            .andExpect(status().isOk())
            .andDo(document(
                "running-crew/findHosted",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()))
            )
        ;
    }

    @DisplayName("내가 참여한 러닝크루 목록을 조회한다")
    @Test
    void findParticipated() throws Exception {
        final String status = ProgressionType.CREATED.getSavedName();
        final int page = 0;
        final int size = 10;
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("status", status);
        params.add("page", String.valueOf(page));
        params.add("size", String.valueOf(size));

        final RunningCrewStatusRequest request = new RunningCrewStatusRequest(status, page, size);
        final RunningCrewsResponse response = new RunningCrewsResponse(1, page, size, List.of(createRunningCrewResponse(1L)));
        given(runningCrewService.findParticipated(any(), eq(request))).willReturn(response);

        mockMvc.perform(get(BASIC_URL + "/participated")
                .header("Authorization", "accessToken")
                .params(params))
            .andExpect(status().isOk())
            .andDo(document(
                "running-crew/findParticipated",
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
        final String accessToken = "accessToken";
        final RunningCrewUpdateRequest request = createRunningCrewUpdateRequest();
        final String body = objectMapper.writeValueAsString(request);

        mockMvc.perform(put(BASIC_URL + "/" + runningCrewId)
                .header("Authorization", accessToken)
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
        final String accessToken = "accessToken";

        mockMvc.perform(delete(BASIC_URL + "/" + runningCrewId)
                .header("Authorization", accessToken))
            .andExpect(status().isOk())
            .andDo(document(
                "running-crew/delete",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()))
            )
        ;
    }

    @DisplayName("러닝크루를 시작한다")
    @Test
    void start() throws Exception {
        final long runningCrewId = 1L;
        final String accessToken = "accessToken";

        mockMvc.perform(post(BASIC_URL + "/" + runningCrewId + "/start")
                .header("Authorization", accessToken))
            .andExpect(status().isOk())
            .andDo(document(
                "running-crew/start",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()))
            )
        ;
    }

    @DisplayName("러닝크루를 종료한다")
    @Test
    void end() throws Exception {
        final long runningCrewId = 1L;
        final String accessToken = "accessToken";

        mockMvc.perform(post(BASIC_URL + "/" + runningCrewId + "/end")
                .header("Authorization", accessToken))
            .andExpect(status().isOk())
            .andDo(document(
                "running-crew/end",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()))
            )
        ;
    }
}
