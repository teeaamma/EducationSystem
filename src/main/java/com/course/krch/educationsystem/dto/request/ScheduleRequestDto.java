package com.course.krch.educationsystem.dto.request;

import com.course.krch.educationsystem.validation.DateRange;
import com.course.krch.educationsystem.validation.ValidDateRange;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@ValidDateRange
public record ScheduleRequestDto(

        @NotNull
        UUID groupId,

        @NotNull
        UUID teacherId,

        @NotNull
        UUID courseId,

        @NotNull
        LocalDateTime startDate,

        @NotNull
        LocalDateTime endDate
) implements DateRange {
}
