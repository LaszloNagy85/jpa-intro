package com.codecool.jpaintro.repository;

import com.codecool.jpaintro.entity.Address;
import com.codecool.jpaintro.entity.Location;
import com.codecool.jpaintro.entity.School;
import com.codecool.jpaintro.entity.Student;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class AllRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void saveOneSimple() {
        Student john = Student.builder()
                .email("john@codecool.com")
                .name("John")
                .build();

        studentRepository.save(john);

        List<Student> studentList = studentRepository.findAll();
        assertThat(studentList).hasSize(1);

    }

    @Test(expected = DataIntegrityViolationException.class)
    public void saveUniqueFieldTwice() {
        Student student = Student.builder()
                .email("john@codecool.com")
                .name("John")
                .build();

        studentRepository.save(student);

        Student student2 = Student.builder()
                .email("john@codecool.com")
                .name("Peter")
                .build();

        studentRepository.saveAndFlush(student2);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void emailShouldNotBeNul() {
        Student student = Student.builder()
                .name("John")
                .build();

        studentRepository.save(student);

    }

    @Test
    public void transientIsNotSaved() {
        Student student = Student.builder()
                .email("john@codecool.com")
                .name("John")
                .birthDate(LocalDate.of(1980, 3, 5))
                .build();

        student.calculateAge();
        assertThat(student.getAge()).isGreaterThanOrEqualTo(31);

        studentRepository.save(student);
        entityManager.clear();

        List<Student> students = studentRepository.findAll();
        assertThat(students).allMatch(student1 -> student1.getAge() == 0L);

    }

    @Test
    public void addressIsPersistedWithStudent() {
        Address address = Address.builder()
                .country("Hungary")
                .city("Budapest")
                .address("Nagymezo street 44")
                .zipCode(1065)
                .build();

        Student student = Student.builder()
                .email("john@codecool.com")
                .name("John")
                .address(address)
                .birthDate(LocalDate.of(1980, 3, 5))
                .build();

        studentRepository.save(student);

        List<Address> addresses = addressRepository.findAll();
        assertThat(addresses)
                .hasSize(1)
                .allMatch(address1 -> address.getId() > 0L);

    }

    @Test
    public void studentsArePersistentAndDeletedWithNewSchool() {
        Student student = Student.builder()
                .email("john@codecool.com")
                .name("John")
                .birthDate(LocalDate.of(1980, 3, 5))
                .build();

        Student student2 = Student.builder()
                .email("john2@codecool.com")
                .name("John2")
                .birthDate(LocalDate.of(1980, 3, 5))
                .build();

        School school = School.builder()
                .name("Codecool")
                .location(Location.BUDAPEST)
                .student(student)
                .student(student2)
                .build();

        schoolRepository.save(school);
        List<Student> students = studentRepository.findAll();
        assertThat(students)
                .hasSize(2)
                .anyMatch(student1 -> student1.getEmail().equals("john2@codecool.com"));

        schoolRepository.delete(school);
        List<Student> studentsRemoved = studentRepository.findAll();
        assertThat(studentsRemoved)
                .hasSize(0);

    }

    @Test
    public void findByNameStartingWithOrBirthDateBetween() {
        Student john = Student.builder()
                .email("john2@codecool.com")
                .name("John")
                .build();
        Student jane = Student.builder()
                .email("jane@codecool.com")
                .name("Jane")
                .build();

        Student martha = Student.builder()
                .email("martha@codecool.com")
                .name("Martha")
                .build();

        Student johnDate1 = Student.builder()
                .email("jd1@codecool.com")
                .birthDate(LocalDate.of(2010, 10, 31))
                .build();

        Student johnDate2 = Student.builder()
                .email("jd2@codecool.com")
                .birthDate(LocalDate.of(2011, 9, 30))
                .build();

        studentRepository.saveAll(Lists.newArrayList(john, jane, martha, johnDate1, johnDate2));

        List<Student> filteredStudents = studentRepository.findByNameStartingWithOrBirthDateBetween("J",
                LocalDate.of(2009, 10, 10),
                LocalDate.of(2011, 1, 1));

        assertThat(filteredStudents).containsExactlyInAnyOrder( john, jane, johnDate1);

    }

    @Test
    public void findAllCountry() {

        Student first = Student.builder()
                .email("first@codecool.com")
                .name("")
                .address(Address.builder()
                        .country("Hungary")
                        .build())
                .build();

        Student second = Student.builder()
                .email("second@codecool.com")
                .name("")
                .address(Address.builder()
                        .country("Hungary")
                        .build())
                .build();

        Student third = Student.builder()
                .email("third@codecool.com")
                .name("")
                .address(Address.builder()
                        .country("Poland")
                        .build())
                .build();

        Student forth = Student.builder()
                .email("forth@codecool.com")
                .name("")
                .address(Address.builder()
                        .country("Poland")
                        .build())
                .build();

        studentRepository.saveAll(Lists.newArrayList(first, second, third, forth));

        List<String> countries = studentRepository.findAllCountry();

        assertThat(countries)
                .hasSize(2)
                .containsOnlyOnce("Hungary", "Poland");
    }

    @Test
    public void updateAllToUSAByStudentName() {
        Address address1 = Address.builder().country("Poland").build();

        Address address2 = Address.builder().country("Hungary").build();

        Address address3 = Address.builder().country("Germany").build();

        Student smith = Student.builder()
                .email("smith@codecool.com")
                .name("Smith")
                .address(address1)
                .build();

        studentRepository.save(smith);
        addressRepository.saveAll(Lists.newArrayList(address2, address3));

        assertThat(addressRepository.findAll())
                .hasSize(3)
                .noneMatch(address -> address.getCountry().equals("USA"));

        int updatedRows = addressRepository.updateAllToUSAByStudentName("Smith");

        assertThat(updatedRows == 1);

        assertThat(addressRepository.findAll())
                .hasSize(3)
                .anyMatch(address -> address.getCountry().equals("USA"));

    }


}