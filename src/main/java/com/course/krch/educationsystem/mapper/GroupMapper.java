package com.course.krch.educationsystem.mapper;

import com.course.krch.educationsystem.dto.request.GroupRequestDto;
import com.course.krch.educationsystem.dto.response.GroupResponseDto;
import com.course.krch.educationsystem.entity.Group;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = StudentShortMapper.class
)
public interface GroupMapper {

    GroupResponseDto toDto(Group group);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Group toEntity(GroupRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void update(GroupRequestDto dto, @MappingTarget Group group);
}
