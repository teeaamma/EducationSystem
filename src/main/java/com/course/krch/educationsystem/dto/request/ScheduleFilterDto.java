package com.course.krch.educationsystem.dto.request;

import com.course.krch.educationsystem.validation.DateRange;
import com.course.krch.educationsystem.validation.ValidDateRange;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@ValidDateRange
public record ScheduleFilterDto(

        String groupName,

        String courseName,

        String teacherName,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDateTime startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDateTime endDate
) implements DateRange {
}
