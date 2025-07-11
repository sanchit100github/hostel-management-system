package org.studyeasy.SpringStarter.Model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private Boolean active;

    private Integer deposit;

    private String firstName;
    private String middleName;
    private String lastName;
    private String contact;
    private String pName;
    private String pContact;
    private String whatsapp;
    private String email;
    private String adhar;
    private String pancard; 
    private String work;
    private String workPlace;
    private String workPosition;
    private String photoPath;


    private LocalDate birthDate;
    private LocalDate joiningDate;
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Room room;

    @OneToMany(mappedBy = "student")
    private List<Payment> payment;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(columnDefinition = "TEXT")
    private String guardianAddress;
    private String guardianName;
    private String guardianContact;


}
