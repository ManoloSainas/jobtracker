package com.manolo.jobtracker.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "tag",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_tag_name",
                        columnNames = "name"
                )
        }
)
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(
            nullable = false,
            unique = true
    )
    private String name;


    @ManyToMany(mappedBy = "tags")
    private Set<JobApplication> applications = new HashSet<>();


    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Tag tag = (Tag) o;

        return id != null && id.equals(tag.id);
    }


    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}