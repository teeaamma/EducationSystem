package com.course.krch.educationsystem.mapper;

import com.course.krch.educationsystem.dto.response.GroupShortResponseDto;
import com.course.krch.educationsystem.entity.Group;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GroupShortMapper {

    GroupShortResponseDto toShortDto(Group group);

}
