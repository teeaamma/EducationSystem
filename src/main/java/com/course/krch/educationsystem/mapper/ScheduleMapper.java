package com.course.krch.educationsystem.mapper;


import com.course.krch.educationsystem.dto.request.ScheduleRequestDto;
import com.course.krch.educationsystem.dto.response.ScheduleResponseDto;
import com.course.krch.educationsystem.entity.Course;
import com.course.krch.educationsystem.entity.Group;
import com.course.krch.educationsystem.entity.Schedule;
import com.course.krch.educationsystem.entity.Teacher;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {
                GroupShortMapper.class,
                TeacherMapper.class,
                CourseShortMapper.class
        }
)
public interface ScheduleMapper {

    ScheduleResponseDto toDto(Schedule schedule);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "group", source = "group")
    @Mapping(target = "teacher", source = "teacher")
    @Mapping(target = "course", source = "course")
    Schedule toEntity(
            ScheduleRequestDto dto,
            Group group,
            Teacher teacher,
            Course course
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void update(
            ScheduleRequestDto dto,
            Group group,
            Teacher teacher,
            Course course,
            @MappingTarget Schedule schedule
    );
}
