package com.course.krch.educationsystem.controller;

import com.course.krch.educationsystem.dto.request.ScheduleFilterDto;
import com.course.krch.educationsystem.dto.request.ScheduleRequestDto;
import com.course.krch.educationsystem.dto.response.ScheduleResponseDto;
import com.course.krch.educationsystem.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/schedules")
@AllArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public Page<ScheduleResponseDto> getAll(
            @ModelAttribute @ParameterObject ScheduleFilterDto filter,
            @ParameterObject Pageable pageable
    ){
        return scheduleService.getAll(filter, pageable);
    }

    @GetMapping("/{id}")
    public ScheduleResponseDto getById(@PathVariable UUID id){
        return scheduleService.getById(id);
    }

    @PostMapping
    public ScheduleResponseDto create(@Valid @RequestBody ScheduleRequestDto dto){
        return scheduleService.create(dto);
    }

    @PutMapping("/{id}")
    public ScheduleResponseDto update(
            @PathVariable UUID id,
            @Valid @RequestBody ScheduleRequestDto dto
    ){
        return scheduleService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id){
        scheduleService.delete(id);
    }
}
