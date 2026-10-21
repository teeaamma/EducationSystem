package com.course.krch.educationsystem.repository;

import com.course.krch.educationsystem.entity.Student;
import com.course.krch.educationsystem.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.*;

public interface StudentRepository extends JpaRepository<Student, UUID>, JpaSpecificationExecutor<Student> {

    default Student findByIdAndIsDeletedFalseOrThrow(UUID id){
        return findByIdAndIsDeletedFalse(id)
                .orElseThrow(
                        () -> new NotFoundException("Студента с id = " + id + " не существует")
                );
    }

    Optional<Student> findByIdAndIsDeletedFalse(UUID id);

    Set<Student> findAllByIdInAndIsDeletedFalse(Collection<UUID> ids);
}
