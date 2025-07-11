package org.studyeasy.SpringStarter.Services;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.studyeasy.SpringStarter.Model.Room;
import org.studyeasy.SpringStarter.Repository.RoomRepository;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    public Optional<Room> getByRoomNo(String roomNo, String hostel) {
        return roomRepository.getByRoomNoAndHostel(roomNo, hostel);
    }

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public List<Room> findAllByHostel(String hostel) {
        List<Room> roomOpt = roomRepository.findAll();
        List<Room> roomList = new ArrayList<>();
        for(Room temp : roomOpt) {
            if(temp.getHostel().equals(hostel)) {
                roomList.add(temp);
            }
        }
        return roomList;
    }

    public Room save(Room room) {
        return roomRepository.save(room);
    }

    public void delete(Room room) {
        roomRepository.delete(room);
    }

    public void increaseCapacity(String roomNo, String hostel) {
        Optional<Room> optionalRoom = roomRepository.getByRoomNoAndHostel(roomNo,hostel);
        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();
            room.setCurrCapacity(room.getCurrCapacity() + 1);
            roomRepository.save(room);
        }
    }

    public void decreaseCapacity(String roomNo, String hostel) {
        Optional<Room> optionalRoom = roomRepository.getByRoomNoAndHostel(roomNo,hostel);
        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();
            if (room.getCurrCapacity() > 0) {
                room.setCurrCapacity(room.getCurrCapacity() - 1);
                roomRepository.save(room);
            }
        }
    }

    public Optional<Room> getByRoomNoandHostel(String roomNo, String hostel) {
        return roomRepository.getByRoomNoAndHostel(roomNo,hostel);
    }
}
