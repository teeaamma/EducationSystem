package com.course.krch.educationsystem.service;

import com.course.krch.educationsystem.dto.request.CourseRequestDto;
import com.course.krch.educationsystem.dto.response.CourseResponseDto;
import com.course.krch.educationsystem.entity.Course;
import com.course.krch.educationsystem.entity.Group;
import com.course.krch.educationsystem.exception.DeleteNotAllowedException;
import com.course.krch.educationsystem.mapper.CourseMapper;
import com.course.krch.educationsystem.repository.CourseRepository;
import com.course.krch.educationsystem.repository.ScheduleRepository;
import com.course.krch.educationsystem.specification.CourseSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final ScheduleRepository scheduleRepository;
    private final GroupService groupService;
    private final CourseMapper courseMapper;

    @Transactional(readOnly = true)
    public Page<CourseResponseDto> getAll(String name, String description, Pageable pageable){
        Specification<Course> specification = Specification.allOf(
                CourseSpecification.nameContains(name),
                CourseSpecification.descriptionContains(description),
                CourseSpecification.notDeleted()
        );

        return courseRepository.findAll(specification, pageable)
                .map(courseMapper::toDto);
    }

    @Transactional(readOnly = true)
    public CourseResponseDto getById(UUID id){
        Course course = courseRepository.findByIdAndIsDeletedFalseOrThrow(id);
        return courseMapper.toDto(course);
    }

    @Transactional
    public CourseResponseDto create(CourseRequestDto dto){
        Course course = courseMapper.toEntity(dto);
        Set<Group> groups = groupService.findAllByIds(dto.groupIds());
        course.addGroups(groups);
        return courseMapper.toDto(courseRepository.save(course));
    }

    @Transactional
    public CourseResponseDto update(UUID id, CourseRequestDto dto){
        Course course = courseRepository.findByIdAndIsDeletedFalseOrThrow(id);

        Set<Group> groups = groupService.findAllByIds(dto.groupIds());
        course.replaceGroups(groups);
        courseMapper.update(dto, course);

        return courseMapper.toDto(courseRepository.save(course));
    }

    @Transactional
    public void delete(UUID id){
        if (scheduleRepository.existsByCourseIdAndIsDeletedFalse(id))
            throw new DeleteNotAllowedException(
                    "Нельзя удалить курс с id " + id + ": у него есть активные занятия в расписании"
            );

        Course course = courseRepository.findByIdAndIsDeletedFalseOrThrow(id);
        course.clearGroups();
        course.setDeleted(true);
    }

    @Transactional
    public CourseResponseDto addGroups(UUID id, List<UUID> groupIds){
        Course course = courseRepository.findByIdAndIsDeletedFalseOrThrow(id);

        Set<Group> groups = groupService.findAllByIds(groupIds);
        course.addGroups(groups);

        return courseMapper.toDto(courseRepository.save(course));
    }
}
