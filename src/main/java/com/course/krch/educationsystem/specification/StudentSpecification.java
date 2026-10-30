package com.course.krch.educationsystem.specification;

import com.course.krch.educationsystem.entity.Student;
import org.springframework.data.jpa.domain.Specification;

public class StudentSpecification {

    private StudentSpecification(){}

    public static Specification<Student> notDeleted(){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isDeleted"));
    }

    public static Specification<Student> firstNameContains(String firstName){
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

    public static Specification<Student> lastNameContains(String lastName){
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
