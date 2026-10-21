package com.course.krch.educationsystem.dto.response;

import java.util.UUID;

public record TeacherResponseDto(
        UUID id,
        String firstName,
        String lastName
) {
}
