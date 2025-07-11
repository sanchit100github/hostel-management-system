package org.studyeasy.SpringStarter.Services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.studyeasy.SpringStarter.Model.Room;
import org.studyeasy.SpringStarter.Model.Student;
import org.studyeasy.SpringStarter.Repository.RoomRepository;
import org.studyeasy.SpringStarter.Repository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Value("${spring.mvc.static-path-pattern}")
    private String photo_prefix;

    public Optional<Student> getById(Long id) {
        return studentRepository.findById(id);
    }

    public List<Student> getByAll() {
        return studentRepository.findAll();
    }

    public List<String> getAllEmails(){
        List<Student> opStudents = studentRepository.findAll();
        List<String> emailslist = new ArrayList<>();
        for (Student temp : opStudents) {
            if(temp.getActive()) {
                emailslist.add(temp.getEmail());
            }

        }
        return emailslist;
    }

    public void delete(Student student) {
        studentRepository.delete(student);
    }

    public Student save(Student student) {
        if (student.getId() == null) {
            student.setJoiningDate(LocalDate.now());
        }

        if (student.getRoom() != null) {
            // Retrieve the room using the room number from the student
            String roomNo = student.getRoom().getRoomNo();  // Correctly get room number
            String hostel = student.getRoom().getHostel(); 
            Optional<Room> roomOpt = roomRepository.getByRoomNoAndHostel(roomNo,hostel);

            if (roomOpt.isPresent()) {
                Room room = roomOpt.get();  // Extract the Room object
                if (room.getCurrCapacity() > 0) {
                    room.setCurrCapacity(room.getCurrCapacity() - 1);
                    roomRepository.save(room);  // Save the updated room
                    if (student.getPhotoPath() == null){
                        String path = photo_prefix.replace("**", "images/person.png");
                        student.setPhotoPath(path);
                    }
                    return studentRepository.save(student);  // Save the student
                } else {
                    throw new RuntimeException("Room is full");
                }
            } else {
                throw new RuntimeException("Room not found");
            }
        } else {
            throw new RuntimeException("Room not assigned to student");
        }
    }

    public Student findByFirstNameAndLastName(String firstName, String lastName) {
        return studentRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    public void updateStudent(Student student) {
        studentRepository.save(student);
    }

    public Student findStudentById(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));
    }

    public List<Student> findAllByActive() {
        List<Student> opStudents = studentRepository.findAll();
        List<Student> studentslist = new ArrayList<>();
        for (Student temp : opStudents) {
            if(temp.getActive()) {
                studentslist.add(temp);
            }

        }
        return studentslist;
    }

    public String getEmailById(Long id) {
        return studentRepository.getEmailById(id);
    }

    public Map<Student, Long> getDaysLeft(String hostel) {
        List<Student> opStudents = studentRepository.findAll();
        List<Student> finalList = new ArrayList<>();
        for(Student temp : opStudents) {
            if(temp.getRoom().getHostel().equals(hostel) && temp.getActive()) {
                finalList.add(temp);
            }
        }
        Map<Student, Long> daysMap = new LinkedHashMap<>(); // LinkedHashMap to maintain insertion order
        LocalDate today = LocalDate.now();
        YearMonth currentYearMonth = YearMonth.from(today); // Get the current month and year
    
        // Loop through all students
        for (Student temp : finalList) {
            if (temp.getActive()) {
                LocalDate dueDate = temp.getDueDate();
                if (dueDate != null) {
                    YearMonth dueYearMonth = YearMonth.from(dueDate); // Get the due date's month and year
                    if (currentYearMonth.equals(dueYearMonth) && !dueDate.isBefore(today)) {
                        long daysLeft = ChronoUnit.DAYS.between(today, dueDate);
                        daysMap.put(temp, daysLeft); // Store the student and days left
                    }
                }
            }
        }
    
        // Sort the map by days left
        return daysMap.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue()) // Sort by days left
            .collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), LinkedHashMap::putAll);
    }

    // public List<Student> getStudentsByRoomNo(String roomNo) {
    //     List<Student> stdList = studentRepository.findAll();
    //     List<Student> finalList = new ArrayList<>();
    //     for(Student temp : stdList) {
    //         if(temp.getActive() && temp.getRoom().getRoomNo().equals(roomNo)) {
    //             finalList.add(temp);
    //         }
    //     }
    //     return finalList;
    // }

    public List<Student> getStudentsByRoomNoAndHostel(String roomNo, String hostel) {
        List<Student> stdList = findAllByActive();
        List<Student> finalList = new ArrayList<>();
        for(Student temp : stdList) {
            if(temp.getRoom().getRoomNo().equals(roomNo) && temp.getRoom().getHostel().equals(hostel)) {
                finalList.add(temp);
            }
        }
        return finalList;
    }
}
