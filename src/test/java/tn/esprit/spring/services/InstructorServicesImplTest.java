package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.entities.Instructor;
import tn.esprit.spring.repositories.IInstructorRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InstructorServicesImplTest {

    @InjectMocks
    private InstructorServicesImpl instructorServices; // Assurez-vous d'utiliser l'implémentation correcte ici

    @Mock
    private IInstructorRepository instructorRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddInstructor() {
        Instructor instructor = new Instructor();
        instructor.setFirstName("John");
        instructor.setLastName("Doe");

        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        Instructor result = instructorServices.addInstructor(instructor);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(instructorRepository, times(1)).save(instructor);
    }

    @Test
    void testRetrieveAllInstructors() {
        when(instructorRepository.findAll()).thenReturn(Collections.singletonList(new Instructor()));

        List<Instructor> instructors = instructorServices.retrieveAllInstructors();

        assertEquals(1, instructors.size());
        verify(instructorRepository, times(1)).findAll();
    }
    
   
}
