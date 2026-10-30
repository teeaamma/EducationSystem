package com.course.krch.educationsystem.dto.response;

import java.util.UUID;

public record StudentShortResponseDto(
        UUID id,
        String firstName,
        String lastName
) {
}
