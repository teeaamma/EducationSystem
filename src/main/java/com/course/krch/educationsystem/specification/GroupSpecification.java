package com.course.krch.educationsystem.specification;

import com.course.krch.educationsystem.entity.Group;
import org.springframework.data.jpa.domain.Specification;

public class GroupSpecification {

    private GroupSpecification(){}

    public static Specification<Group> notDeleted(){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isDeleted"));
    }

    public static Specification<Group> nameContains(String name){
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
}
