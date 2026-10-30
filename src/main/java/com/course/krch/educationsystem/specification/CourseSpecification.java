package com.course.krch.educationsystem.specification;

import com.course.krch.educationsystem.entity.Course;
import org.springframework.data.jpa.domain.Specification;

public class CourseSpecification {

    private CourseSpecification(){}

    public static Specification<Course> notDeleted(){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isDeleted"));
    }

    public static Specification<Course> nameContains(String name){
        if (name == null || name.isBlank()) {
            return Specification.unrestricted();
        }

        String value = "%" + name.trim().toLowerCase() + "%";

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        value
                );
    }

    public static Specification<Course> descriptionContains(String description){
        if (description == null || description.isBlank()) {
            return Specification.unrestricted();
        }

        String value = "%" + description.trim().toLowerCase() + "%";

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")),
                        value
                );
    }
}
