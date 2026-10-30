package com.course.krch.educationsystem.service;

import com.course.krch.educationsystem.dto.request.StudentRequestDto;
import com.course.krch.educationsystem.dto.response.StudentResponseDto;
import com.course.krch.educationsystem.entity.Group;
import com.course.krch.educationsystem.entity.Student;
import com.course.krch.educationsystem.mapper.StudentMapper;
import com.course.krch.educationsystem.repository.StudentRepository;
import com.course.krch.educationsystem.specification.StudentSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final GroupService groupService;
    private final StudentMapper studentMapper;

    @Transactional(readOnly = true)
    public Page<StudentResponseDto> getAll(String firstName, String lastName, Pageable pageable){
        Specification<Student> specification = Specification.allOf(
                StudentSpecification.firstNameContains(firstName),
                StudentSpecification.lastNameContains(lastName),
                StudentSpecification.notDeleted()
        );

        return studentRepository.findAll(specification, pageable)
                .map(studentMapper::toDto);
    }

    @Transactional(readOnly = true)
    public StudentResponseDto getById(UUID id){
        Student student = studentRepository.findByIdAndIsDeletedFalseOrThrow(id);
        return studentMapper.toDto(student);
    }

    @Transactional
    public StudentResponseDto create(StudentRequestDto dto){
        Student student = studentMapper.toEntity(dto);
        Set<Group> groups = groupService.findAllByIds(dto.groupIds());
        student.addGroups(groups);
        return studentMapper.toDto(studentRepository.save(student));
    }

    @Transactional
    public StudentResponseDto update(UUID id, StudentRequestDto dto){
        Student student = studentRepository.findByIdAndIsDeletedFalseOrThrow(id);

        Set<Group> groups = groupService.findAllByIds(dto.groupIds());
        student.replaceGroups(groups);
        studentMapper.update(dto, student);

        return studentMapper.toDto(studentRepository.save(student));
    }

    @Transactional
    public void delete(UUID id){
        Student student = studentRepository.findByIdAndIsDeletedFalseOrThrow(id);
        student.setDeleted(true);
        student.clearGroups();
    }
}
