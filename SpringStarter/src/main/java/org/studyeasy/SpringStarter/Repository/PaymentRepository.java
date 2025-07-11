package org.studyeasy.SpringStarter.Repository;

import org.studyeasy.SpringStarter.Model.Payment;
import org.studyeasy.SpringStarter.Model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByPayDateBetween(LocalDate startDate, LocalDate endDate);

    List<Payment> findByStudent(Student student);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.payDate BETWEEN :startDate AND :endDate")
    Double sumAmountByPayDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
