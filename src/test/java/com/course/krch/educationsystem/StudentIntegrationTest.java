package com.course.krch.educationsystem;

import com.course.krch.educationsystem.dto.request.StudentRequestDto;
import com.course.krch.educationsystem.dto.response.GroupShortResponseDto;
import com.course.krch.educationsystem.dto.response.StudentResponseDto;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentIntegrationTest extends AbstractIntegrationTest {

    @Test
    @Sql("/sql/student/list.sql")
    void getAll_defaultPagination() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/students", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(5);
        assertThat(json.read("$.number", Integer.class)).isZero();
        assertThat(json.read("$.size", Integer.class)).isEqualTo(20);
    }

    @Test
    @Sql("/sql/student/list.sql")
    void getAll_requestedPage() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/students?page={page}&size={size}&sort={sort}",
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
    @Sql("/sql/student/list.sql")
    void getAll_lastPartialPage() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/students?page={page}&size={size}&sort={sort}",
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
    @Sql("/sql/student/list.sql")
    void getAll_firstFilter() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/students?firstName={firstName}",
                String.class,
                "ор"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(2);
        assertThat(json.<List<String>>read("$.content[*].firstName"))
                .containsExactlyInAnyOrder("Борис", "Егор");
    }

    @Test
    @Sql("/sql/student/list.sql")
    void getAll_secondFilter() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/students?lastName={lastName}",
                String.class,
                "ров"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(2);
        assertThat(json.<List<String>>read("$.content[*].lastName"))
                .containsExactlyInAnyOrder("Владимиров", "Егоров");
    }

    @Test
    @Sql("/sql/student/list.sql")
    void getAll_bothFilters() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/students?firstName={firstName}&lastName={lastName}",
                String.class,
                "ор", "ров"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(1);
        assertThat(json.read("$.content[0].id", String.class))
                .isEqualTo("55555555-5555-4555-5555-555555555555");
    }

    @Test
    @Sql("/sql/student/list.sql")
    void getAll_noFilterMatches() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/students?firstName={firstName}&lastName={lastName}",
                String.class,
                "фильтр", "фильтр"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.content", List.class)).hasSize(0);
        assertThat(json.read("$.numberOfElements", Integer.class)).isEqualTo(0);
    }

    @Test
    @Sql("/sql/student/list.sql")
    void getAll_filteredPagination() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/students?page={page}&size={size}&sort={sort}&firstName={firstName}&lastName={lastName}",
                String.class,
                1, 1, "id,asc", "ор", "ов"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.content", List.class)).hasSize(1);
        assertThat(json.read("$.number", Integer.class)).isEqualTo(1);
        assertThat(json.read("$.size", Integer.class)).isEqualTo(1);
        assertThat(json.read("$.totalElements", Integer.class)).isEqualTo(2);
        assertThat(json.read("$.content[0].id", String.class))
                .isEqualTo("55555555-5555-4555-5555-555555555555");
    }

    @Test
    @Sql("/sql/student/item.sql")
    void getById_existingId() {
        ResponseEntity<StudentResponseDto> response = restTemplate.getForEntity(
                "/api/v1/students/11111111-1111-4111-1111-111111111111", StudentResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        StudentResponseDto student = response.getBody();

        assertThat(student.id()).isEqualTo(UUID.fromString("11111111-1111-4111-1111-111111111111"));
        assertThat(student.firstName()).isEqualTo("Андрей");
        assertThat(student.lastName()).isEqualTo("Андреев");
    }

    @Test
    void getById_nonExistingId() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/students/11111111-1111-4111-1111-111111111111", String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Студента с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    void getById_invalidIdFormat() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/students/invalid-format", String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Неправильный формат аргумента id"
        );
    }

    @Test
    @Sql("/sql/student/deleted-item.sql")
    void getById_deletedId() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/students/11111111-1111-4111-1111-111111111111", String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Студента с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    @Sql("/sql/student/groups.sql")
    void create_validRequest() {
        StudentRequestDto request = new StudentRequestDto(
                "Андрей",
                "Андреев",
                List.of(
                        UUID.fromString("77777777-7777-4777-7777-777777777777"),
                        UUID.fromString("88888888-8888-4888-8888-888888888888")
                )
        );

        ResponseEntity<StudentResponseDto> response = restTemplate.postForEntity(
                "/api/v1/students",
                request,
                StudentResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        StudentResponseDto student = response.getBody();

        assertThat(student.id()).isNotNull();
        assertThat(student.firstName()).isEqualTo("Андрей");
        assertThat(student.lastName()).isEqualTo("Андреев");
        assertThat(student.groups())
                .extracting(GroupShortResponseDto::id)
                .containsExactlyInAnyOrder(
                        UUID.fromString("77777777-7777-4777-7777-777777777777"),
                        UUID.fromString("88888888-8888-4888-8888-888888888888")
                );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @Sql("/sql/student/groups.sql")
    void create_invalidFirstName(String firstName) {
        StudentRequestDto request = new StudentRequestDto(
                firstName,
                "Андреев",
                List.of(
                        UUID.fromString("77777777-7777-4777-7777-777777777777"),
                        UUID.fromString("88888888-8888-4888-8888-888888888888")
                )
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/students",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(1);
        assertThat(json.read("$.violations[0].fieldName", String.class)).isEqualTo("firstName");
        assertThat(json.read("$.violations[0].message", String.class)).isEqualTo("не должно быть пустым");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    @Sql("/sql/student/groups.sql")
    void create_invalidLastName(String lastName) {
        StudentRequestDto request = new StudentRequestDto(
                "Андрей",
                lastName,
                List.of(
                        UUID.fromString("77777777-7777-4777-7777-777777777777"),
                        UUID.fromString("88888888-8888-4888-8888-888888888888")
                )
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/students",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(1);
        assertThat(json.read("$.violations[0].fieldName", String.class)).isEqualTo("lastName");
        assertThat(json.read("$.violations[0].message", String.class)).isEqualTo("не должно быть пустым");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void create_invalidGroups(List<UUID> groupIds) {
        StudentRequestDto request = new StudentRequestDto(
                "Андрей",
                "Андреев",
                groupIds
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/students",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(1);
        assertThat(json.read("$.violations[0].fieldName", String.class)).isEqualTo("groupIds");
        assertThat(json.read("$.violations[0].message", String.class))
                .containsAnyOf("не должно равняться null", "не должно быть пустым");
    }

    @Test
    void create_nonExistingGroup() {
        StudentRequestDto request = new StudentRequestDto(
                "Андрей",
                "Андреев",
                List.of(UUID.fromString("66666666-6666-4666-6666-666666666666"))
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/students",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Не найдены группы с id: 66666666-6666-4666-6666-666666666666"
        );
    }

    @Test
    void create_invalidFields() {
        StudentRequestDto request = new StudentRequestDto("", "", null);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/students",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(3);
        assertThat(json.<List<String>>read("$.violations[*].fieldName"))
                .containsExactlyInAnyOrder("firstName", "lastName", "groupIds");
        assertThat(json.<List<String>>read("$.violations[*].message"))
                .allMatch(message -> message.equals("не должно быть пустым"));
    }

    @Test
    @Sql({
            "/sql/student/item.sql",
            "/sql/student/groups.sql"
    })
    void update_validRequest() {
        StudentRequestDto request = new StudentRequestDto(
                "Андрей",
                "Андреев",
                List.of(
                        UUID.fromString("77777777-7777-4777-7777-777777777777"),
                        UUID.fromString("88888888-8888-4888-8888-888888888888")
                )
        );

        ResponseEntity<StudentResponseDto> response = restTemplate.exchange(
                "/api/v1/students/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                StudentResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        StudentResponseDto student = response.getBody();

        assertThat(student.id()).isEqualTo(UUID.fromString("11111111-1111-4111-1111-111111111111"));
        assertThat(student.firstName()).isEqualTo("Андрей");
        assertThat(student.lastName()).isEqualTo("Андреев");
        assertThat(student.groups())
                .extracting(GroupShortResponseDto::id)
                .containsExactlyInAnyOrder(
                        UUID.fromString("77777777-7777-4777-7777-777777777777"),
                        UUID.fromString("88888888-8888-4888-8888-888888888888")
                );
    }

    @Test
    @Sql("/sql/student/groups.sql")
    void update_nonExistingId() {
        StudentRequestDto request = new StudentRequestDto(
                "Андрей",
                "Андреев",
                List.of(
                        UUID.fromString("77777777-7777-4777-7777-777777777777"),
                        UUID.fromString("88888888-8888-4888-8888-888888888888")
                )
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/students/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Студента с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    void update_invalidIdFormat() {
        StudentRequestDto request = new StudentRequestDto(
                "Андрей",
                "Андреев",
                List.of(
                        UUID.fromString("77777777-7777-4777-7777-777777777777"),
                        UUID.fromString("88888888-8888-4888-8888-888888888888")
                )
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/students/invalid-format",
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
    @Sql("/sql/student/item.sql")
    void update_nonExistingGroup() {
        StudentRequestDto request = new StudentRequestDto(
                "Андрей",
                "Андреев",
                List.of(
                        UUID.fromString("77777777-7777-4777-7777-777777777777")
                )
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/students/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Не найдены группы с id: 77777777-7777-4777-7777-777777777777"
        );
    }

    @Test
    @Sql({
            "/sql/student/item.sql",
            "/sql/student/groups.sql"
    })
    void update_invalidBody() {
        StudentRequestDto request = new StudentRequestDto(
                "",
                "Андреев",
                List.of(
                        UUID.fromString("77777777-7777-4777-7777-777777777777"),
                        UUID.fromString("88888888-8888-4888-8888-888888888888")
                )
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/students/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(1);
        assertThat(json.read("$.violations[0].fieldName", String.class)).isEqualTo("firstName");
        assertThat(json.read("$.violations[0].message", String.class)).isEqualTo("не должно быть пустым");
    }

    @Test
    @Sql({
            "/sql/student/deleted-item.sql",
            "/sql/student/groups.sql"
    })
    void update_deletedId() {
        StudentRequestDto request = new StudentRequestDto(
                "Андрей",
                "Андреев",
                List.of(
                        UUID.fromString("77777777-7777-4777-7777-777777777777"),
                        UUID.fromString("88888888-8888-4888-8888-888888888888")
                )
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/students/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Студента с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    @Sql("/sql/student/item.sql")
    void delete_existingId() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/students/11111111-1111-4111-1111-111111111111",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Boolean isDeleted = jdbcTemplate.queryForObject(
                "SELECT is_deleted FROM students WHERE id = ?",
                Boolean.class,
                UUID.fromString("11111111-1111-4111-1111-111111111111")
        );

        assertThat(isDeleted).isTrue();
    }

    @Test
    void delete_nonExistingId() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/students/11111111-1111-4111-1111-111111111111",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Студента с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    void delete_invalidIdFormat() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/students/invalid-format",
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
}
