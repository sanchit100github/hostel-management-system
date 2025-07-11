package org.studyeasy.SpringStarter.Controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.studyeasy.SpringStarter.Model.Room;
import org.studyeasy.SpringStarter.Model.Student;
import org.studyeasy.SpringStarter.Repository.RoomRepository;
import org.studyeasy.SpringStarter.Services.StudentService;
import org.studyeasy.SpringStarter.Utils.AppUtil;
import org.studyeasy.SpringStarter.Services.RoomService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.util.StringUtils;

@Controller
public class StudentController {

    @Value("${spring.mvc.static-path-pattern}")
    private String photoPrefix;

    @Autowired
    private StudentService studentService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomService roomService;

    @GetMapping("/students_management")
    public String showStudentsManagementPage(Model model) {
        return "students_management";
    }

    @GetMapping("/add-student")
    public String showAddStudentForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("rooms", roomRepository.findAll());
        return "add_student";
    }

    @PostMapping("/student/add")
    public String addStudent(@ModelAttribute("student") Student student,
                            @RequestParam("firstName") String firstName,
                            @RequestParam("lastName") String lastName,
                            @RequestParam("roomNo") String roomNo,
                            @RequestParam("hostel") String hostel,
                            @RequestParam("file") MultipartFile file,
                            RedirectAttributes attributes,
                            Model model) {
        Optional<Room> roomOpt = roomService.getByRoomNoandHostel(roomNo,hostel);
        Student existingStudent = studentService.findByFirstNameAndLastName(firstName, lastName);
        LocalDate today = LocalDate.now();

        if (roomOpt.isPresent()) {
            if(roomOpt.get().getCurrCapacity().equals(0)) {
                model.addAttribute("error", "Room is Full");
                return "add_student";
            }
            if (existingStudent == null) {
                roomOpt.get().setRoomNo(roomNo);
                roomOpt.get().setHostel(hostel);
                student.setRoom(roomOpt.get());
                student.setActive(true);
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                try {
                    int lenght = 10;
                    boolean useLetters = true;
                    boolean useNumbers = true;
                    String generatedString = RandomStringUtils.random(lenght,useLetters,useNumbers);
                    String final_photo_name = generatedString + fileName;
                    String absolute_fileLocation = AppUtil.get_upload_path(final_photo_name);

                    Path path = Paths.get(absolute_fileLocation);
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    attributes.addFlashAttribute("message", "You successfully uploaded");
                    String relative_fileLocation = photoPrefix.replace("**","uploads/"+ final_photo_name);
                    student.setPhotoPath(relative_fileLocation);             
                } 
                catch (Exception e) {
                    System.out.println(e);
                }
                studentService.save(student);
            } 
            else {
                // Update existing student
                existingStudent.setFirstName(student.getFirstName());
                existingStudent.setMiddleName(student.getMiddleName());
                existingStudent.setLastName(student.getLastName());
                existingStudent.setContact(student.getContact());
                existingStudent.setPContact(student.getPContact());
                existingStudent.setEmail(student.getEmail());
                existingStudent.setBirthDate(student.getBirthDate());
                existingStudent.setJoiningDate(today);
                existingStudent.setDueDate(student.getDueDate());
                existingStudent.setWork(student.getWork());
                existingStudent.setDeposit(student.getDeposit());
                existingStudent.setWorkPlace(student.getWorkPlace());
                existingStudent.setWorkPosition(student.getWorkPosition());
                existingStudent.setAddress(student.getAddress());
                existingStudent.setRoom(roomOpt.get());
                existingStudent.setActive(true);
                studentService.save(existingStudent);
            }
            model.addAttribute("message", "Student Added Successfully");
            return "students_management";
        }
        model.addAttribute("error", "Room is not present");
        return "add_student";
    }

    @PostMapping("/search-student")
    public String searchStudent(@RequestParam("firstName") String firstName,
                                @RequestParam("lastName") String lastName,
                                Model model) {
        Student student = studentService.findByFirstNameAndLastName(firstName, lastName);
        if (student != null) {
            int age = Period.between(student.getBirthDate(), LocalDate.now()).getYears();
            model.addAttribute("ageOpt", age);
            model.addAttribute("formattedBirthDate", DateTimeFormatter.ofPattern("dd-MM-yyyy").format(student.getBirthDate()));
            model.addAttribute("formattedJoiningDate", DateTimeFormatter.ofPattern("dd-MM-yyyy").format(student.getJoiningDate()));
            model.addAttribute("formattedDueDate", DateTimeFormatter.ofPattern("dd-MM-yyyy").format(student.getDueDate()));
            model.addAttribute("student", student);
            return "student_details";
        } else {
            model.addAttribute("error", "Student not found");
            return "students_management";
        }
    }

    @PostMapping("/student-edit")
    public String updateStudent(@RequestParam("roomNo") String roomNo,
                                @RequestParam("hostel") String hostel,
                                @RequestParam("firstName") String firstName,
                                @RequestParam("lastName") String lastName,
                                @RequestParam("middleName") String middleName,
                                @RequestParam("contact") String contact,
                                @RequestParam("pContact") String pContact,
                                @RequestParam("pName") String pName,
                                @RequestParam("whatsapp") String whatsapp,
                                @RequestParam("email") String email,
                                @RequestParam("dueDate") LocalDate dueDate,
                                @RequestParam("birthDate") LocalDate birthDate,
                                @RequestParam("work") String work,
                                @RequestParam("workPlace") String workPlace,
                                @RequestParam("workPosition") String workPosition,
                                @RequestParam("deposit") Integer deposit,
                                @RequestParam("address") String address,
                                @RequestParam("guardianName") String guardianName,
                                @RequestParam("guardianContact") String guardianContact,
                                @RequestParam("guardianAddress") String guardianAddress,
                                @RequestParam("adhar") String adhar,
                                @RequestParam("pancard") String pancard,
                                @RequestParam("id") Long id,
                                Model model) {
        Optional<Student> existingStudentOpt = studentService.getById(id);
        if (existingStudentOpt.isPresent()) {
            Student existingStudent = existingStudentOpt.get();

            Optional<Room> newRoomOpt = roomRepository.getByRoomNoAndHostel(roomNo,hostel);
            if(existingStudent.getRoom() == newRoomOpt.get()) {
                roomService.increaseCapacity(existingStudent.getRoom().getRoomNo(),existingStudent.getRoom().getHostel());
            }
            else {
                if (newRoomOpt.isPresent()) {
                    if(newRoomOpt.get().getCurrCapacity()<=0) {
                        model.addAttribute("student", existingStudent);
                        model.addAttribute("error", "New room is full");
                        return "student_details";
                    }
                    if (existingStudent.getRoom() != null) {
                        roomService.increaseCapacity(existingStudent.getRoom().getRoomNo(),existingStudent.getRoom().getHostel());
                    }
                    Room newRoom = newRoomOpt.get();
                    existingStudent.setRoom(newRoom);
                }
                else {
                    model.addAttribute("student", existingStudent);
                    model.addAttribute("error", "New room is not present");
                    return "student_details";
                }
            }

            existingStudent.setFirstName(firstName);
            existingStudent.setMiddleName(middleName);
            existingStudent.setLastName(lastName);
            existingStudent.setContact(contact);
            existingStudent.setPContact(pContact);
            existingStudent.setPName(pName);
            existingStudent.setWhatsapp(whatsapp);
            existingStudent.setEmail(email);
            existingStudent.setDueDate(dueDate);
            existingStudent.setBirthDate(birthDate);
            existingStudent.setWork(work);
            existingStudent.setAdhar(adhar);
            existingStudent.setPancard(pancard);
            existingStudent.setDeposit(deposit);
            existingStudent.setWorkPlace(workPlace);
            existingStudent.setWorkPosition(workPosition);
            existingStudent.setAddress(address);
            existingStudent.setGuardianName(guardianName);
            existingStudent.setGuardianContact(guardianContact);
            existingStudent.setGuardianAddress(guardianAddress);

            // Save the updated student
            studentService.save(existingStudent);
            int age = Period.between(existingStudent.getBirthDate(), LocalDate.now()).getYears();
            model.addAttribute("ageOpt", age);
            model.addAttribute("formattedBirthDate", DateTimeFormatter.ofPattern("dd-MM-yyyy").format(existingStudent.getBirthDate()));
            model.addAttribute("formattedJoiningDate", DateTimeFormatter.ofPattern("dd-MM-yyyy").format(existingStudent.getJoiningDate()));
            model.addAttribute("formattedDueDate", DateTimeFormatter.ofPattern("dd-MM-yyyy").format(existingStudent.getDueDate()));
            model.addAttribute("student", existingStudent);
            model.addAttribute("message", "Student Updated");
            return "student_details";
        } else {
            model.addAttribute("error", "Some Error Occured");
            return "student_details";
        }
    }

    @GetMapping("/student-update")
    public String showUpdateStudentForm(@RequestParam("id") Long id, Model model) {
        Optional<Student> studentOpt = studentService.getById(id);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            model.addAttribute("formattedDueDate", DateTimeFormatter.ofPattern("dd-MM-yyyy").format(student.getDueDate()));
            model.addAttribute("student", student);
            model.addAttribute("room", student.getRoom());
            model.addAttribute("rooms", roomService.findAllByHostel(student.getRoom().getHostel()));
            return "update_student";
        } else {
            model.addAttribute("error", "Some Error Occured");
            return "update_student";
        }
    }

    @PostMapping("/delete-student")
    public String deleteStudent(@RequestParam("id") Long id, Model model) {
        Optional<Student> studentOpt = studentService.getById(id);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            roomService.increaseCapacity(student.getRoom().getRoomNo(), student.getRoom().getHostel()); // Fixed duplicate call
            roomService.increaseCapacity(student.getRoom().getRoomNo(), student.getRoom().getHostel());
            student.setActive(false);
            studentService.save(student);
            model.addAttribute("message", "Student Deleted");
            return "students_management";
        } else {
            model.addAttribute("error", "Some Error Occured");
            return "students_management";
        }
    }

    @GetMapping("/view-all-students")
    public String viewAllStudents(@RequestParam("hostel") String hostel, Model model) {
        List<Student> stdList = studentService.findAllByActive();
        List<Student> finalList = new ArrayList<>();
        for(Student temp : stdList) {
            if(temp.getRoom().getHostel().equals(hostel)) {
                finalList.add(temp);
            }
        }
        model.addAttribute("students", finalList);
        return "view_all_students";
        
        
    }

    @GetMapping("/view-students-by-due-date")
    public String viewStudentsByDueDate(@RequestParam("hostel") String hostel, Model model) {
        Map<Student, Long> daysLeftMap = studentService.getDaysLeft(hostel);
        model.addAttribute("daysLeftMap", daysLeftMap);
        return "view_students_by_due_date";
    }



    @GetMapping("/student/details/{id}")
    public String viewStudentDetails(@PathVariable("id") Long id, Model model) {
        Optional<Student> studentOpt = studentService.getById(id);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            int age = Period.between(student.getBirthDate(), LocalDate.now()).getYears();
            model.addAttribute("ageOpt", age);
            model.addAttribute("formattedBirthDate", DateTimeFormatter.ofPattern("dd-MM-yyyy").format(student.getBirthDate()));
            model.addAttribute("formattedJoiningDate", DateTimeFormatter.ofPattern("dd-MM-yyyy").format(student.getJoiningDate()));
            model.addAttribute("formattedDueDate", DateTimeFormatter.ofPattern("dd-MM-yyyy").format(student.getDueDate()));
            model.addAttribute("student", student);
            return "student_details";
        } else {
            return "student_details";
            
        }
    }

    @GetMapping("/mail-student")
    public String sendMail(@RequestParam("id") Long id, Model model) {
        Optional<Student> studentOpt = studentService.getById(id);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            model.addAttribute("student", student);
            return "send_mail";
        } else {
            return "404";
        }
    }
}
