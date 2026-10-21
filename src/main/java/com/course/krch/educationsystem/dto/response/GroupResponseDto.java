package com.course.krch.educationsystem.dto.response;

import java.util.List;
import java.util.UUID;

public record GroupResponseDto(
        UUID id,
        String name,
        List<StudentShortResponseDto> students
) {
}
