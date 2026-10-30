package com.course.krch.educationsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String description;

    @ManyToMany
    @JoinTable(
            name = "course_group",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<Group> groups = new HashSet<>();

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public void addGroup(Group group){
        groups.add(group);
    }

    public void removeGroup(Group group){
        groups.remove(group);
    }

    public void addGroups(Set<Group> newGroups){
        for (Group group : newGroups) {
            addGroup(group);
        }
    }

    public void replaceGroups(Set<Group> newGroups){
        groups.clear();
        groups.addAll(newGroups);
    }

    public void clearGroups(){
        for (Group group : new ArrayList<>(groups)) {
            removeGroup(group);
        }
    }
}
