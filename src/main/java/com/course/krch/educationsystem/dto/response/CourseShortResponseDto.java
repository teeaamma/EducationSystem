package com.course.krch.educationsystem.dto.response;

import java.util.UUID;

public record CourseShortResponseDto(
        UUID id,
        String name,
        String description
) {
}
