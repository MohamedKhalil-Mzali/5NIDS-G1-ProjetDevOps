package tn.esprit.spring.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE)
@Entity
public class Instructor implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long numInstructor;
    String firstName;
    String lastName;
    LocalDate dateOfHire;

    @OneToMany(mappedBy = "instructor") // Si Course a une relation vers Instructor
    transient Set<Course> courses; // Ou assure-toi que Course est sérialisable
}

