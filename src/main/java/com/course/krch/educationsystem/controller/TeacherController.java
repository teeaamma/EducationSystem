package com.course.krch.educationsystem.controller;

import com.course.krch.educationsystem.dto.request.TeacherRequestDto;
import com.course.krch.educationsystem.dto.response.ScheduleResponseDto;
import com.course.krch.educationsystem.dto.response.TeacherResponseDto;
import com.course.krch.educationsystem.service.ScheduleService;
import com.course.krch.educationsystem.service.TeacherService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teachers")
@AllArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final ScheduleService scheduleService;

    @GetMapping
    public Page<TeacherResponseDto> getAll(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @ParameterObject Pageable pageable
    ){
        return teacherService.getAll(firstName, lastName, pageable);
    }

    @GetMapping("/{id}")
    public TeacherResponseDto getById(@PathVariable UUID id){
        return teacherService.getById(id);
    }

    @PostMapping
    public TeacherResponseDto create(@Valid @RequestBody TeacherRequestDto dto){
        return teacherService.create(dto);
    }

    @PutMapping("/{id}")
    public TeacherResponseDto update(
            @PathVariable UUID id,
            @Valid @RequestBody TeacherRequestDto dto
    ){
        return teacherService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id){
        teacherService.delete(id);
    }

    @GetMapping("/{id}/schedules")
    public Page<ScheduleResponseDto> getSchedules(
            @PathVariable UUID id,
            @ParameterObject Pageable pageable
    ){
        return scheduleService.getAllByTeacherId(id, pageable);
    }
}
