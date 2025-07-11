package org.studyeasy.SpringStarter.Controller;

import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.studyeasy.SpringStarter.Model.Payment;
import org.studyeasy.SpringStarter.Model.Student;
import org.studyeasy.SpringStarter.Services.PaymentService;
import org.studyeasy.SpringStarter.Services.StudentService;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ReportController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/make-report")
    public String makeReport() {
        return "make_report"; // Assumes you have a template for making reports
    }

    @GetMapping("/get-report")
    public void getReport(@RequestParam("month") Integer month,
                          @RequestParam("year") Integer year,
                          @RequestParam("hostel") String hostel,
                          HttpServletResponse response) throws IOException {

        // Fetch active students
        List<Student> optList = studentService.findAllByActive();
        List<Student> joinedList = new ArrayList<>();
        for (Student temp : optList) {
            if (temp.getRoom().getHostel().equals(hostel) &&
                    temp.getJoiningDate().getMonthValue() == month &&
                    temp.getJoiningDate().getYear() == year) {
                joinedList.add(temp);
            }
        }

        // Fetch payments for the selected month and year
        List<Payment> payments = paymentService.getPaymentsByMonthAndYear(month, year);
        List<Payment> finalPayments = new ArrayList<>();
        for (Payment temp : payments) {
            if (temp.getStudent().getRoom().getHostel().equals(hostel)) {
                finalPayments.add(temp);
            }
        }

        // Calculate total payment amount
        Double totalAmount = finalPayments.stream().mapToDouble(Payment::getAmount).sum();

        // Fetch students with due dates in the next month
        List<Student> finalDueDate = getStudentsWithUpcomingDueDates(optList, hostel, month, year);

        // Prepare the HTML content for the PDF
        String htmlContent = generateHtmlContent(hostel, month, year, joinedList, finalPayments, totalAmount, finalDueDate);

        // Setup response for PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=report.pdf");

        // Convert HTML to PDF
        HtmlConverter.convertToPdf(htmlContent, response.getOutputStream());
    }

    private List<Student> getStudentsWithUpcomingDueDates(List<Student> students, String hostel, int month, int year) {
        List<Student> finalDueDate = new ArrayList<>();
        for (Student temp : students) {
            if (temp.getRoom().getHostel().equals(hostel) && temp.getDueDate() != null) {
                if ((month == 12 && temp.getDueDate().getMonthValue() == 1 && temp.getDueDate().getYear() == year + 1) ||
                    (temp.getDueDate().getMonthValue() == month + 1 && temp.getDueDate().getYear() == year)) {
                    finalDueDate.add(temp);
                }
            }
        }
        return finalDueDate;
    }

    private String generateHtmlContent(String hostel, Integer month, Integer year,
                                       List<Student> joinedList, List<Payment> finalPayments,
                                       Double totalAmount, List<Student> finalDueDate) {
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<!DOCTYPE html>")
                .append("<html lang='en'>")
                .append("<head>")
                .append("<meta charset='UTF-8'>")
                .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                .append("<title>Hostel Report</title>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; margin: 20px; background-color: #ffffff; color: #000; }") // All text in black
                .append("h1 { text-align: center; color: black; border-bottom: 2px solid black; padding-bottom: 10px; }") // Changed to black
                .append("h3 { color: black; }") // Heading text in black
                .append("table { width: 100%; border-collapse: collapse; margin: 20px 0; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); background-color: #ffffff; }") // White background for tables
                .append("th, td { padding: 12px; text-align: left; border: 1px solid #ddd; }") // Text in black
                .append("th { background-color: #cccccc; color: #000; }") // Grey for header
                .append("td { background-color: #ffffff; color: #000; }") // White for rows
                .append("tr:nth-child(even) { background-color: #f9f9f9; }") // Light grey for even rows
                .append("tr:hover { background-color: #d0d0d0; }") // Hover effect for rows
                .append(".total-amount { font-weight: bold; font-size: 1.2em; margin: 20px 0; text-align: left; color: #000; }") // Total amount in black
                .append("hr { border: 1px solid black; margin: 20px 0; }") // Black horizontal lines
                .append("</style>")
                .append("</head>")
                .append("<body>");

        htmlContent.append("<h1>Report - ").append(hostel).append("</h1>")
                   .append("<h3>Month: ").append(month).append(" Year: ").append(year).append("</h3>");

        // Students Joined
        htmlContent.append("<h3>Students Joined in ").append(month).append("/").append(year).append(":</h3>")
                .append("<table><tr><th>Name</th><th>Room Number</th><th>Joining Date</th></tr>");
        for (Student student : joinedList) {
            htmlContent.append("<tr><td>").append(student.getFirstName()).append(" ").append(student.getLastName())
                    .append("</td><td>").append(student.getRoom().getRoomNo())
                    .append("</td><td>").append(student.getJoiningDate().toString())
                    .append("</td></tr>");
        }
        htmlContent.append("</table>");

        // Payments
        htmlContent.append("<h3>Payments for ").append(month).append("/").append(year).append(":</h3>")
                .append("<table><tr><th>Payment ID</th><th>Student</th><th>Amount</th><th>Payment Date</th></tr>");
        for (Payment payment : finalPayments) {
            htmlContent.append("<tr><td>").append(payment.getId())
                    .append("</td><td>").append(payment.getStudent().getFirstName()).append(" ").append(payment.getStudent().getLastName())
                    .append("</td><td>").append(payment.getAmount())
                    .append("</td><td>").append(payment.getPayDate().toString()) // Assuming you have a getPaymentDate method
                    .append("</td></tr>");
        }
        htmlContent.append("</table>");

        // Total Amount
        htmlContent.append("<div class='total-amount'>Total Payment: Rs ").append(totalAmount).append("</div>");

        // Students with Upcoming Due Dates
        htmlContent.append("<h3>Students with Due Date in Next Month:</h3>")
                .append("<table><tr><th>Name</th><th>Due Date</th></tr>");
        for (Student student : finalDueDate) {
            htmlContent.append("<tr><td>").append(student.getFirstName()).append(" ").append(student.getLastName())
                    .append("</td><td>").append(student.getDueDate().toString())  // Assuming you have a getDueDate method
                    .append("</td></tr>");
        }
        htmlContent.append("</table>");

        // Add horizontal line for separation
        htmlContent.append("<hr>");

        htmlContent.append("</body>")
                .append("</html>");

        return htmlContent.toString();
    }
}
