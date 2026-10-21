package com.course.krch.educationsystem.mapper;

import com.course.krch.educationsystem.dto.response.CourseShortResponseDto;
import com.course.krch.educationsystem.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CourseShortMapper {

    CourseShortResponseDto toShortDto(Course course);

}
