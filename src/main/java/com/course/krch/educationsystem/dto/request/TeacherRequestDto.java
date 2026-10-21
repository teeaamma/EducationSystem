package com.course.krch.educationsystem.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TeacherRequestDto(

        @NotBlank
        String firstName,

        @NotBlank
        String lastName
) {
}
