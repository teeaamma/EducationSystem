package com.course.krch.educationsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CourseRequestDto(

        @NotBlank
        String name,

        @NotBlank
        String description,

        @NotNull
        List<@NotNull UUID> groupIds
) {
}
