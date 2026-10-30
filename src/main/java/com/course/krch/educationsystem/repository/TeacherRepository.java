package com.course.krch.educationsystem.repository;

import com.course.krch.educationsystem.entity.Teacher;
import com.course.krch.educationsystem.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface TeacherRepository extends JpaRepository<Teacher, UUID>, JpaSpecificationExecutor<Teacher> {

    default Teacher findByIdAndIsDeletedFalseOrThrow(UUID id){
        return findByIdAndIsDeletedFalse(id)
                .orElseThrow(
                        () -> new NotFoundException("Преподавателя с id = " + id + " не существует")
                );
    }

    Optional<Teacher> findByIdAndIsDeletedFalse(UUID id);

    @Modifying
    @Query("""
            update Teacher t
            set t.isDeleted = true
            where t.id = :id
            and t.isDeleted = false
            """)
    int softDeleteById(UUID id);
}
