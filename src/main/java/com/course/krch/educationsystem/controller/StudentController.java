package com.course.krch.educationsystem.controller;

import com.course.krch.educationsystem.dto.request.StudentRequestDto;
import com.course.krch.educationsystem.dto.response.StudentResponseDto;
import com.course.krch.educationsystem.service.StudentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/students")
@AllArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public Page<StudentResponseDto> getAll(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @ParameterObject Pageable pageable
    ){
        return studentService.getAll(firstName, lastName, pageable);
    }

    @GetMapping("/{id}")
    public StudentResponseDto getById(@PathVariable UUID id){
        return studentService.getById(id);
    }

    @PostMapping
    public StudentResponseDto create(@Valid @RequestBody StudentRequestDto dto){
        return studentService.create(dto);
    }

    @PutMapping("/{id}")
    public StudentResponseDto update(
            @PathVariable UUID id,
            @Valid @RequestBody StudentRequestDto dto
    ){
        return studentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id){
        studentService.delete(id);
    }
}
