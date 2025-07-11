package org.studyeasy.SpringStarter.Config;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.studyeasy.SpringStarter.Model.Student;
import org.studyeasy.SpringStarter.Model.Room;
import org.studyeasy.SpringStarter.Services.RoomService;
import org.studyeasy.SpringStarter.Services.StudentService;

@Component
public class SeedData implements CommandLineRunner {

    @Autowired
    private StudentService studentService;

    @Autowired
    private RoomService roomService;

    @Override
    public void run(String... args) throws Exception {
        List<Student> students = studentService.getByAll();
        if (students.isEmpty()) {
            List<Room> rooms = roomService.findAll();
            if (rooms.isEmpty()) {
                // Seed rooms
                Room room1 = new Room();
            room1.setCapacity(1);
            room1.setCurrCapacity(1);
            room1.setIndRent(5000);
            room1.setRoomNo("301");
            room1.setHostel("Hostel A");
            roomService.save(room1);

            Room room2 = new Room();
            room2.setCapacity(2);
            room2.setCurrCapacity(2);
            room2.setIndRent(5500);
            room2.setRoomNo("302");
            room2.setHostel("Hostel A");
            roomService.save(room2);

            Room room3 = new Room();
            room3.setCapacity(3);
            room3.setCurrCapacity(3);
            room3.setIndRent(6000);
            room3.setRoomNo("303");
            room3.setHostel("Hostel A");
            roomService.save(room3);

            Room room4 = new Room();
            room4.setCapacity(1);
            room4.setCurrCapacity(1);
            room4.setIndRent(5000);
            room4.setRoomNo("301");
            room4.setHostel("Hostel B");
            roomService.save(room4);

            Room room5 = new Room();
            room5.setCapacity(2);
            room5.setCurrCapacity(2);
            room5.setIndRent(5000);
            room5.setRoomNo("302");
            room5.setHostel("Hostel B");
            roomService.save(room5);

            Room room6 = new Room();
            room6.setCapacity(3);
            room6.setCurrCapacity(3);
            room6.setIndRent(6000);
            room6.setRoomNo("303");
            room6.setHostel("Hostel B");
            roomService.save(room6);


                // More room seeding...
                System.out.println("Seeding initial rooms...");
            }

            // Seed students
            Optional<Room> room301Opt = roomService.getByRoomNoandHostel("301","Hostel A");
            Optional<Room> room402Opt = roomService.getByRoomNoandHostel("302", "Hostel B");

            if (room301Opt.isPresent() && room402Opt.isPresent()) {
                Room room301 = room301Opt.get();
                Room room402 = room402Opt.get();

                Student student1 = new Student();
                student1.setFirstName("John");
                student1.setMiddleName("A.");
                student1.setLastName("Doe");
                student1.setContact("9876543210");
                student1.setPName("Bob");
                student1.setPContact("1234567890");
                student1.setWhatsapp("9876543210");
                student1.setEmail("sanchitkhad100@gmail.com");
                student1.setWork("Studying");
                student1.setWorkPlace("PCCOE");
                student1.setWorkPosition("3rd year");
                student1.setAdhar("rtutdgfvyt");
                student1.setPancard("frtfgygnfyt");
                student1.setBirthDate(LocalDate.of(2000, 5, 15));
                student1.setDueDate(LocalDate.of(2024, 10, 5));
                student1.setDeposit(15000);
                student1.setRoom(room301);
                student1.setAddress("123 Main Street, Anytown, India");
                student1.setActive(true);
                student1.setGuardianName("edcfvgbh");
                student1.setGuardianContact("85689456");
                student1.setGuardianAddress("wfergthrgef");


                studentService.save(student1);

                Student student2 = new Student();
                student2.setFirstName("Jane");
                student2.setMiddleName("B.");
                student2.setLastName("Smith");
                student2.setContact("9876543211");
                student2.setPName("Jake");
                student2.setPContact("1234567891");
                student2.setWhatsapp("9876543211");
                student2.setEmail("sanchit.khadkodkar22@pccoepune.org");
                student2.setWork("Working");
                student2.setWorkPlace("Google");
                student2.setWorkPosition("HR");
                student2.setAdhar("rewsadfgt");
                student2.setPancard("ugyftdredf");
                student2.setBirthDate(LocalDate.of(1995, 8, 22));
                student2.setDueDate(LocalDate.of(2024, 10, 19));
                student2.setDeposit(20000);
                student2.setRoom(room402);
                student2.setAddress("456 Elm Street, Othertown, India");
                student2.setActive(true);
                student2.setGuardianName("tuywnijrb");
                student2.setGuardianContact("956598");
                student2.setGuardianAddress("gvbtfvbnjuyt");

                studentService.save(student2);

                System.out.println("Seeding initial students...");
            } else {
                System.out.println("One or more rooms not found. Check the room numbers.");
            }
        } else {
            System.out.println("Data already exists in the database.");
        }
    }
}
