package com.course.krch.educationsystem.mapper;


import com.course.krch.educationsystem.dto.request.StudentRequestDto;
import com.course.krch.educationsystem.dto.response.StudentResponseDto;
import com.course.krch.educationsystem.entity.Student;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = GroupShortMapper.class
)
public interface StudentMapper {

    StudentResponseDto toDto(Student student);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Student toEntity(StudentRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void update(StudentRequestDto dto, @MappingTarget Student student);
}
