package com.course.krch.educationsystem.service;

import com.course.krch.educationsystem.dto.request.GroupRequestDto;
import com.course.krch.educationsystem.dto.response.GroupResponseDto;
import com.course.krch.educationsystem.entity.Group;
import com.course.krch.educationsystem.entity.Student;
import com.course.krch.educationsystem.exception.DeleteNotAllowedException;
import com.course.krch.educationsystem.exception.NotFoundException;
import com.course.krch.educationsystem.mapper.GroupMapper;
import com.course.krch.educationsystem.repository.GroupRepository;
import com.course.krch.educationsystem.repository.ScheduleRepository;
import com.course.krch.educationsystem.repository.StudentRepository;
import com.course.krch.educationsystem.specification.GroupSpecification;
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
public class GroupService {

    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final ScheduleRepository scheduleRepository;
    private final GroupMapper groupMapper;

    @Transactional(readOnly = true)
    public Page<GroupResponseDto> getAll(String name, Pageable pageable){
        Specification<Group> specification = Specification.allOf(
                GroupSpecification.nameContains(name),
                GroupSpecification.notDeleted()
        );

        return groupRepository.findAll(specification, pageable)
                .map(groupMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Set<Group> findAllByIds(List<UUID> ids){
        Set<Group> groups = groupRepository.findAllByIdInAndIsDeletedFalse(ids);

        List<String> missingIds = ids.stream()
                .filter(id -> !groups.stream()
                        .map(Group::getId)
                        .toList()
                        .contains(id))
                .map(UUID::toString)
                .toList();

        if (!missingIds.isEmpty())
            throw new NotFoundException(
                    String.format(
                            "Не найдены группы с id: %s",
                            String.join(", ", missingIds)
                    )
            );

        return groups;
    }

    @Transactional(readOnly = true)
    public GroupResponseDto getById(UUID id){
        Group group = groupRepository.findByIdAndIsDeletedFalseOrThrow(id);
        return groupMapper.toDto(group);
    }

    @Transactional
    public GroupResponseDto create(GroupRequestDto dto){
        Group group = groupMapper.toEntity(dto);
        Set<Student> students = findStudents(dto.studentIds());
        group.addStudents(students);
        return groupMapper.toDto(groupRepository.save(group));
    }

    @Transactional
    public GroupResponseDto update(UUID id, GroupRequestDto dto){
        Group group = groupRepository.findByIdAndIsDeletedFalseOrThrow(id);

        Set<Student> students = findStudents(dto.studentIds());
        group.replaceStudents(students);
        groupMapper.update(dto, group);

        return groupMapper.toDto(groupRepository.save(group));
    }

    private Set<Student> findStudents(List<UUID> ids){
        Set<Student> students = studentRepository.findAllByIdInAndIsDeletedFalse(ids);

        List<String> missingIds = ids.stream()
                .filter(id -> !students.stream()
                        .map(Student::getId)
                        .toList()
                        .contains(id))
                .map(UUID::toString)
                .toList();

        if (!missingIds.isEmpty())
            throw new NotFoundException(
                    String.format(
                            "Не найдены студенты с id: %s",
                            String.join(", ", missingIds)
                    )
            );

        return students;
    }

    @Transactional
    public void delete(UUID id){
        if (scheduleRepository.existsByGroupIdAndIsDeletedFalse(id))
            throw new DeleteNotAllowedException(
                    "Нельзя удалить группу с id " + id + ": у нее есть активные занятия в расписании"
            );

        Group group = groupRepository.findByIdAndIsDeletedFalseOrThrow(id);
        group.clearStudents();
        group.setDeleted(true);
    }
}
