package com.course.krch.educationsystem.specification;

import com.course.krch.educationsystem.entity.Teacher;
import org.springframework.data.jpa.domain.Specification;

public class TeacherSpecification {

    private TeacherSpecification(){}

    public static Specification<Teacher> notDeleted(){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isDeleted"));
    }

    public static Specification<Teacher> firstNameContains(String firstName){
        if (firstName == null || firstName.isBlank()) {
            return Specification.unrestricted();
        }

        String value = "%" + firstName.trim().toLowerCase() + "%";

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("firstName")),
                        value
                );
    }

    public static Specification<Teacher> lastNameContains(String lastName){
        if (lastName == null || lastName.isBlank()) {
            return Specification.unrestricted();
        }

        String value = "%" + lastName.trim().toLowerCase() + "%";

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("lastName")),
                        value
                );
    }
}
