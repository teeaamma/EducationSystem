package com.course.krch.educationsystem;

import com.course.krch.educationsystem.dto.request.CourseRequestDto;
import com.course.krch.educationsystem.dto.response.CourseResponseDto;
import com.course.krch.educationsystem.dto.response.GroupShortResponseDto;
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

public class CourseIntegrationTest extends AbstractIntegrationTest {

    @Test
    @Sql("/sql/course/list.sql")
    void getAll_defaultPagination() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/courses", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(5);
        assertThat(json.read("$.number", Integer.class)).isZero();
        assertThat(json.read("$.size", Integer.class)).isEqualTo(20);
    }

    @Test
    @Sql("/sql/course/list.sql")
    void getAll_requestedPage() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/courses?page={page}&size={size}&sort={sort}",
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
    @Sql("/sql/course/list.sql")
    void getAll_lastPartialPage() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/courses?page={page}&size={size}&sort={sort}",
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
    @Sql("/sql/course/list.sql")
    void getAll_firstFilter() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/courses?name={name}",
                String.class,
                "a"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(2);
        assertThat(json.<List<String>>read("$.content[*].name"))
                .containsExactlyInAnyOrder("Java", "Kafka");
    }

    @Test
    @Sql("/sql/course/list.sql")
    void getAll_secondFilter() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/courses?description={description}",
                String.class,
                "backend"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(2);
        assertThat(json.<List<String>>read("$.content[*].name"))
                .containsExactlyInAnyOrder("Java", "Spring");
    }

    @Test
    @Sql("/sql/course/list.sql")
    void getAll_bothFilters() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/courses?name={name}&description={description}",
                String.class,
                "a", "backend"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(1);
        assertThat(json.read("$.content[0].name", String.class))
                .isEqualTo("Java");
    }

    @Test
    @Sql("/sql/course/list.sql")
    void getAll_noFilterMatches() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/courses?name={name}&description={description}",
                String.class,
                "фильтр", "фильтр"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.content", List.class)).hasSize(0);
        assertThat(json.read("$.numberOfElements", Integer.class)).isEqualTo(0);
    }

    @Test
    @Sql("/sql/course/list.sql")
    void getAll_filteredPagination() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/courses?page={page}&size={size}&sort={sort}&description={description}",
                String.class,
                1, 1, "id,asc", "backend"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.content", List.class)).hasSize(1);
        assertThat(json.read("$.number", Integer.class)).isEqualTo(1);
        assertThat(json.read("$.size", Integer.class)).isEqualTo(1);
        assertThat(json.read("$.totalElements", Integer.class)).isEqualTo(2);
        assertThat(json.read("$.content[0].name", String.class))
                .isEqualTo("Spring");
    }

    @Test
    @Sql("/sql/course/item.sql")
    void getById_existingId() {
        ResponseEntity<CourseResponseDto> response = restTemplate.getForEntity(
                "/api/v1/courses/11111111-1111-4111-1111-111111111111", CourseResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        CourseResponseDto course = response.getBody();

        assertThat(course.id()).isEqualTo(UUID.fromString("11111111-1111-4111-1111-111111111111"));
        assertThat(course.name()).isEqualTo("Java");
        assertThat(course.description()).isEqualTo("Backend development with Java");
    }

    @Test
    void getById_nonExistingId() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/courses/11111111-1111-4111-1111-111111111111", String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Курса с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    void getById_invalidIdFormat() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/courses/invalid-format", String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Неправильный формат аргумента id"
        );
    }

    @Test
    @Sql("/sql/course/deleted-item.sql")
    void getById_deletedId() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/courses/11111111-1111-4111-1111-111111111111", String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Курса с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    void create_validRequestWithoutGroups() {
        CourseRequestDto request = new CourseRequestDto(
                "Java",
                "Backend development with Java",
                new ArrayList<>()
        );

        ResponseEntity<CourseResponseDto> response = restTemplate.postForEntity(
                "/api/v1/courses",
                request,
                CourseResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        CourseResponseDto course = response.getBody();

        assertThat(course.id()).isNotNull();
        assertThat(course.name()).isEqualTo("Java");
        assertThat(course.description()).isEqualTo("Backend development with Java");
        assertThat(course.groups()).isEmpty();
    }

    @Test
    @Sql("/sql/course/groups.sql")
    void create_validRequestWithGroups() {
        CourseRequestDto request = new CourseRequestDto(
                "Java",
                "Backend development with Java",
                List.of(
                        UUID.fromString("66666666-6666-4666-6666-666666666666"),
                        UUID.fromString("77777777-7777-4777-7777-777777777777")
                )
        );

        ResponseEntity<CourseResponseDto> response = restTemplate.postForEntity(
                "/api/v1/courses",
                request,
                CourseResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        CourseResponseDto course = response.getBody();

        assertThat(course.id()).isNotNull();
        assertThat(course.name()).isEqualTo("Java");
        assertThat(course.description()).isEqualTo("Backend development with Java");
        assertThat(course.groups())
                .extracting(GroupShortResponseDto::id)
                .containsExactlyInAnyOrder(
                        UUID.fromString("66666666-6666-4666-6666-666666666666"),
                        UUID.fromString("77777777-7777-4777-7777-777777777777")
                );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void create_invalidName(String name) {
        CourseRequestDto request = new CourseRequestDto(
                name,
                "Backend development with Java",
                new ArrayList<>()
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/courses",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(1);
        assertThat(json.read("$.violations[0].fieldName", String.class)).isEqualTo("name");
        assertThat(json.read("$.violations[0].message", String.class)).isEqualTo("не должно быть пустым");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void create_invalidDescription(String description) {
        CourseRequestDto request = new CourseRequestDto(
                "Java",
                description,
                new ArrayList<>()
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/courses",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(1);
        assertThat(json.read("$.violations[0].fieldName", String.class)).isEqualTo("description");
        assertThat(json.read("$.violations[0].message", String.class)).isEqualTo("не должно быть пустым");
    }

    @Test
    void create_invalidGroups() {
        CourseRequestDto request = new CourseRequestDto(
                "Java",
                "Backend development with Java",
                null
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/courses",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(1);
        assertThat(json.read("$.violations[0].fieldName", String.class)).isEqualTo("groupIds");
        assertThat(json.read("$.violations[0].message", String.class)).isEqualTo("не должно равняться null");
    }

    @Test
    void create_nonExistingGroup() {
        CourseRequestDto request = new CourseRequestDto(
                "Java",
                "Backend development with Java",
                List.of(UUID.fromString("66666666-6666-4666-6666-666666666666"))
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/courses",
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
        CourseRequestDto request = new CourseRequestDto("", "", null);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/courses",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(3);
        assertThat(json.<List<String>>read("$.violations[*].fieldName"))
                .containsExactlyInAnyOrder("name", "description", "groupIds");
        assertThat(json.<List<String>>read("$.violations[*].message"))
                .containsExactlyInAnyOrder(
                        "не должно быть пустым",
                        "не должно быть пустым",
                        "не должно равняться null"
                );
    }

    @Test
    @Sql({
            "/sql/course/item.sql",
            "/sql/course/groups.sql"
    })
    void update_validRequest() {
        CourseRequestDto request = new CourseRequestDto(
                "Course",
                "Description",
                List.of(
                        UUID.fromString("66666666-6666-4666-6666-666666666666"),
                        UUID.fromString("77777777-7777-4777-7777-777777777777")
                )
        );

        ResponseEntity<CourseResponseDto> response = restTemplate.exchange(
                "/api/v1/courses/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                CourseResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        CourseResponseDto course = response.getBody();

        assertThat(course.id()).isEqualTo(UUID.fromString("11111111-1111-4111-1111-111111111111"));
        assertThat(course.name()).isEqualTo("Course");
        assertThat(course.description()).isEqualTo("Description");
        assertThat(course.groups())
                .extracting(GroupShortResponseDto::id)
                .containsExactlyInAnyOrder(
                        UUID.fromString("66666666-6666-4666-6666-666666666666"),
                        UUID.fromString("77777777-7777-4777-7777-777777777777")
                );
    }

    @Test
    void update_nonExistingId() {
        CourseRequestDto request = new CourseRequestDto(
                "Java",
                "Description",
                new ArrayList<>()
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/courses/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Курса с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    void update_invalidIdFormat() {
        CourseRequestDto request = new CourseRequestDto(
                "Java",
                "Description",
                new ArrayList<>()
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/courses/invalid-format",
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
    @Sql("/sql/course/item.sql")
    void update_invalidBody() {
        CourseRequestDto request = new CourseRequestDto(
                "",
                "Description",
                new ArrayList<>()
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/courses/11111111-1111-4111-1111-111111111111",
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
    @Sql("/sql/course/deleted-item.sql")
    void update_deletedId() {
        CourseRequestDto request = new CourseRequestDto(
                "Java",
                "Description",
                new ArrayList<>()
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/courses/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Курса с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    @Sql("/sql/course/item.sql")
    void delete_existingId() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/courses/11111111-1111-4111-1111-111111111111",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Boolean isDeleted = jdbcTemplate.queryForObject(
                "SELECT is_deleted FROM courses WHERE id = ?",
                Boolean.class,
                UUID.fromString("11111111-1111-4111-1111-111111111111")
        );

        assertThat(isDeleted).isTrue();
    }

    @Test
    void delete_nonExistingId() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/courses/11111111-1111-4111-1111-111111111111",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Курса с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    void delete_invalidIdFormat() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/courses/invalid-format",
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
            "/sql/course/item.sql",
            "/sql/course/schedules.sql"
    })
    void delete_withExistingSchedules() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/courses/11111111-1111-4111-1111-111111111111",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Нельзя удалить курс с id 11111111-1111-4111-1111-111111111111: у него есть активные занятия в расписании"
        );
    }

    @Test
    @Sql({
            "/sql/course/item.sql",
            "/sql/course/groups.sql"
    })
    void addGroups_existingGroups() {
        List<UUID> groupIds = List.of(
                UUID.fromString("66666666-6666-4666-6666-666666666666"),
                UUID.fromString("77777777-7777-4777-7777-777777777777")
        );

        ResponseEntity<CourseResponseDto> response = restTemplate.postForEntity(
                "/api/v1/courses/11111111-1111-4111-1111-111111111111/addGroups",
                groupIds,
                CourseResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        CourseResponseDto course = response.getBody();

        assertThat(course.id()).isEqualTo(UUID.fromString("11111111-1111-4111-1111-111111111111"));
        assertThat(course.groups())
                .extracting(GroupShortResponseDto::id)
                .containsExactlyInAnyOrder(
                        UUID.fromString("66666666-6666-4666-6666-666666666666"),
                        UUID.fromString("77777777-7777-4777-7777-777777777777")
                );
    }

    @Test
    @Sql("/sql/course/item.sql")
    void addGroups_emptyGroups() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/courses/11111111-1111-4111-1111-111111111111/addGroups",
                HttpMethod.POST,
                new HttpEntity<>(new ArrayList<>(), headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(1);
        assertThat(json.read("$.violations[0].fieldName", String.class)).isEqualTo("groupIds");
        assertThat(json.read("$.violations[0].message", String.class)).isEqualTo("не должно быть пустым");
    }

    @Test
    @Sql("/sql/course/item.sql")
    void addGroups_nullGroups() {
        List<UUID> groupIds = new ArrayList<>();
        groupIds.add(null);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/courses/11111111-1111-4111-1111-111111111111/addGroups",
                HttpMethod.POST,
                new HttpEntity<>(groupIds, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(1);
        assertThat(json.read("$.violations[0].fieldName", String.class)).isEqualTo("groupIds");
        assertThat(json.read("$.violations[0].message", String.class)).isEqualTo("не должно равняться null");
    }

    @Test
    @Sql("/sql/course/item.sql")
    void addGroups_nonExistingGroups() {
        List<UUID> groupIds = List.of(
                UUID.fromString("66666666-6666-4666-6666-666666666666"),
                UUID.fromString("77777777-7777-4777-7777-777777777777")
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/courses/11111111-1111-4111-1111-111111111111/addGroups",
                new HttpEntity<>(groupIds, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Не найдены группы с id: 66666666-6666-4666-6666-666666666666, 77777777-7777-4777-7777-777777777777"
        );
    }
}
