package com.codecool.jpaintro.repository;

import com.codecool.jpaintro.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByNameStartingWithOrBirthDateBetween(String name, LocalDate from, LocalDate to);

    @Query("SELECT distinct s.address.country from Student s")
    List<String> findAllCountry();

}
