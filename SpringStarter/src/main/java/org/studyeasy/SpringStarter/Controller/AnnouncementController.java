package org.studyeasy.SpringStarter.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.studyeasy.SpringStarter.Model.Student;
import org.studyeasy.SpringStarter.Services.EmailService;
import org.studyeasy.SpringStarter.Services.StudentService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
public class AnnouncementController {

    private final EmailService emailService;

    @Autowired
    private StudentService studentService;

    public AnnouncementController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/make-announcement")
    public String announcement(Model model) {
        return "announcement";
    }

    @PostMapping("/send-announcement")
    public String sendAnnouncement(@RequestParam("hostel") String hostel, @RequestParam("announcement-text") String announcementText, Model model) {

        // Get the current date and format it to dd-MM-yyyy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = LocalDate.now().format(formatter);

        // Create the email content
        String subject = "Announcement Date: " + formattedDate;

        // Send the email
        emailService.sendAnnouncementEmail(hostel, subject, announcementText);

        model.addAttribute("message", "Announcement sent successfully!");
        return "announcement"; // Redirect to a success page or show a success message
    }

    @PostMapping("/mail-sender")
    public String sendMail(@RequestParam("mail-text") String mailText, @RequestParam("id") Long id, Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = LocalDate.now().format(formatter);
        String subject = "Mail Date: " + formattedDate;
        emailService.sendIndividualEmail(subject, mailText, id);
        Optional<Student> optStudent = studentService.getById(id);
        if (optStudent.isPresent()) {
            Student student = optStudent.get();
            model.addAttribute("student", student);
            model.addAttribute("message", "Mail sent successfully!");
        }
        return "student_details"; 
    }

    @PostMapping("/send-duedate-mail/{id}/{days}")
    public String sendDuedateMail(@PathVariable("id") Long id, @PathVariable("days") Long days, Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = LocalDate.now().format(formatter);
        String subject = "Mail Date: " + formattedDate;
        String mailText = "Your payment of the Hostel Fees in due in next " + days +" days.";
        Optional<Student> studentOpt = studentService.getById(id);
        if(studentOpt.isPresent()) {
            Student std = studentOpt.get();
            emailService.sendIndividualEmail(subject, mailText, std.getId());
        }
        return "students_management"; 
    }
}
