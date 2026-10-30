package com.course.krch.educationsystem.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ValidationErrorResponse(
        List<Violation> violations,
        List<SimpleErrorResponse> errors
) {
}
