package org.studyeasy.SpringStarter.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.studyeasy.SpringStarter.Model.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> getByRoomNoAndHostel(String roomNo, String hostel);
}