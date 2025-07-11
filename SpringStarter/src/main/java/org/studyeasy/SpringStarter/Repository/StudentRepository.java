package org.studyeasy.SpringStarter.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.studyeasy.SpringStarter.Model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByFirstNameAndLastName(String firstName, String lastName);
    String getEmailById(Long id);
}
