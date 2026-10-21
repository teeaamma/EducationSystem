package com.course.krch.educationsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record GroupRequestDto(

        @NotBlank
        String name,

        @NotNull
        List<@NotNull UUID> studentIds
) {
}
