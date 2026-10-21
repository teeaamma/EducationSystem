package com.course.krch.educationsystem.mapper;

import com.course.krch.educationsystem.dto.request.CourseRequestDto;
import com.course.krch.educationsystem.dto.response.CourseResponseDto;
import com.course.krch.educationsystem.entity.Course;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = GroupShortMapper.class
)
public interface CourseMapper {

    CourseResponseDto toDto(Course course);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Course toEntity(CourseRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void update(CourseRequestDto dto, @MappingTarget Course course);
}
