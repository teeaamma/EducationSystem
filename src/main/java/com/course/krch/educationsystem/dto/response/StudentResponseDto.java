package com.course.krch.educationsystem.dto.response;

import java.util.List;
import java.util.UUID;

public record StudentResponseDto(
        UUID id,
        String firstName,
        String lastName,
        List<GroupShortResponseDto> groups
) {
}
