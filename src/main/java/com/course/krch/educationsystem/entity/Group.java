package com.course.krch.educationsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "groups")
@Getter
@Setter
@NoArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @ManyToMany
    @JoinTable(
            name = "group_student",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Student> students = new HashSet<>();

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public void addStudent(Student student){
        student.addGroup(this);
    }

    public void removeStudent(Student student){
        student.removeGroup(this);
    }

    public void addStudents(Set<Student> newStudents){
        for (Student student : newStudents) {
            addStudent(student);
        }
    }

    public void replaceStudents(Set<Student> newStudents){
        for (Student newStudent : newStudents) {
            addStudent(newStudent);
        }

        for (Student student : new ArrayList<>(students)) {
            if (!newStudents.contains(student))
                removeStudent(student);
        }
    }

    public void clearStudents(){
        for (Student student : new ArrayList<>(students)) {
            removeStudent(student);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group group)) return false;
        return id != null && Objects.equals(id, group.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
