package com.course.krch.educationsystem.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduleResponseDto(
        UUID id,
        GroupShortResponseDto group,
        TeacherResponseDto teacher,
        CourseShortResponseDto course,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
