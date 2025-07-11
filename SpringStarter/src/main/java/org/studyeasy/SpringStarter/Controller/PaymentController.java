package org.studyeasy.SpringStarter.Controller;

import org.studyeasy.SpringStarter.Model.Payment;
import org.studyeasy.SpringStarter.Model.Student;
import org.studyeasy.SpringStarter.Services.EmailService;
import org.studyeasy.SpringStarter.Services.PaymentService;
import org.studyeasy.SpringStarter.Services.RoomService;
import org.studyeasy.SpringStarter.Services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/payments")
    public String paymentManagement() {
        return "payment_management";
    }

    @GetMapping("/payment-add")
    public String goToAddPayment(){
        return "add_payment";
    }

    @GetMapping("/viewByMonthYear")
    public String viewByMonthYear() {
        return "view_payments_by_month_year";
    }
    
    @GetMapping("/payments-viewByMonthYear")
    public String viewPaymentsByMonthAndYear(@RequestParam("month") Integer month, @RequestParam("year") Integer year, @RequestParam("hostel") String hostel, Model model) {
        List<Payment> payments = paymentService.getPaymentsByMonthAndYear(month, year);
        List<Payment> finalList = new ArrayList<>();
        for(Payment temp : payments) {
            if(temp.getStudent().getRoom().getHostel().equals(hostel)) {
                finalList.add(temp);
            }
        }
        Double totalAmount = (double) 0;
        for(Payment temp : finalList) {
            totalAmount = totalAmount + temp.getAmount();
        }
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("payments", finalList);
        return "payments_by_month_year";
    }

    @GetMapping("/viewByStudent")
    public String viewByStudentName() {
        return "view_payments_by_student";
    }

    @GetMapping("/payments-viewByStudent")
    public String viewPaymentsByStudentName(@RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName, Model model) {
        List<Payment> payments = paymentService.getPaymentsByStudentName(firstName, lastName);
        model.addAttribute("payments", payments);
        return "payment_by_student";
    }

    @PostMapping("/add-payment")
    public String addPayment(@ModelAttribute Payment payment,
                             @RequestParam Integer amount,
                             @RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam LocalDate dueDate,
                             Model model) {

        Student student = studentService.findByFirstNameAndLastName(firstName, lastName);
        if(student != null){
            try {
                Long id = student.getId();
                String subject = "Payment Success";
                String mailText = "Your Payment Of "+amount+" has been successful";
                Payment savedPayment = paymentService.addPayment(payment, student.getId());
                student.setDueDate(dueDate);
                roomService.increaseCapacity(student.getRoom().getRoomNo(),student.getRoom().getHostel());
                studentService.save(student);
                model.addAttribute("message", "Payment added successfully!");
                model.addAttribute("payment", savedPayment);
                emailService.sendIndividualEmail(subject, mailText, id);
            } catch (IllegalArgumentException e) {
                model.addAttribute("message", e.getMessage());
            }
        }
        else{
            model.addAttribute("error", "Student not found");
        }
        return "payment_management";
    }
}
