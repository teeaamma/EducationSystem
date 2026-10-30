package com.course.krch.educationsystem;

import com.course.krch.educationsystem.dto.request.ScheduleRequestDto;
import com.course.krch.educationsystem.dto.response.ScheduleResponseDto;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ScheduleIntegrationTest extends AbstractIntegrationTest {

    @Test
    @Sql("/sql/schedule/list.sql")
    void getAll_defaultPagination() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/schedules", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(5);
        assertThat(json.read("$.number", Integer.class)).isZero();
        assertThat(json.read("$.size", Integer.class)).isEqualTo(20);
    }

    @Test
    @Sql("/sql/schedule/list.sql")
    void getAll_requestedPage() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/schedules?page={page}&size={size}&sort={sort}",
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
    @Sql("/sql/schedule/list.sql")
    void getAll_lastPartialPage() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/schedules?page={page}&size={size}&sort={sort}",
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
    @Sql("/sql/schedule/list.sql")
    void getAll_groupNameFilter() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/schedules?groupName={groupName}",
                String.class,
                "Java"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(3);
        assertThat(json.<List<String>>read("$.content[*].group.name"))
                .allMatch(name -> name.equals("Java group"));
    }

    @Test
    @Sql("/sql/schedule/list.sql")
    void getAll_courseNameFilter() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/schedules?courseName={courseName}",
                String.class,
                "Spring"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(3);
        assertThat(json.<List<String>>read("$.content[*].course.name"))
                .allMatch(name -> name.equals("Spring"));
    }

    @Test
    @Sql("/sql/schedule/list.sql")
    void getAll_teacherNameFilter() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/schedules?teacherName={teacherName}",
                String.class,
                "Андрей"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(2);
        assertThat(json.<List<String>>read("$.content[*].teacher.firstName"))
                .allMatch(name -> name.equals("Андрей"));
    }

    @Test
    @Sql("/sql/schedule/list.sql")
    void getAll_startDateFilter() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/schedules?startDate={startDate}",
                String.class,
                "2026-10-23T09:00:00"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(2);
        assertThat(json.<List<String>>read("$.content[*].id"))
                .containsExactlyInAnyOrder(
                        "44444444-4444-4444-4444-444444444444",
                        "55555555-5555-4555-5555-555555555555"
                );
    }

    @Test
    @Sql("/sql/schedule/list.sql")
    void getAll_endDateFilter() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/schedules?endDate={endDate}",
                String.class,
                "2026-10-23T09:00:00"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(3);
        assertThat(json.<List<String>>read("$.content[*].id"))
                .containsExactlyInAnyOrder(
                        "11111111-1111-4111-1111-111111111111",
                        "22222222-2222-4222-2222-222222222222",
                        "33333333-3333-4333-3333-333333333333"
                );
    }

    @Test
    @Sql("/sql/schedule/list.sql")
    void getAll_dateFilter() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/schedules?startDate={startDate}&endDate={endDate}",
                String.class,
                "2026-10-22T16:00:00",
                "2026-10-23T13:00:00"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(2);
        assertThat(json.<List<String>>read("$.content[*].id"))
                .containsExactlyInAnyOrder(
                        "33333333-3333-4333-3333-333333333333",
                        "44444444-4444-4444-4444-444444444444"
                );
    }

    @Test
    @Sql("/sql/schedule/list.sql")
    void getAll_invalidDateFilter() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/schedules?startDate={startDate}&endDate={endDate}",
                String.class,
                "2026-10-23T13:00:00",
                "2026-10-22T16:00:00"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.errors")).hasSize(1);
        assertThat(json.read("$.errors[0].message", String.class))
                .isEqualTo("Дата начала должна быть раньше даты окончания");
    }

    @Test
    @Sql("/sql/schedule/list.sql")
    void getAll_combinedFilters() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/schedules?groupName={groupName}&startDate={startDate}&endDate={endDate}",
                String.class,
                "Java",
                "2026-10-22T16:00:00",
                "2026-10-23T13:00:00"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.content")).hasSize(1);
        assertThat(json.read("$.content[0].id", String.class))
                .isEqualTo("33333333-3333-4333-3333-333333333333");
    }

    @Test
    @Sql("/sql/schedule/list.sql")
    void getAll_noFilterMatches() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/schedules?groupName={groupName}&teacherName={teacherName}",
                String.class,
                "фильтр", "фильтр"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.content", List.class)).hasSize(0);
        assertThat(json.read("$.numberOfElements", Integer.class)).isEqualTo(0);
    }

    @Test
    @Sql("/sql/schedule/list.sql")
    void getAll_filteredPagination() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/schedules?page={page}&size={size}&sort={sort}&groupName={groupName}&endDate={endDate}",
                String.class,
                1, 1, "id,asc", "Java", "2026-10-23T13:00:00"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.content", List.class)).hasSize(1);
        assertThat(json.read("$.number", Integer.class)).isEqualTo(1);
        assertThat(json.read("$.size", Integer.class)).isEqualTo(1);
        assertThat(json.read("$.totalElements", Integer.class)).isEqualTo(3);
        assertThat(json.read("$.content[0].id", String.class))
                .isEqualTo("22222222-2222-4222-2222-222222222222");
    }

    @Test
    @Sql("/sql/schedule/item.sql")
    void getById_existingId() {
        ResponseEntity<ScheduleResponseDto> response = restTemplate.getForEntity(
                "/api/v1/schedules/11111111-1111-4111-1111-111111111111", ScheduleResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ScheduleResponseDto schedule = response.getBody();

        assertThat(schedule.id()).isEqualTo(UUID.fromString("11111111-1111-4111-1111-111111111111"));
        assertThat(schedule.group().name()).isEqualTo("Java group");
        assertThat(schedule.teacher().firstName()).isEqualTo("Андрей");
        assertThat(schedule.course().name()).isEqualTo("Java");
        assertThat(schedule.startDate())
                .isEqualTo(LocalDateTime.of(2026, 10, 22, 10, 30));
        assertThat(schedule.endDate())
                .isEqualTo(LocalDateTime.of(2026, 10, 22, 12, 0));
    }

    @Test
    void getById_nonExistingId() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/schedules/11111111-1111-4111-1111-111111111111", String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Занятия с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    void getById_invalidIdFormat() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/schedules/invalid-format", String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Неправильный формат аргумента id"
        );
    }

    @Test
    @Sql("/sql/schedule/deleted-item.sql")
    void getById_deletedId() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/schedules/11111111-1111-4111-1111-111111111111", String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Занятия с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    @Sql("/sql/schedule/group-teacher-course.sql")
    void create_validRequest() {
        ScheduleRequestDto request = new ScheduleRequestDto(
                UUID.fromString("77777777-7777-4777-7777-777777777777"),
                UUID.fromString("99999999-9999-4999-9999-999999999999"),
                UUID.fromString("bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb"),
                LocalDateTime.of(2026, 10, 22, 10, 30),
                LocalDateTime.of(2026, 10, 22, 12, 0)
        );

        ResponseEntity<ScheduleResponseDto> response = restTemplate.postForEntity(
                "/api/v1/schedules",
                request,
                ScheduleResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ScheduleResponseDto schedule = response.getBody();

        assertThat(schedule.id()).isNotNull();
        assertThat(schedule.group().name()).isEqualTo("Spring group");
        assertThat(schedule.teacher().firstName()).isEqualTo("Борис");
        assertThat(schedule.course().name()).isEqualTo("Spring");
        assertThat(schedule.startDate())
                .isEqualTo(LocalDateTime.of(2026, 10, 22, 10, 30));
        assertThat(schedule.endDate())
                .isEqualTo(LocalDateTime.of(2026, 10, 22, 12, 0));
    }

    @Test
    @Sql("/sql/schedule/group-teacher-course.sql")
    void create_nullGroupId() {
        ScheduleRequestDto request = new ScheduleRequestDto(
                null,
                UUID.fromString("99999999-9999-4999-9999-999999999999"),
                UUID.fromString("bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb"),
                LocalDateTime.of(2026, 10, 22, 10, 30),
                LocalDateTime.of(2026, 10, 22, 12, 0)
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/schedules",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(1);
        assertThat(json.read("$.violations[0].fieldName", String.class)).isEqualTo("groupId");
        assertThat(json.read("$.violations[0].message", String.class)).isEqualTo("не должно равняться null");
    }

    @Test
    @Sql("/sql/schedule/group-teacher-course.sql")
    void create_invalidGroupId() {
        String request = """
            {
              "groupId": "invalid-format",
              "teacherId": "99999999-9999-4999-9999-999999999999",
              "courseId": "bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb",
              "startDate": "2026-10-22T10:30:00",
              "endDate": "2026-10-22T12:00:00"
            }
            """;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/schedules",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Неправильный формат тела запроса"
        );
    }

    @Test
    @Sql("/sql/schedule/group-teacher-course.sql")
    void create_nonExistingGroupId() {
        ScheduleRequestDto request = new ScheduleRequestDto(
                UUID.fromString("cccccccc-cccc-4ccc-cccc-cccccccccccc"),
                UUID.fromString("99999999-9999-4999-9999-999999999999"),
                UUID.fromString("bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb"),
                LocalDateTime.of(2026, 10, 22, 10, 30),
                LocalDateTime.of(2026, 10, 22, 12, 0)
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/schedules",
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Группы с id = cccccccc-cccc-4ccc-cccc-cccccccccccc не существует"
        );
    }

    @Test
    @Sql("/sql/schedule/group-teacher-course.sql")
    void create_nullTeacherId() {
        ScheduleRequestDto request = new ScheduleRequestDto(
                UUID.fromString("77777777-7777-4777-7777-777777777777"),
                null,
                UUID.fromString("bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb"),
                LocalDateTime.of(2026, 10, 22, 10, 30),
                LocalDateTime.of(2026, 10, 22, 12, 0)
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/schedules",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(1);
        assertThat(json.read("$.violations[0].fieldName", String.class)).isEqualTo("teacherId");
        assertThat(json.read("$.violations[0].message", String.class)).isEqualTo("не должно равняться null");
    }

    @Test
    @Sql("/sql/schedule/group-teacher-course.sql")
    void create_invalidTeacherId() {
        String request = """
            {
              "groupId": "77777777-7777-4777-7777-777777777777",
              "teacherId": "invalid-format",
              "courseId": "bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb",
              "startDate": "2026-10-22T10:30:00",
              "endDate": "2026-10-22T12:00:00"
            }
            """;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/schedules",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Неправильный формат тела запроса"
        );
    }

    @Test
    @Sql("/sql/schedule/group-teacher-course.sql")
    void create_nonExistingTeacherId() {
        ScheduleRequestDto request = new ScheduleRequestDto(
                UUID.fromString("77777777-7777-4777-7777-777777777777"),
                UUID.fromString("cccccccc-cccc-4ccc-cccc-cccccccccccc"),
                UUID.fromString("bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb"),
                LocalDateTime.of(2026, 10, 22, 10, 30),
                LocalDateTime.of(2026, 10, 22, 12, 0)
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/schedules",
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Преподавателя с id = cccccccc-cccc-4ccc-cccc-cccccccccccc не существует"
        );
    }

    @Test
    @Sql("/sql/schedule/group-teacher-course.sql")
    void create_nullCourseId() {
        ScheduleRequestDto request = new ScheduleRequestDto(
                UUID.fromString("77777777-7777-4777-7777-777777777777"),
                UUID.fromString("99999999-9999-4999-9999-999999999999"),
                null,
                LocalDateTime.of(2026, 10, 22, 10, 30),
                LocalDateTime.of(2026, 10, 22, 12, 0)
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/schedules",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(1);
        assertThat(json.read("$.violations[0].fieldName", String.class)).isEqualTo("courseId");
        assertThat(json.read("$.violations[0].message", String.class)).isEqualTo("не должно равняться null");
    }

    @Test
    @Sql("/sql/schedule/group-teacher-course.sql")
    void create_invalidCourseId() {
        String request = """
            {
              "groupId": "77777777-7777-4777-7777-777777777777",
              "teacherId": "99999999-9999-4999-9999-999999999999",
              "courseId": "invalid-format",
              "startDate": "2026-10-22T10:30:00",
              "endDate": "2026-10-22T12:00:00"
            }
            """;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/schedules",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Неправильный формат тела запроса"
        );
    }

    @Test
    @Sql("/sql/schedule/group-teacher-course.sql")
    void create_nonExistingCourseId() {
        ScheduleRequestDto request = new ScheduleRequestDto(
                UUID.fromString("77777777-7777-4777-7777-777777777777"),
                UUID.fromString("99999999-9999-4999-9999-999999999999"),
                UUID.fromString("cccccccc-cccc-4ccc-cccc-cccccccccccc"),
                LocalDateTime.of(2026, 10, 22, 10, 30),
                LocalDateTime.of(2026, 10, 22, 12, 0)
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/schedules",
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Курса с id = cccccccc-cccc-4ccc-cccc-cccccccccccc не существует"
        );
    }

    @Test
    @Sql("/sql/schedule/group-teacher-course.sql")
    void create_nullStartDate() {
        ScheduleRequestDto request = new ScheduleRequestDto(
                UUID.fromString("77777777-7777-4777-7777-777777777777"),
                UUID.fromString("99999999-9999-4999-9999-999999999999"),
                UUID.fromString("bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb"),
                null,
                LocalDateTime.of(2026, 10, 22, 12, 0)
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/schedules",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(1);
        assertThat(json.read("$.violations[0].fieldName", String.class)).isEqualTo("startDate");
        assertThat(json.read("$.violations[0].message", String.class)).isEqualTo("не должно равняться null");
    }

    @Test
    @Sql("/sql/schedule/group-teacher-course.sql")
    void create_invalidStartDate() {
        String request = """
            {
              "groupId": "77777777-7777-4777-7777-777777777777",
              "teacherId": "99999999-9999-4999-9999-999999999999",
              "courseId": "bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb",
              "startDate": "invalid-format",
              "endDate": "2026-10-22T12:00:00"
            }
            """;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/schedules",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Неправильный формат тела запроса"
        );
    }

    @Test
    @Sql("/sql/schedule/group-teacher-course.sql")
    void create_nullEndDate() {
        ScheduleRequestDto request = new ScheduleRequestDto(
                UUID.fromString("77777777-7777-4777-7777-777777777777"),
                UUID.fromString("99999999-9999-4999-9999-999999999999"),
                UUID.fromString("bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb"),
                LocalDateTime.of(2026, 10, 22, 10, 30),
                null
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/schedules",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(1);
        assertThat(json.read("$.violations[0].fieldName", String.class)).isEqualTo("endDate");
        assertThat(json.read("$.violations[0].message", String.class)).isEqualTo("не должно равняться null");
    }

    @Test
    @Sql("/sql/schedule/group-teacher-course.sql")
    void create_invalidEndDate() {
        String request = """
            {
              "groupId": "77777777-7777-4777-7777-777777777777",
              "teacherId": "99999999-9999-4999-9999-999999999999",
              "courseId": "bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb",
              "startDate": "2026-10-22T10:30:00",
              "endDate": "invalid-format"
            }
            """;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/schedules",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Неправильный формат тела запроса"
        );
    }

    @Test
    @Sql("/sql/schedule/group-teacher-course.sql")
    void create_startDateAfterEndDate() {
        ScheduleRequestDto request = new ScheduleRequestDto(
                UUID.fromString("77777777-7777-4777-7777-777777777777"),
                UUID.fromString("99999999-9999-4999-9999-999999999999"),
                UUID.fromString("bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb"),
                LocalDateTime.of(2026, 11, 22, 10, 30),
                LocalDateTime.of(2026, 10, 22, 10, 30)
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/schedules",
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.errors")).hasSize(1);
        assertThat(json.read("$.errors[0].message", String.class))
                .isEqualTo("Дата начала должна быть раньше даты окончания");
    }

    @Test
    void create_invalidFields() {
        ScheduleRequestDto request = new ScheduleRequestDto(
                null,
                null,
                null,
                null,
                null
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/schedules",
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(5);
        assertThat(json.<List<String>>read("$.violations[*].fieldName"))
                .containsExactlyInAnyOrder("groupId", "teacherId", "courseId", "startDate", "endDate");
        assertThat(json.<List<String>>read("$.violations[*].message"))
                .allMatch(message -> message.equals("не должно равняться null"));
    }

    @Test
    @Sql({
            "/sql/schedule/item.sql",
            "/sql/schedule/group-teacher-course.sql"
    })
    void update_validRequest() {
        ScheduleRequestDto request = new ScheduleRequestDto(
                UUID.fromString("77777777-7777-4777-7777-777777777777"),
                UUID.fromString("99999999-9999-4999-9999-999999999999"),
                UUID.fromString("bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb"),
                LocalDateTime.of(2026, 10, 23, 10, 30),
                LocalDateTime.of(2026, 10, 23, 12, 0)
        );

        ResponseEntity<ScheduleResponseDto> response = restTemplate.exchange(
                "/api/v1/schedules/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ScheduleResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ScheduleResponseDto schedule = response.getBody();

        assertThat(schedule.id()).isEqualTo(UUID.fromString("11111111-1111-4111-1111-111111111111"));
        assertThat(schedule.group().name()).isEqualTo("Spring group");
        assertThat(schedule.teacher().firstName()).isEqualTo("Борис");
        assertThat(schedule.course().name()).isEqualTo("Spring");
        assertThat(schedule.startDate())
                .isEqualTo(LocalDateTime.of(2026, 10, 23, 10, 30));
        assertThat(schedule.endDate())
                .isEqualTo(LocalDateTime.of(2026, 10, 23, 12, 0));

    }

    @Test
    @Sql("/sql/schedule/group-teacher-course.sql")
    void update_nonExistingId() {
        ScheduleRequestDto request = new ScheduleRequestDto(
                UUID.fromString("77777777-7777-4777-7777-777777777777"),
                UUID.fromString("99999999-9999-4999-9999-999999999999"),
                UUID.fromString("bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb"),
                LocalDateTime.of(2026, 10, 23, 10, 30),
                LocalDateTime.of(2026, 10, 23, 12, 0)
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/schedules/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Занятия с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    void update_invalidIdFormat() {
        ScheduleRequestDto request = new ScheduleRequestDto(
                UUID.fromString("77777777-7777-4777-7777-777777777777"),
                UUID.fromString("99999999-9999-4999-9999-999999999999"),
                UUID.fromString("bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb"),
                LocalDateTime.of(2026, 10, 23, 10, 30),
                LocalDateTime.of(2026, 10, 23, 10, 30)
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/schedules/invalid-format",
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
    @Sql({
            "/sql/schedule/item.sql",
            "/sql/schedule/group-teacher-course.sql"
    })
    void update_nonExistingGroupId() {
        ScheduleRequestDto request = new ScheduleRequestDto(
                UUID.fromString("cccccccc-cccc-4ccc-cccc-cccccccccccc"),
                UUID.fromString("99999999-9999-4999-9999-999999999999"),
                UUID.fromString("bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb"),
                LocalDateTime.of(2026, 10, 22, 10, 30),
                LocalDateTime.of(2026, 10, 22, 12, 0)
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/schedules/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Группы с id = cccccccc-cccc-4ccc-cccc-cccccccccccc не существует"
        );
    }

    @Test
    @Sql({
            "/sql/schedule/item.sql",
            "/sql/schedule/group-teacher-course.sql"
    })
    void update_nonExistingTeacherId() {
        ScheduleRequestDto request = new ScheduleRequestDto(
                UUID.fromString("77777777-7777-4777-7777-777777777777"),
                UUID.fromString("cccccccc-cccc-4ccc-cccc-cccccccccccc"),
                UUID.fromString("bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb"),
                LocalDateTime.of(2026, 10, 22, 10, 30),
                LocalDateTime.of(2026, 10, 22, 12, 0)
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/schedules/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Преподавателя с id = cccccccc-cccc-4ccc-cccc-cccccccccccc не существует"
        );
    }

    @Test
    @Sql({
            "/sql/schedule/item.sql",
            "/sql/schedule/group-teacher-course.sql"
    })
    void update_nonExistingCourseId() {
        ScheduleRequestDto request = new ScheduleRequestDto(
                UUID.fromString("77777777-7777-4777-7777-777777777777"),
                UUID.fromString("99999999-9999-4999-9999-999999999999"),
                UUID.fromString("cccccccc-cccc-4ccc-cccc-cccccccccccc"),
                LocalDateTime.of(2026, 10, 22, 10, 30),
                LocalDateTime.of(2026, 10, 22, 12, 0)
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/schedules/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Курса с id = cccccccc-cccc-4ccc-cccc-cccccccccccc не существует"
        );
    }

    @Test
    @Sql({
            "/sql/schedule/item.sql",
            "/sql/schedule/group-teacher-course.sql"
    })
    void update_invalidBody() {
        ScheduleRequestDto request = new ScheduleRequestDto(
                null,
                null,
                null,
                null,
                null
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, "ru");

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/schedules/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request, headers),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.<List<?>>read("$.violations")).hasSize(5);
        assertThat(json.<List<String>>read("$.violations[*].fieldName"))
                .containsExactlyInAnyOrder("groupId", "teacherId", "courseId", "startDate", "endDate");
        assertThat(json.<List<String>>read("$.violations[*].message"))
                .allMatch(message -> message.equals("не должно равняться null"));
    }

    @Test
    @Sql({
            "/sql/schedule/deleted-item.sql",
            "/sql/schedule/group-teacher-course.sql"
    })
    void update_deletedId() {
        ScheduleRequestDto request = new ScheduleRequestDto(
                UUID.fromString("77777777-7777-4777-7777-777777777777"),
                UUID.fromString("99999999-9999-4999-9999-999999999999"),
                UUID.fromString("bbbbbbbb-bbbb-4bbb-bbbb-bbbbbbbbbbbb"),
                LocalDateTime.of(2026, 10, 23, 10, 30),
                LocalDateTime.of(2026, 10, 23, 12, 00)
        );

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/schedules/11111111-1111-4111-1111-111111111111",
                HttpMethod.PUT,
                new HttpEntity<>(request),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Занятия с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    @Sql("/sql/schedule/item.sql")
    void delete_existingId() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/schedules/11111111-1111-4111-1111-111111111111",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Boolean isDeleted = jdbcTemplate.queryForObject(
                "SELECT is_deleted FROM schedules WHERE id = ?",
                Boolean.class,
                UUID.fromString("11111111-1111-4111-1111-111111111111")
        );

        assertThat(isDeleted).isTrue();
    }

    @Test
    void delete_nonExistingId() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/schedules/11111111-1111-4111-1111-111111111111",
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        DocumentContext json = JsonPath.parse(response.getBody());

        assertThat(json.read("$.message", String.class)).isEqualTo(
                "Занятия с id = 11111111-1111-4111-1111-111111111111 не существует"
        );
    }

    @Test
    void delete_invalidIdFormat() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/schedules/invalid-format",
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
