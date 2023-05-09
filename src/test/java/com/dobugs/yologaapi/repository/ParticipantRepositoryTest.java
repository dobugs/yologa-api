package com.dobugs.yologaapi.repository;

import static com.dobugs.yologaapi.domain.runningcrew.fixture.RunningCrewFixture.createRunningCrew;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dobugs.yologaapi.domain.runningcrew.ParticipantType;
import com.dobugs.yologaapi.domain.runningcrew.RunningCrew;
import com.dobugs.yologaapi.repository.dto.response.ParticipantDto;

import jakarta.persistence.EntityManager;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@DisplayName("Participant 레포지토리 테스트")
class ParticipantRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private RunningCrewRepository runningCrewRepository;

    @DisplayName("러닝크루의 참여자 목록 정보 조회")
    @Nested
    public class findParticipants {

        private static final Long HOST_ID = 1L;
        private static final List<Long> MEMBERS = List.of(2L, 3L);

        @BeforeEach
        void setUp() {
            entityManager
                .createNativeQuery("drop table if exists member CASCADE;\n"
                + "\n"
                + "CREATE TABLE member (\n"
                + "    id int NOT NULL AUTO_INCREMENT,\n"
                + "    oauth_id varchar(255) NOT NULL,\n"
                + "    nickname varchar(50),\n"
                + "    phone_number varchar(50),\n"
                + "    resource_id int,\n"
                + "    archived boolean,\n"
                + "    archived_at datetime,\n"
                + "    created_at datetime,\n"
                + "    updated_at datetime,\n"
                + "    CONSTRAINT member_PK PRIMARY KEY(id)\n"
                + ");")
                .executeUpdate();
        }

        @DisplayName("러닝크루의 참여자 목록 정보를 조회한다")
        @Test
        void success() {
            final RunningCrew runningCrew = createRunningCrew(HOST_ID);
            for (final Long memberId : MEMBERS) {
                runningCrew.participate(memberId);
                runningCrew.accept(HOST_ID, memberId);
            }
            final RunningCrew savedRunningCrew = runningCrewRepository.save(runningCrew);

            final Pageable pageable = Pageable.unpaged();
            final Page<ParticipantDto> response = participantRepository.findParticipants(savedRunningCrew.getId(), ParticipantType.PARTICIPATING.getSavedName(), pageable);

            assertThat(response).hasSize(MEMBERS.size() + 1);
        }
    }
}
