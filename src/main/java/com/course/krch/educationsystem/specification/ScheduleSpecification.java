package com.course.krch.educationsystem.specification;

import com.course.krch.educationsystem.dto.request.ScheduleFilterDto;
import com.course.krch.educationsystem.entity.Schedule;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ScheduleSpecification {

    private ScheduleSpecification(){}

    public static Specification<Schedule> from(ScheduleFilterDto filter){
        return Specification.allOf(
                ScheduleSpecification.notDeleted(),
                ScheduleSpecification.groupNameContains(filter.groupName()),
                ScheduleSpecification.courseNameContains(filter.courseName()),
                ScheduleSpecification.teacherNameContains(filter.teacherName()),
                ScheduleSpecification.startDateAfter(filter.startDate()),
                ScheduleSpecification.endDateBefore(filter.endDate())
        );
    }

    public static Specification<Schedule> notDeleted(){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isDeleted"));
    }

    public static Specification<Schedule> groupNameContains(String groupName){
        if (groupName == null || groupName.isBlank()) {
            return Specification.unrestricted();
        }

        String value = "%" + groupName.trim().toLowerCase() + "%";

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("group").get("name")),
                        value
                );
    }

    public static Specification<Schedule> courseNameContains(String name){
        if (name == null || name.isBlank()) {
            return Specification.unrestricted();
        }

        String value = "%" + name.trim().toLowerCase() + "%";

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("course").get("name")),
                        value
                );
    }

    public static Specification<Schedule> teacherNameContains(String name){
        if (name == null || name.isBlank()) {
            return Specification.unrestricted();
        }

        String value = "%" + name.trim().toLowerCase() + "%";

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("teacher").get("firstName")),
                                value
                        ),
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("teacher").get("lastName")),
                                value
                        )
                );
    }

    public static Specification<Schedule> startDateAfter(LocalDateTime startDate){
        if (startDate == null) {
            return Specification.unrestricted();
        }

        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("startDate"),
                        startDate
                )
                );
    }

    public static Specification<Schedule> endDateBefore(LocalDateTime endDate){
        if (endDate == null) {
            return Specification.unrestricted();
        }

        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("startDate"),
                        endDate
                )
        );
    }
}
