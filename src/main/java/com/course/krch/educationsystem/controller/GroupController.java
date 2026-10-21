package com.course.krch.educationsystem.controller;

import com.course.krch.educationsystem.dto.request.GroupRequestDto;
import com.course.krch.educationsystem.dto.response.GroupResponseDto;
import com.course.krch.educationsystem.dto.response.ScheduleResponseDto;
import com.course.krch.educationsystem.service.GroupService;
import com.course.krch.educationsystem.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
@AllArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final ScheduleService scheduleService;

    @GetMapping
    public Page<GroupResponseDto> getAll(
            @RequestParam(required = false) String name,
            @ParameterObject Pageable pageable
    ){
        return groupService.getAll(name, pageable);
    }

    @GetMapping("/{id}")
    public GroupResponseDto getById(@PathVariable UUID id){
        return groupService.getById(id);
    }

    @PostMapping
    public GroupResponseDto create(@Valid @RequestBody GroupRequestDto dto){
        return groupService.create(dto);
    }

    @PutMapping("/{id}")
    public GroupResponseDto update(
            @PathVariable UUID id,
            @Valid @RequestBody GroupRequestDto dto
    ){
        return groupService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id){
        groupService.delete(id);
    }

    @GetMapping("/{id}/schedules")
    public Page<ScheduleResponseDto> getSchedules(
            @PathVariable UUID id,
            @ParameterObject Pageable pageable
    ){
        return scheduleService.getAllByGroupId(id, pageable);
    }
}
