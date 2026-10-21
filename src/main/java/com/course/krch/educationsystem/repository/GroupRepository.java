package com.course.krch.educationsystem.repository;

import com.course.krch.educationsystem.entity.Group;
import com.course.krch.educationsystem.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.*;

public interface GroupRepository extends JpaRepository<Group, UUID>, JpaSpecificationExecutor<Group> {

    default Group findByIdAndIsDeletedFalseOrThrow(UUID id){
        return findByIdAndIsDeletedFalse(id)
                .orElseThrow(
                        () -> new NotFoundException("Группы с id = " + id + " не существует")
                );
    }

    Optional<Group> findByIdAndIsDeletedFalse(UUID id);

    Set<Group> findAllByIdInAndIsDeletedFalse(Collection<UUID> ids);
}
