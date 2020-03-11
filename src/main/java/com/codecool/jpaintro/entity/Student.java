package com.codecool.jpaintro.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Student {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private LocalDate birthDate;

    @Transient
    private long age;

    @OneToOne(cascade = CascadeType.PERSIST)
    private Address address;

    @ElementCollection
    @Singular
    private List<String> phoneNumbers;

    @ManyToOne
    private School school;

    public void calculateAge() {
        if (birthDate != null) {
            age = ChronoUnit.YEARS.between(birthDate, LocalDate.now());
        }
    }

}
