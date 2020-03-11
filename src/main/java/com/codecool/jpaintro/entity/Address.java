package com.codecool.jpaintro.entity;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Address {

    @Id
    @GeneratedValue
    private Long id;

    private String country;

    private String city;

    private String address;

    private Integer zipCode;

    @OneToOne(mappedBy = "address")
    @EqualsAndHashCode.Exclude
    private Student student;
}

