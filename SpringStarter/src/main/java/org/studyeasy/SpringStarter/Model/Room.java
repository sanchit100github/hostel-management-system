package org.studyeasy.SpringStarter.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String hostel;
    private String roomNo; 
    private Integer capacity;
    private Integer indRent;
    private Integer currCapacity; // Updated field name for consistency

    @OneToMany(mappedBy = "room")
    private List<Student> students;
}
