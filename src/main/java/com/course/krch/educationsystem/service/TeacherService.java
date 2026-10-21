package com.course.krch.educationsystem.service;

import com.course.krch.educationsystem.dto.request.TeacherRequestDto;
import com.course.krch.educationsystem.dto.response.TeacherResponseDto;
import com.course.krch.educationsystem.entity.Teacher;
import com.course.krch.educationsystem.exception.DeleteNotAllowedException;
import com.course.krch.educationsystem.exception.NotFoundException;
import com.course.krch.educationsystem.mapper.TeacherMapper;
import com.course.krch.educationsystem.repository.ScheduleRepository;
import com.course.krch.educationsystem.repository.TeacherRepository;
import com.course.krch.educationsystem.specification.TeacherSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final ScheduleRepository scheduleRepository;
    private final TeacherMapper teacherMapper;

    @Transactional(readOnly = true)
    public Page<TeacherResponseDto> getAll(String firstName, String lastName, Pageable pageable){
        Specification<Teacher> specification = Specification.allOf(
                TeacherSpecification.firstNameContains(firstName),
                TeacherSpecification.lastNameContains(lastName),
                TeacherSpecification.notDeleted()
        );

        return teacherRepository.findAll(specification, pageable)
                .map(teacherMapper::toDto);
    }

    @Transactional(readOnly = true)
    public TeacherResponseDto getById(UUID id){
        Teacher teacher = teacherRepository.findByIdAndIsDeletedFalseOrThrow(id);
        return teacherMapper.toDto(teacher);
    }

    public TeacherResponseDto create(TeacherRequestDto dto){
        Teacher teacher = teacherMapper.toEntity(dto);
        return teacherMapper.toDto(teacherRepository.save(teacher));
    }

    @Transactional
    public TeacherResponseDto update(UUID id, TeacherRequestDto dto){
        Teacher teacher = teacherRepository.findByIdAndIsDeletedFalseOrThrow(id);
        teacherMapper.update(dto, teacher);
        return teacherMapper.toDto(teacherRepository.save(teacher));
    }

    @Transactional
    public void delete(UUID id){
        if (scheduleRepository.existsByTeacherIdAndIsDeletedFalse(id))
            throw new DeleteNotAllowedException(
                    "Нельзя удалить преподавателя с id " + id + ": у него есть активные занятия в расписании"
            );

        int updatedRows = teacherRepository.softDeleteById(id);

        if (updatedRows == 0)
            throw new NotFoundException("Преподавателя с id = " + id + " не существует");
    }
}
