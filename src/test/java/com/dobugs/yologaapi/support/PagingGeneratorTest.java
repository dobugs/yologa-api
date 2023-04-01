package com.dobugs.yologaapi.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

@DisplayName("PagingGenerator 테스트")
class PagingGeneratorTest {

    private PagingGenerator pagingGenerator;

    @BeforeEach
    void setUp() {
        pagingGenerator = new PagingGenerator();
    }

    @DisplayName("Pagable 객체를 생성한다")
    @Nested
    public class from {

        @DisplayName("page 와 size 의 값이 모두 존재하는 경우 Pagable 객체를 생성한다")
        @Test
        void success() {
            final Pageable pageable = pagingGenerator.from(0, 10);

            assertThat(pageable.isPaged()).isTrue();
        }

        @DisplayName("page 와 size 의 값이 모두 존재하지 않는 경우 빈 Pagable 객체를 생성한다")
        @Test
        void pageAndSizeIsNull() {
            final Pageable pageable = pagingGenerator.from(null, null);

            assertThat(pageable.isUnpaged()).isTrue();
        }

        @DisplayName("page 는 null 이고 size 의 값이 존재할 경우 예외가 발생한다")
        @Test
        void pageIsNull() {
            assertThatThrownBy(() -> pagingGenerator.from(null, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("page 값을 입력해주세요.");
        }

        @DisplayName("page 는 값이 존재하고 size 는 null 일 경우 예외가 발생한다")
        @Test
        void sizeIsNull() {
            assertThatThrownBy(() -> pagingGenerator.from(0, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("size 값을 입력해주세요.");
        }
    }
}
