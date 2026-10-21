package com.course.krch.educationsystem.repository;

import com.course.krch.educationsystem.entity.Schedule;
import com.course.krch.educationsystem.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID>, JpaSpecificationExecutor<Schedule> {

    default Schedule findByIdAndIsDeletedFalseOrThrow(UUID id){
        return findByIdAndIsDeletedFalse(id)
                .orElseThrow(
                        () -> new NotFoundException("Занятия с id = " + id + " не существует")
                );
    }

    Optional<Schedule> findByIdAndIsDeletedFalse(UUID id);

    Page<Schedule> findAllByGroupIdAndIsDeletedFalse(UUID groupId, Pageable pageable);

    Page<Schedule> findAllByTeacherIdAndIsDeletedFalse(UUID teacherId, Pageable pageable);

    boolean existsByGroupIdAndIsDeletedFalse(UUID groupId);

    boolean existsByTeacherIdAndIsDeletedFalse(UUID teacherId);

    boolean existsByCourseIdAndIsDeletedFalse(UUID courseId);

    @Query("""
            select count(s) > 0
            from Schedule s
            where s.course.id = :courseId
            and s.teacher.id <> :teacherId
            and s.isDeleted = false
            """)
    boolean hasConflict(UUID courseId, UUID teacherId);

    @Query("""
            select count(s) > 0
            from Schedule s
            where s.course.id = :courseId
            and s.teacher.id <> :teacherId
            and s.id <> :scheduleId
            and s.isDeleted = false
            """)
    boolean hasConflictExcluding(UUID courseId, UUID teacherId, UUID scheduleId);

    @Modifying
    @Query("""
            update Schedule s
            set s.isDeleted = true
            where s.id = :id
            and s.isDeleted = false
            """)
    int softDeleteById(UUID id);

    @Modifying
    @Query("""
            update Schedule s
            set s.isDeleted = true
            where s.endDate < :endDateBefore
            and s.isDeleted = false
            """)
    int softDeleteByEndDateBefore(LocalDateTime endDateBefore);
}
