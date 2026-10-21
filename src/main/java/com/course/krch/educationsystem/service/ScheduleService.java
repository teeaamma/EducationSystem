package com.course.krch.educationsystem.service;

import com.course.krch.educationsystem.dto.request.ScheduleFilterDto;
import com.course.krch.educationsystem.dto.request.ScheduleRequestDto;
import com.course.krch.educationsystem.dto.response.ScheduleResponseDto;
import com.course.krch.educationsystem.entity.Course;
import com.course.krch.educationsystem.entity.Group;
import com.course.krch.educationsystem.entity.Schedule;
import com.course.krch.educationsystem.entity.Teacher;
import com.course.krch.educationsystem.exception.NotFoundException;
import com.course.krch.educationsystem.exception.NotValidException;
import com.course.krch.educationsystem.mapper.ScheduleMapper;
import com.course.krch.educationsystem.repository.CourseRepository;
import com.course.krch.educationsystem.repository.GroupRepository;
import com.course.krch.educationsystem.repository.ScheduleRepository;
import com.course.krch.educationsystem.repository.TeacherRepository;
import com.course.krch.educationsystem.specification.ScheduleSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final GroupRepository groupRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;

    private final ScheduleMapper scheduleMapper;

    @Transactional(readOnly = true)
    public Page<ScheduleResponseDto> getAll(ScheduleFilterDto filter, Pageable pageable){
        return scheduleRepository.findAll(ScheduleSpecification.from(filter), pageable)
                .map(scheduleMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ScheduleResponseDto getById(UUID id){
        Schedule schedule = scheduleRepository.findByIdAndIsDeletedFalseOrThrow(id);
        return scheduleMapper.toDto(schedule);
    }

    @Transactional
    public ScheduleResponseDto create(ScheduleRequestDto dto){
        validateCreation(dto);

        Group group = groupRepository.findByIdAndIsDeletedFalseOrThrow(dto.groupId());
        Teacher teacher = teacherRepository.findByIdAndIsDeletedFalseOrThrow(dto.teacherId());
        Course course = courseRepository.findByIdAndIsDeletedFalseOrThrow(dto.courseId());

        Schedule schedule = scheduleMapper.toEntity(dto, group, teacher, course);
        return scheduleMapper.toDto(scheduleRepository.save(schedule));
    }

    private void validateCreation(ScheduleRequestDto dto){
        if (scheduleRepository.hasConflict(dto.courseId(), dto.teacherId()))
            throw new NotValidException(
                    "Невозможно создать занятие: данный предмет ведет другой преподаватель"
            );
    }

    @Transactional
    public ScheduleResponseDto update(UUID id, ScheduleRequestDto dto){
        Schedule schedule = scheduleRepository.findByIdAndIsDeletedFalseOrThrow(id);
        validateUpdate(id, dto);

        Group group = groupRepository.findByIdAndIsDeletedFalseOrThrow(dto.groupId());
        Teacher teacher = teacherRepository.findByIdAndIsDeletedFalseOrThrow(dto.teacherId());
        Course course = courseRepository.findByIdAndIsDeletedFalseOrThrow(dto.courseId());

        scheduleMapper.update(
                dto,
                group,
                teacher,
                course,
                schedule
        );

        return scheduleMapper.toDto(scheduleRepository.save(schedule));
    }

    private void validateUpdate(UUID scheduleId, ScheduleRequestDto dto){
        if (scheduleRepository.hasConflictExcluding(
                dto.courseId(),
                dto.teacherId(),
                scheduleId
        ))
            throw new NotValidException(
                    "Невозможно обновить занятие: данный предмет ведет другой преподаватель"
            );
    }

    @Transactional
    public void delete(UUID id){
        int updatedRows = scheduleRepository.softDeleteById(id);

        if (updatedRows == 0)
            throw new NotFoundException("Занятия с id = " + id + " не существует");
    }

    @Transactional(readOnly = true)
    public Page<ScheduleResponseDto> getAllByGroupId(UUID id, Pageable pageable){
        return scheduleRepository.findAllByGroupIdAndIsDeletedFalse(id, pageable)
                .map(scheduleMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ScheduleResponseDto> getAllByTeacherId(UUID id, Pageable pageable){
        return scheduleRepository.findAllByTeacherIdAndIsDeletedFalse(id, pageable)
                .map(scheduleMapper::toDto);
    }
}
