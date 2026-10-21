package com.course.krch.educationsystem.entity;

import com.course.krch.educationsystem.exception.NotValidException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @ManyToMany(mappedBy = "students")
    private Set<Group> groups = new HashSet<>();

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public void addGroup(Group group) {
        groups.add(group);
        group.getStudents().add(this);
    }

    public void removeGroup(Group group) {
        if (groups.contains(group) && !isDeleted && groups.size() == 1)
            throw new NotValidException("У студента должна быть минимум одна группа, id: " + id);

        groups.remove(group);
        group.getStudents().remove(this);
    }

    public void addGroups(Set<Group> newGroups){
        for (Group group : newGroups) {
            addGroup(group);
        }
    }

    public void replaceGroups(Set<Group> newGroups){
        if (!isDeleted && newGroups.isEmpty())
            throw new NotValidException("У студента должна быть минимум одна группа, id: " + id);

        for (Group newGroup : newGroups) {
            addGroup(newGroup);
        }

        for (Group group : new ArrayList<>(groups)) {
            if (!newGroups.contains(group))
                removeGroup(group);
        }
    }

    public void clearGroups(){
        for (Group group : new ArrayList<>(groups)) {
            removeGroup(group);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student student)) return false;
        return id != null && Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
