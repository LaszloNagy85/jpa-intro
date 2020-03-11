package com.codecool.jpaintro;

import com.codecool.jpaintro.entity.Address;
import com.codecool.jpaintro.entity.Location;
import com.codecool.jpaintro.entity.School;
import com.codecool.jpaintro.entity.Student;
import com.codecool.jpaintro.repository.AddressRepository;
import com.codecool.jpaintro.repository.SchoolRepository;
import com.codecool.jpaintro.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.Arrays;

@SpringBootApplication
public class JpaIntroApplication {

//    @Autowired
//    StudentRepository studentRepository;
//
//    @Autowired
//    AddressRepository addressRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    public static void main(String[] args) {
        SpringApplication.run(JpaIntroApplication.class, args);
    }

    @Bean
    @Profile("production")
    public CommandLineRunner init() {
        return args -> {

//            Student john = Student.builder()
//                    .email("john@codecool.com")
//                    .name("John")
//                    .address(Address.builder()
//                            .country("Hungary")
//                            .city("Miskolc")
//                            .build())
//                    .phoneNumber("0620555-6677")
//                    .phoneNumber("0620555-7788")
//                    .phoneNumber("0620555-8899")
//                    .birthDate(LocalDate.of(1980, 3, 5))
//                    .build();
//
//            john.calculateAge();
//            studentRepository.save(john);
        Address address = Address.builder()
                .address("Nagymezo street 44")
                .city("Budapest")
                .country("Hungary")
                .build();

        Address address2 = Address.builder()
                .address("Alkotmany street 20")
                .city("Budapest")
                .country("Hungary")
                .build();

        Student john = Student.builder()
                .email("john@codecool.com")
                .name("John")
                .birthDate(LocalDate.of(1989, 4, 10))
                .address(address)
                .phoneNumbers(Arrays.asList("123-5566", "555-6677"))
                .build();

        Student peter = Student.builder()
                .email("peter@codecool.com")
                .name("Peter")
                .birthDate(LocalDate.of(1984, 10, 10))
                .address(address2)
                .phoneNumbers(Arrays.asList("123-3566", "155-6677"))
                .build();

        School school = School.builder()
                .name("Codecool")
                .location(Location.BUDAPEST)
                .student(john)
                .student(peter)
                .build();

        john.setSchool(school);
        peter.setSchool(school);

        schoolRepository.save(school);
        };
    }
}
