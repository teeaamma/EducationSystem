package com.course.krch.educationsystem.dto.response;

import java.util.List;
import java.util.UUID;

public record CourseResponseDto(
        UUID id,
        String name,
        String description,
        List<GroupShortResponseDto> groups
) {
}
