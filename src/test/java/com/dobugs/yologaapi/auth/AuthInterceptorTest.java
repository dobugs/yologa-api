package com.dobugs.yologaapi.auth;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.dobugs.yologaapi.auth.dto.response.AuthorizationResponse;
import com.dobugs.yologaapi.auth.support.DobugsConnector;

@WebMvcTest(FakeController.class)
@DisplayName("Auth 인터셉터 테스트")
class AuthInterceptorTest {

    private static final String BASIC_URL = "/api";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DobugsConnector dobugsConnector;

    @MockBean
    private TokenExtractor tokenExtractor;

    @DisplayName("@Authorized 어노테이션이 없는 경우 테스트")
    @Nested
    public class hasNotAuthorizedAnnotation {

        private static final String URL = BASIC_URL + "/hasNotAnnotation";

        @DisplayName("@Authorized 어노테이션이 없을 경우 두벅스 서버에게 요청을 보내지 않는다")
        @Test
        void success() throws Exception {
            mockMvc.perform(get(URL))
                .andExpect(status().isOk())
            ;
        }
    }

    @DisplayName("@Authorized 어노테이션이 있는 경우 테스트")
    @Nested
    public class hasAuthorizedAnnotation {

        private static final String URL = BASIC_URL + "/hasAuthorizedAnnotation";

        @DisplayName("@Authorized 어노테이션이 있을 경우 두벅스 서버에게 요청을 보낸다")
        @Test
        void success() throws Exception {
            final ResponseEntity response = mock(ResponseEntity.class);
            given(response.getStatusCode()).willReturn(HttpStatus.OK);
            given(dobugsConnector.post(any(), any())).willReturn(response);

            mockMvc.perform(get(URL)
                    .header("Authorization", "token"))
                .andExpect(status().isOk())
            ;
        }

        @DisplayName("두벅스 서버의 응답이 2XX 이 아닐 경우 예외가 발생한다")
        @Test
        void statusIsNot2XX() throws Exception {
            final String responseMessage = "message";

            final ResponseEntity response = mock(ResponseEntity.class);
            given(response.getStatusCode()).willReturn(HttpStatus.UNAUTHORIZED);
            given(response.getBody()).willReturn(new AuthorizationResponse(responseMessage));
            given(dobugsConnector.post(any(), any())).willReturn(response);

            mockMvc.perform(get(URL)
                    .header("Authorization", "token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString(responseMessage)))
            ;
        }

        @DisplayName("두벅스 서버의 응답이 2XX 이 아니면서 Body 가 null 일 경우 욜로가 서버에서 지정한 메시지를 포함한 예외가 발생한다")
        @Test
        void statusIsNot2XXAndBodyIsNull() throws Exception {
            final ResponseEntity response = mock(ResponseEntity.class);
            given(response.getStatusCode()).willReturn(HttpStatus.UNAUTHORIZED);
            given(response.getBody()).willReturn(null);
            given(dobugsConnector.post(any(), any())).willReturn(response);

            mockMvc.perform(get(URL)
                    .header("Authorization", "token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString("두벅스 서버와의 연결에 실패하였습니다.")))
            ;
        }
    }
}
