package org.studyeasy.SpringStarter.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.studyeasy.SpringStarter.Model.Student;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;


@Service
public class EmailService {

    @Autowired
    private StudentService studentService;

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAnnouncementEmail(String hostel, String subject, String announcementText) {
        List<Student> stdList = studentService.getByAll(); 
        List<String> recipientEmails = new ArrayList<>();

        for (Student temp : stdList) {
            if(temp.getRoom().getHostel().equals(hostel)) {
                recipientEmails.add(temp.getEmail());
            }
        }

        if (recipientEmails.isEmpty()) {
            System.out.println("No recipient emails found.");
        }

        for (String email : recipientEmails) {
            try {
                sendEmail(email, subject, announcementText);
            } catch (MessagingException e) {
                // Log the error and handle it as needed
                System.err.println("Failed to send email to " + email + ": " + e.getMessage());
            }
        }
    }

    public void sendIndividualEmail(String subject, String mailText, Long id) {
        Optional<Student> studentOpt = studentService.getById(id);
        if(studentOpt.isPresent()) {
            Student student = studentOpt.get();
            String email = student.getEmail();
            try {
                sendEmail(email, subject, mailText);
            } catch (MessagingException e) {
                System.err.println("Failed to send email to " + email + ": " + e.getMessage());
            }
        }
    }

    private void sendEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        mailSender.send(message);
    }


}
