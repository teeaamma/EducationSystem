package com.course.krch.educationsystem;

import com.course.krch.educationsystem.dto.request.GroupRequestDto;
import com.course.krch.educationsystem.dto.response.GroupResponseDto;
import com.course.krch.educationsystem.dto.response.StudentShortResponseDto;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupIntegrationTest extends AbstractIntegrationTest {

    @Test
    @Sql("/sql/group/list.sql")
    void getAll_defaultPagination() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/groups", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(5);
        assertThat(json.read("$.number", Integer.class)).isZero();
        assertThat(json.read("$.size", Integer.class)).isEqualTo(20);
    }

    @Test
    @Sql("/sql/group/list.sql")
    void getAll_requestedPage() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/groups?page={page}&size={size}&sort={sort}",
                String.class,
                2, 1, "id,asc"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(1);
        assertThat(json.read("$.number", Integer.class)).isEqualTo(2);
        assertThat(json.read("$.size", Integer.class)).isEqualTo(1);
        assertThat(json.read("$.content[0].id", String.class))
                .isEqualTo("33333333-3333-4333-3333-333333333333");
    }

    @Test
    @Sql("/sql/group/list.sql")
    void getAll_lastPartialPage() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/groups?page={page}&size={size}&sort={sort}",
                String.class,
                2, 2, "id,asc"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(1);
        assertThat(json.read("$.number", Integer.class)).isEqualTo(2);
        assertThat(json.read("$.size", Integer.class)).isEqualTo(2);
        assertThat(json.read("$.last", Boolean.class)).isTrue();
        assertThat(json.read("$.content[0].id", String.class))
                .isEqualTo("55555555-5555-4555-5555-555555555555");
    }

    @Test
    @Sql("/sql/group/list.sql")
    void getAll_Filter() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/groups?name={name}",
                String.class,
                "a"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(2);
        assertThat(json.<List<String>>read("$.content[*].name"))
                .containsExactlyInAnyOrder("Java group", "Kafka group");
    }

    @Test
    @Sql("/sql/group/list.sql")
    void getAll_noFilterMatches() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/groups?name={name}",
                String.class,
                "фильтр"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.content", List.class)).hasSize(0);
        assertThat(json.read("$.numberOfElements", Integer.class)).isEqualTo(0);
    }

    @Test
    @Sql("/sql/group/list.sql")
    void getAll_filteredPagination() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/groups?page={page}&size={size}&sort={sort}&name={name}",
                String.class,
                1, 1, "id,asc", "a"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.content", List.class)).hasSize(1);
        assertThat(json.read("$.number", Integer.class)).isEqualTo(1);
        assertThat(json.read("$.size", Integer.class)).isEqualTo(1);
        assertThat(json.read("$.totalElements", Integer.class)).isEqualTo(2);
        assertThat(json.read("$.content[0].name", String.class))
                .isEqualTo("Kafka group");
    }

    @Test
    @Sql("/sql/group/item.sql")
    void getById_existingId() {
        ResponseEntity<GroupResponseDto> response = restTemplate.getForEntity(
                "/api/v1/groups/11111111-1111-4111-1111-111111111111", GroupResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        GroupResponseDto group = response.getBody();

        assertThat(group.id()).isEqualTo(UUID.fromString("11111111-1111-4111-1111-111111111111"));
        assertThat(group.name()).isEqualTo("Java group");
    }

    @Test
    void getById_nonExistingId() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/groups/11111111-1111-4111-1111-111111111111", String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Группы с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    void getById_invalidIdFormat() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/groups/invalid-format", String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Неправильный формат аргумента id"
        );
    }

    @Test
    @Sql("/sql/group/deleted-item.sql")
    void getById_deletedId() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/groups/11111111-1111-4111-1111-111111111111", String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Группы с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    void create_validRequestWithoutGroups() {
        GroupRequestDto request = new GroupRequestDto(
          "Java group",
          new ArrayList<>()
        );

        ResponseEntity<GroupResponseDto> response = restTemplate.postForEntity(
                "/api/v1/groups",
                request,
                GroupResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        GroupResponseDto group = response.getBody();

        assertThat(group.id()).isNotNull();
        assertThat(group.name()).isEqualTo("Java group");
        assertThat(group.students()).isEmpty();
    }

    @Test
    @Sql("/sql/group/group-with-students.sql")
    void create_validRequestWithGroups() {
        GroupRequestDto request = new GroupRequestDto(
                "Java group",
                List.of(
                        UUID.fromString("66666666-6666-4666-6666-666666666666"),
                        UUID.fromString("77777777-7777-4777-7777-777777777777")
                )
        );

        ResponseEntity<GroupResponseDto> response = restTemplate.postForEntity(
                "/api/v1/groups",
                request,
                GroupResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        GroupResponseDto group = response.getBody();

        assertThat(group.id()).isNotNull();
        assertThat(group.name()).isEqualTo("Java group");
        assertThat(group.students())
                .extracting(StudentShortResponseDto::id)
                .containsExactlyInAnyOrder(
                        UUID.fromString("66666666-6666-4666-6666-666666666666"),
                        UUID.fromString("77777777-7777-4777-7777-777777777777")
                );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void create_invalidName(String name) {
        GroupRequestDto request = new GroupRequestDto(
                name,
                new ArrayList<>()
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/groups",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(1);
        assertThat(json.read("$.violations[0].fieldName", String.class)).isEqualTo("name");
        assertThat(json.read("$.violations[0].message", String.class)).isEqualTo("не должно быть пустым");
    }

    @Test
    void create_invalidStudents() {
        GroupRequestDto request = new GroupRequestDto(
                "Java group",
                null
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/groups",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(1);
        assertThat(json.read("$.violations[0].fieldName", String.class)).isEqualTo("studentIds");
        assertThat(json.read("$.violations[0].message", String.class)).isEqualTo("не должно равняться null");
    }

    @Test
    void create_nonExistingStudent() {
        GroupRequestDto request = new GroupRequestDto(
                "Java group",
                List.of(UUID.fromString("66666666-6666-4666-6666-666666666666"))
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/groups",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Не найдены студенты с id: 66666666-6666-4666-6666-666666666666"
        );
    }

    @Test
    void create_invalidFields() {
        GroupRequestDto request = new GroupRequestDto("", null);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/groups",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(2);
        assertThat(json.<List<String>>read("$.violations[*].fieldName"))
                .containsExactlyInAnyOrder("name", "studentIds");
        assertThat(json.<List<String>>read("$.violations[*].message"))
                .containsExactlyInAnyOrder(
                        "не должно быть пустым",
                        "не должно равняться null"
                );
    }

    @Test
    @Sql({
            "/sql/group/item.sql",
            "/sql/group/group-with-students.sql"
    })
    void update_validRequest() {
        GroupRequestDto request = new GroupRequestDto(
                "Spring group",
                List.of(
                        UUID.fromString("66666666-6666-4666-6666-666666666666"),
                        UUID.fromString("77777777-7777-4777-7777-777777777777")
                )
        );

        ResponseEntity<GroupResponseDto> response = restTemplate.exchange(
                "/api/v1/groups/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                GroupResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        GroupResponseDto group = response.getBody();

        assertThat(group.id()).isEqualTo(UUID.fromString("11111111-1111-4111-1111-111111111111"));
        assertThat(group.name()).isEqualTo("Spring group");
        assertThat(group.students())
                .extracting(StudentShortResponseDto::id)
                .containsExactlyInAnyOrder(
                        UUID.fromString("66666666-6666-4666-6666-666666666666"),
                        UUID.fromString("77777777-7777-4777-7777-777777777777")
                );
    }

    @Test
    void update_nonExistingId() {
        GroupRequestDto request = new GroupRequestDto(
                "Spring group",
                new ArrayList<>()
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/groups/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Группы с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    void update_invalidIdFormat() {
        GroupRequestDto request = new GroupRequestDto(
                "Spring group",
                new ArrayList<>()
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/groups/invalid-format",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Неправильный формат аргумента id"
        );
    }

    @Test
    @Sql("/sql/group/item.sql")
    void update_nonExistingStudent() {
        GroupRequestDto request = new GroupRequestDto(
                "Spring group",
                List.of(
                        UUID.fromString("66666666-6666-4666-6666-666666666666")
                )
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/groups/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Не найдены студенты с id: 66666666-6666-4666-6666-666666666666"
        );
    }

    @Test
    @Sql("/sql/group/item.sql")
    void update_invalidBody() {
        GroupRequestDto request = new GroupRequestDto(
                "",
                new ArrayList<>()
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/groups/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(1);
        assertThat(json.read("$.violations[0].fieldName", String.class)).isEqualTo("name");
        assertThat(json.read("$.violations[0].message", String.class)).isEqualTo("не должно быть пустым");
    }

    @Test
    @Sql("/sql/group/deleted-item.sql")
    void update_deletedId() {
        GroupRequestDto request = new GroupRequestDto(
                "Spring group",
                new ArrayList<>()
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/groups/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Группы с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    @Sql("/sql/group/item.sql")
    void delete_existingIdWithoutStudents() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/groups/11111111-1111-4111-1111-111111111111",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Boolean isDeleted = jdbcTemplate.queryForObject(
                "SELECT is_deleted FROM groups WHERE id = ?",
                Boolean.class,
                UUID.fromString("11111111-1111-4111-1111-111111111111")
        );

        assertThat(isDeleted).isTrue();
    }

    @Test
    @Sql("/sql/group/groups-with-students.sql")
    void delete_existingIdWithStudentsHavingOtherGroups() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/groups/22222222-2222-4222-2222-222222222222",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Boolean isDeleted = jdbcTemplate.queryForObject(
                "SELECT is_deleted FROM groups WHERE id = ?",
                Boolean.class,
                UUID.fromString("22222222-2222-4222-2222-222222222222")
        );

        assertThat(isDeleted).isTrue();

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM group_student WHERE group_id = ?",
                Integer.class,
                UUID.fromString("22222222-2222-4222-2222-222222222222")
        );

        assertThat(count).isZero();
    }

    @Test
    @Sql("/sql/group/group-with-students.sql")
    void delete_existingIdWithStudentsHavingNoOtherGroups() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/groups/22222222-2222-4222-2222-222222222222",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "У студента должна быть минимум одна группа, id: 66666666-6666-4666-6666-666666666666"
        );

        Boolean isDeleted = jdbcTemplate.queryForObject(
                "SELECT is_deleted FROM groups WHERE id = ?",
                Boolean.class,
                UUID.fromString("22222222-2222-4222-2222-222222222222")
        );

        assertThat(isDeleted).isFalse();

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM group_student WHERE group_id = ?",
                Integer.class,
                UUID.fromString("22222222-2222-4222-2222-222222222222")
        );

        assertThat(count).isEqualTo(2);
    }

    @Test
    @Sql("/sql/group/group-with-students-in-one-and-two-groups.sql")
    void delete_existingIdWithSomeStudentsHavingNoOtherGroups() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/groups/22222222-2222-4222-2222-222222222222",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "У студента должна быть минимум одна группа, id: 77777777-7777-4777-7777-777777777777"
        );

        Boolean isDeleted = jdbcTemplate.queryForObject(
                "SELECT is_deleted FROM groups WHERE id = ?",
                Boolean.class,
                UUID.fromString("22222222-2222-4222-2222-222222222222")
        );

        assertThat(isDeleted).isFalse();

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM group_student WHERE group_id = ?",
                Integer.class,
                UUID.fromString("22222222-2222-4222-2222-222222222222")
        );

        assertThat(count).isEqualTo(2);
    }

    @Test
    void delete_nonExistingId() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/groups/11111111-1111-4111-1111-111111111111",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Группы с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    void delete_invalidIdFormat() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/groups/invalid-format",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Неправильный формат аргумента id"
        );
    }

    @Test
    @Sql({
            "/sql/group/item.sql",
            "/sql/group/schedules.sql"
    })
    void delete_withExistingSchedules() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/groups/11111111-1111-4111-1111-111111111111",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Нельзя удалить группу с id 11111111-1111-4111-1111-111111111111: у нее есть активные занятия в расписании"
        );
    }

    @Test
    @Sql({
            "/sql/group/item.sql",
            "/sql/group/schedules.sql"
    })
    void getSchedules_defaultPagination() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/groups/11111111-1111-4111-1111-111111111111/schedules",
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.content", List.class)).hasSize(5);
        assertThat(json.read("$.number", Integer.class)).isZero();
        assertThat(json.read("$.size", Integer.class)).isEqualTo(20);
    }

    @Test
    @Sql({
            "/sql/group/item.sql",
            "/sql/group/schedules.sql"
    })
    void getSchedules_requestedPage() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/groups/11111111-1111-4111-1111-111111111111/schedules?page={page}&size={size}&sort={sort}",
                String.class,
                2, 1, "id,asc"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(1);
        assertThat(json.read("$.number", Integer.class)).isEqualTo(2);
        assertThat(json.read("$.size", Integer.class)).isEqualTo(1);
        assertThat(json.read("$.content[0].id", String.class))
                .isEqualTo("aaaaaaaa-aaaa-4aaa-aaaa-aaaaaaaaaaaa");
    }

    @Test
    @Sql({
            "/sql/group/item.sql",
            "/sql/group/schedules.sql"
    })
    void getSchedules_lastPartialPage() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/groups/11111111-1111-4111-1111-111111111111/schedules?page={page}&size={size}&sort={sort}",
                String.class,
                2, 2, "id,asc"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.content", List.class)).hasSize(1);
        assertThat(json.read("$.number", Integer.class)).isEqualTo(2);
        assertThat(json.read("$.size", Integer.class)).isEqualTo(2);
        assertThat(json.read("$.last", Boolean.class)).isTrue();
        assertThat(json.read("$.content[0].id", String.class))
                .isEqualTo("cccccccc-cccc-4ccc-cccc-cccccccccccc");
    }
}
