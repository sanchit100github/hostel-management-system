package org.studyeasy.SpringStarter.Services;

import org.studyeasy.SpringStarter.Model.Payment;
import org.studyeasy.SpringStarter.Model.Student;
import org.studyeasy.SpringStarter.Repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private StudentService studentService;

    // Add a payment and ensure the student exists
    public Payment addPayment(Payment payment, Long id) {
        Student student = studentService.findStudentById(id);
        if (student == null) {
            throw new IllegalArgumentException("Student not found");
        }
        payment.setStudent(student);
        payment.setPayDate(LocalDate.now()); // Automatically set the current date
        return paymentRepository.save(payment);
    }

    // Get payments by month and year
    public List<Payment> getPaymentsByMonthAndYear(Integer month, Integer year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return paymentRepository.findByPayDateBetween(startDate, endDate);
    }

    // Get payments by student name
    public List<Payment> getPaymentsByStudentName(String firstName, String lastName) {
        Student studentOpt = studentService.findByFirstNameAndLastName(firstName, lastName);
        if(studentOpt!=null) {
            return paymentRepository.findByStudent(studentOpt);
        }
        else {
            throw new IllegalArgumentException("Student not found");
        }
    }

    // Get total payments by month and year
    public Double getTotalPaymentsByMonthAndYear(Integer month, Integer year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return paymentRepository.sumAmountByPayDateBetween(startDate, endDate);
    }
}
