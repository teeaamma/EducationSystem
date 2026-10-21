package com.course.krch.educationsystem.mapper;

import com.course.krch.educationsystem.dto.request.TeacherRequestDto;
import com.course.krch.educationsystem.dto.response.TeacherResponseDto;
import com.course.krch.educationsystem.entity.Teacher;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TeacherMapper {

    TeacherResponseDto toDto(Teacher teacher);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Teacher toEntity(TeacherRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void update(TeacherRequestDto dto, @MappingTarget Teacher teacher);
}
