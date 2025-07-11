package org.studyeasy.SpringStarter.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.studyeasy.SpringStarter.Model.Room;
import org.studyeasy.SpringStarter.Model.Student;
import org.studyeasy.SpringStarter.Repository.RoomRepository;
import org.studyeasy.SpringStarter.Services.RoomService;
import org.studyeasy.SpringStarter.Services.StudentService;

@Controller
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomService roomService;

    @Autowired
    private StudentService studentService;

    @GetMapping("/rooms_management")
    public String roomsManagement(Model model) {
        // Add all rooms to the model if needed
        model.addAttribute("rooms", roomService.findAll());
        return "rooms_management";
    }

    @GetMapping("/add-room")
    public String roomsAddition(Model model) {
        model.addAttribute("room", new Room());
        return "add_room";
    }

    @PostMapping("/room/add")
    public String addRoom(@ModelAttribute("room") Room room, @RequestParam("capacity") Integer capacity, Model model) {
        room.setCurrCapacity(capacity);
        roomRepository.save(room);
        model.addAttribute("message","Room Added Successfully");
        return "rooms_management";
    }

    @GetMapping("/view-room")
    public String viewRoom(Model model) {
        // This will be handled by a specific view-room functionality if needed
        return "view_room";
    }

    @PostMapping("/search-room")
    public String searchRoom(@RequestParam("roomNo") String roomNo, @RequestParam("hostel") String hostel, Model model) {
        Optional<Room> room = roomService.getByRoomNoandHostel(roomNo,hostel);
        if (room.isPresent()) {
            List<Student> stdList = studentService.getStudentsByRoomNoAndHostel(roomNo, hostel);
            model.addAttribute("students", stdList);
            model.addAttribute("room", room.get());
            return "room_details";
        } 
        else {
            model.addAttribute("error", "Room not found");
            return "view_room";
        }
    }

    @GetMapping("/find-empty-rooms")
    public String viewEmptyRooms( @RequestParam("hostel") String hostel, Model model) {
        List<Room> rooms = roomRepository.findAll();
        List<Room> avaRooms = new ArrayList<>();

        for (Room room : rooms) {
            if (room.getCurrCapacity() > 0 && room.getHostel().equals(hostel)) {
                avaRooms.add(room);
            }
        }

        model.addAttribute("avaRooms", avaRooms);
        return "view_empty_rooms";
    }

    @GetMapping("/search-edit-room")
    public String showUpdateRoomForm(Model model) {
        return "search_update_room";
    }

    @PostMapping("/edit-room")
    public String editRoom(@RequestParam("roomNo") String roomNo, @RequestParam("hostel") String hostel, Model model){
        Optional<Room> optionalRoom = roomService.getByRoomNoandHostel(roomNo, hostel);
        if(optionalRoom.isPresent()){
            Room room =optionalRoom.get();
            model.addAttribute("room", room);
            return "edit_room";
        }
        else{
            model.addAttribute("error", "Room not found");
            return "search_update_room";
        }
    }

    // Method to handle form submission and update room information
    @PostMapping("/update-room")
    public String updateRoom(@RequestParam("roomNo") String roomNo,
                             @RequestParam("hostel") String hostel,
                             @RequestParam("capacity") Integer capacity,
                             @RequestParam("currCapacity") Integer currCapacity,
                             @RequestParam("indRent") Integer indRent,
                             Model model) {
        Optional<Room> optionalRoom = roomService.getByRoomNoandHostel(roomNo,hostel);
        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();
            room.setIndRent(indRent);
            room.setCapacity(capacity);
            room.setCurrCapacity(currCapacity);
            roomRepository.save(room);
            return "redirect:/rooms_management"; // Redirect to the rooms management page or any other page
        } else {
            model.addAttribute("error", "Room not found");
            return "edit_room"; // Redirect back to the edit room page with an error
        }
    }


}
