package com.course.krch.educationsystem.controller;

import com.course.krch.educationsystem.dto.request.CourseRequestDto;
import com.course.krch.educationsystem.dto.response.CourseResponseDto;
import com.course.krch.educationsystem.service.CourseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@AllArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public Page<CourseResponseDto> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @ParameterObject Pageable pageable
    ){
        return courseService.getAll(name, description, pageable);
    }

    @GetMapping("/{id}")
    public CourseResponseDto getById(@PathVariable UUID id){
        return courseService.getById(id);
    }

    @PostMapping
    public CourseResponseDto create(@Valid @RequestBody CourseRequestDto dto){
        return courseService.create(dto);
    }

    @PutMapping("/{id}")
    public CourseResponseDto update(
            @PathVariable UUID id,
            @Valid @RequestBody CourseRequestDto dto
    ){
        return courseService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id){
        courseService.delete(id);
    }

    @PostMapping("/{id}/addGroups")
    public CourseResponseDto addGroups(
            @PathVariable UUID id,
            @RequestBody @NotEmpty List<@NotNull UUID> groupIds
    ){
        return courseService.addGroups(id, groupIds);
    }
}
