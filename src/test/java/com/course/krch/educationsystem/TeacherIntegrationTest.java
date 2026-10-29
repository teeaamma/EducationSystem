package com.course.krch.educationsystem;

import com.course.krch.educationsystem.dto.request.TeacherRequestDto;
import com.course.krch.educationsystem.dto.response.TeacherResponseDto;
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

public class TeacherIntegrationTest extends AbstractIntegrationTest{

    @Test
    @Sql("/sql/teacher/list.sql")
    void getAll_defaultPagination() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/teachers", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(5);
        assertThat(json.read("$.number", Integer.class)).isZero();
        assertThat(json.read("$.size", Integer.class)).isEqualTo(20);
    }

    @Test
    @Sql("/sql/teacher/list.sql")
    void getAll_requestedPage() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/teachers?page={page}&size={size}&sort={sort}",
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
    @Sql("/sql/teacher/list.sql")
    void getAll_lastPartialPage() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/teachers?page={page}&size={size}&sort={sort}",
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
    @Sql("/sql/teacher/list.sql")
    void getAll_firstFilter() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/teachers?firstName={firstName}",
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
    @Sql("/sql/teacher/list.sql")
    void getAll_secondFilter() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/teachers?lastName={lastName}",
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
    @Sql("/sql/teacher/list.sql")
    void getAll_bothFilters() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/teachers?firstName={firstName}&lastName={lastName}",
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
    @Sql("/sql/teacher/list.sql")
    void getAll_noFilterMatches() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/teachers?firstName={firstName}&lastName={lastName}",
                String.class,
                "фильтр", "фильтр"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.content", List.class)).hasSize(0);
        assertThat(json.read("$.numberOfElements", Integer.class)).isEqualTo(0);
    }

    @Test
    @Sql("/sql/teacher/list.sql")
    void getAll_filteredPagination() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/teachers?page={page}&size={size}&sort={sort}&firstName={firstName}&lastName={lastName}",
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
    @Sql("/sql/teacher/item.sql")
    void getById_existingId() {
        ResponseEntity<TeacherResponseDto> response = restTemplate.getForEntity(
                "/api/v1/teachers/11111111-1111-4111-1111-111111111111", TeacherResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        TeacherResponseDto teacher = response.getBody();

        assertThat(teacher.id()).isEqualTo(UUID.fromString("11111111-1111-4111-1111-111111111111"));
        assertThat(teacher.firstName()).isEqualTo("Андрей");
        assertThat(teacher.lastName()).isEqualTo("Андреев");
    }

    @Test
    void getById_nonExistingId() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/teachers/11111111-1111-4111-1111-111111111111", String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Преподавателя с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    void getById_invalidIdFormat() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/teachers/invalid-format", String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Неправильный формат аргумента id"
        );
    }

    @Test
    @Sql("/sql/teacher/deleted-item.sql")
    void getById_deletedId() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/teachers/11111111-1111-4111-1111-111111111111", String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Преподавателя с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    void create_validRequest() {
        TeacherRequestDto request = new TeacherRequestDto("Андрей", "Андреев");

        ResponseEntity<TeacherResponseDto> response = restTemplate.postForEntity(
                "/api/v1/teachers",
                request,
                TeacherResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        TeacherResponseDto teacher = response.getBody();

        assertThat(teacher.id()).isNotNull();
        assertThat(teacher.firstName()).isEqualTo("Андрей");
        assertThat(teacher.lastName()).isEqualTo("Андреев");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void create_invalidFirstName(String firstName) {
        TeacherRequestDto request = new TeacherRequestDto(firstName, "Андреев");
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/teachers",
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
    void create_invalidLastName(String lastName) {
        TeacherRequestDto request = new TeacherRequestDto("Андрей", lastName);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/teachers",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(1);
        assertThat(json.read("$.violations[0].fieldName", String.class)).isEqualTo("lastName");
        assertThat(json.read("$.violations[0].message", String.class)).isEqualTo("не должно быть пустым");
    }

    @Test
    void create_invalidFields() {
        TeacherRequestDto request = new TeacherRequestDto("", "");
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/teachers",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(2);
        assertThat(json.<List<String>>read("$.violations[*].fieldName"))
                .containsExactlyInAnyOrder("firstName", "lastName");
        assertThat(json.<List<String>>read("$.violations[*].message"))
                .allMatch(message -> message.equals("не должно быть пустым"));
    }

    @Test
    @Sql("/sql/teacher/item.sql")
    void update_validRequest() {
        TeacherRequestDto request = new TeacherRequestDto("Антон", "Антонов");

        ResponseEntity<TeacherResponseDto> response = restTemplate.exchange(
                "/api/v1/teachers/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                TeacherResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        TeacherResponseDto teacher = response.getBody();

        assertThat(teacher.id()).isEqualTo(UUID.fromString("11111111-1111-4111-1111-111111111111"));
        assertThat(teacher.firstName()).isEqualTo("Антон");
        assertThat(teacher.lastName()).isEqualTo("Антонов");
    }

    @Test
    void update_nonExistingId() {
        TeacherRequestDto request = new TeacherRequestDto("Антон", "Антонов");

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/teachers/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Преподавателя с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    void update_invalidIdFormat() {
        TeacherRequestDto request = new TeacherRequestDto("Антон", "Антонов");

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/teachers/invalid-format",
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
    @Sql("/sql/teacher/item.sql")
    void update_invalidBody() {
        TeacherRequestDto request = new TeacherRequestDto("", "Антонов");
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/teachers/11111111-1111-4111-1111-111111111111",
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
    @Sql("/sql/teacher/deleted-item.sql")
    void update_deletedId() {
        TeacherRequestDto request = new TeacherRequestDto("Антон", "Антонов");

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/teachers/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Преподавателя с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    @Sql("/sql/teacher/item.sql")
    void delete_existingId() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/teachers/11111111-1111-4111-1111-111111111111",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Boolean isDeleted = jdbcTemplate.queryForObject(
                "SELECT is_deleted FROM teachers WHERE id = ?",
                Boolean.class,
                UUID.fromString("11111111-1111-4111-1111-111111111111")
        );

        assertThat(isDeleted).isTrue();
    }

    @Test
    void delete_nonExistingId() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/teachers/11111111-1111-4111-1111-111111111111",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Преподавателя с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    void delete_invalidIdFormat() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/teachers/invalid-format",
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
            "/sql/teacher/item.sql",
            "/sql/teacher/schedules.sql"
    })
    void delete_withExistingSchedules() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/teachers/11111111-1111-4111-1111-111111111111",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Нельзя удалить преподавателя с id 11111111-1111-4111-1111-111111111111: у него есть активные занятия в расписании"
        );
    }

    @Test
    @Sql({
            "/sql/teacher/item.sql",
            "/sql/teacher/schedules.sql"
    })
    void getSchedules_defaultPagination() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/teachers/11111111-1111-4111-1111-111111111111/schedules",
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
            "/sql/teacher/item.sql",
            "/sql/teacher/schedules.sql"
    })
    void getSchedules_requestedPage() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/teachers/11111111-1111-4111-1111-111111111111/schedules?page={page}&size={size}&sort={sort}",
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
            "/sql/teacher/item.sql",
            "/sql/teacher/schedules.sql"
    })
    void getSchedules_lastPartialPage() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/teachers/11111111-1111-4111-1111-111111111111/schedules?page={page}&size={size}&sort={sort}",
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
