package com.course.krch.educationsystem.mapper;

import com.course.krch.educationsystem.dto.response.StudentShortResponseDto;
import com.course.krch.educationsystem.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StudentShortMapper {

    StudentShortResponseDto toShortDto(Student student);

}
