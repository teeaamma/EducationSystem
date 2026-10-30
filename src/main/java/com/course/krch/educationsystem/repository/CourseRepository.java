package com.course.krch.educationsystem.repository;

import com.course.krch.educationsystem.entity.Course;
import com.course.krch.educationsystem.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID>, JpaSpecificationExecutor<Course> {

    default Course findByIdAndIsDeletedFalseOrThrow(UUID id){
        return findByIdAndIsDeletedFalse(id)
                .orElseThrow(
                        () -> new NotFoundException("Курса с id = " + id + " не существует")
                );
    }

    Optional<Course> findByIdAndIsDeletedFalse(UUID id);
}
