package com.course.krch.educationsystem.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record StudentRequestDto(

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotEmpty
        List<@NotNull UUID> groupIds
) {
}
