package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.Instructor;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.repositories.IInstructorRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InstructorServicesImplTest {

    @Mock
    private IInstructorRepository instructorRepository;

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private InstructorServicesImpl instructorServices;

    private Instructor instructor;
    private Course course;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        instructor = new Instructor();
        instructor.setNumInstructor(1L);
        instructor.setFirstName("John");
        instructor.setLastName("Doe");

        course = new Course();
        course.setNumCourse(1L);
        course.setLevel(1);
    }

    // Tests for addInstructor()

    @Test
    void testAddInstructor_successful() {
        // Arrange
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        // Act
        Instructor savedInstructor = instructorServices.addInstructor(instructor);

        // Assert
        assertNotNull(savedInstructor);
        assertEquals(instructor.getNumInstructor(), savedInstructor.getNumInstructor());
        verify(instructorRepository, times(1)).save(instructor);
    }

    @Test
    void testAddInstructor_nullInstructor() {
        // Arrange
        when(instructorRepository.save(null)).thenThrow(IllegalArgumentException.class);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> instructorServices.addInstructor(null));
        verify(instructorRepository, times(1)).save(null);
    }

    // Tests for retrieveAllInstructors()

    @Test
    void testRetrieveAllInstructors_successful() {
        // Arrange
        List<Instructor> instructorList = Arrays.asList(instructor);
        when(instructorRepository.findAll()).thenReturn(instructorList);

        // Act
        List<Instructor> instructors = instructorServices.retrieveAllInstructors();

        // Assert
        assertEquals(1, instructors.size());
        assertEquals(instructor.getNumInstructor(), instructors.get(0).getNumInstructor());
        verify(instructorRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveAllInstructors_emptyList() {
        // Arrange
        when(instructorRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Instructor> instructors = instructorServices.retrieveAllInstructors();

        // Assert
        assertTrue(instructors.isEmpty());
        verify(instructorRepository, times(1)).findAll();
    }

    // Tests for updateInstructor()

    @Test
    void testUpdateInstructor_successful() {
        // Arrange
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        // Act
        Instructor updatedInstructor = instructorServices.updateInstructor(instructor);

        // Assert
        assertEquals(instructor.getNumInstructor(), updatedInstructor.getNumInstructor());
        verify(instructorRepository, times(1)).save(instructor);
    }

    @Test
    void testUpdateInstructor_nullInstructor() {
        // Arrange
        when(instructorRepository.save(null)).thenThrow(IllegalArgumentException.class);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> instructorServices.updateInstructor(null));
        verify(instructorRepository, times(1)).save(null);
    }

    // Tests for retrieveInstructor()

    @Test
    void testRetrieveInstructor_found() {
        // Arrange
        when(instructorRepository.findById(1L)).thenReturn(Optional.of(instructor));

        // Act
        Instructor foundInstructor = instructorServices.retrieveInstructor(1L);

        // Assert
        assertNotNull(foundInstructor);
        assertEquals(instructor.getNumInstructor(), foundInstructor.getNumInstructor());
        verify(instructorRepository, times(1)).findById(1L);
    }

    @Test
    void testRetrieveInstructor_notFound() {
        // Arrange
        when(instructorRepository.findById(2L)).thenReturn(Optional.empty());

        // Act
        Instructor foundInstructor = instructorServices.retrieveInstructor(2L);

        // Assert
        assertNull(foundInstructor);
        verify(instructorRepository, times(1)).findById(2L);
    }

    // Tests for addInstructorAndAssignToCourse()

    @Test
    void testAddInstructorAndAssignToCourse_successful() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        // Act
        Instructor savedInstructor = instructorServices.addInstructorAndAssignToCourse(instructor, 1L);

        // Assert
        assertNotNull(savedInstructor);
        assertEquals(instructor.getNumInstructor(), savedInstructor.getNumInstructor());
        assertTrue(savedInstructor.getCourses().contains(course));
        verify(instructorRepository, times(1)).save(instructor);
        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    void testAddInstructorAndAssignToCourse_courseNotFound() {
        // Arrange
        when(courseRepository.findById(2L)).thenReturn(Optional.empty());
	when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);
										   
        // Act
        Instructor savedInstructor = instructorServices.addInstructorAndAssignToCourse(instructor, 2L);

        // Assert
        assertNotNull(savedInstructor);
        assertTrue(instructor.getCourses().isEmpty());
        verify(instructorRepository, times(1)).save(instructor);
        verify(courseRepository, times(1)).findById(2L);
    }
}
