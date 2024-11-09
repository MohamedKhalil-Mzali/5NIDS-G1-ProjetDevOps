package tn.esprit.spring.services;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.entities.TypeCourse;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.Registration;
import tn.esprit.spring.entities.Skier;
import tn.esprit.spring.entities.Support;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.repositories.IRegistrationRepository;
import tn.esprit.spring.repositories.ISkierRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationServicesImplTest {

    @Mock
    private IRegistrationRepository registrationRepository;

    @Mock
    private ISkierRepository skierRepository;

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private RegistrationServicesImpl registrationServices;

    private Registration registration;
    private Skier skier;
    private Course course;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        registration = new Registration();
        registration.setNumRegistration(1L);
        registration.setNumWeek(5);

        skier = new Skier();
        skier.setNumSkier(1L);

        course = new Course();
        course.setNumCourse(1L);
    }

    // Tests for addRegistrationAndAssignToSkier()

    @Test
    void testAddRegistrationAndAssignToSkier_successful() {
        // Arrange
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        // Act
        Registration savedRegistration = registrationServices.addRegistrationAndAssignToSkier(registration, 1L);

        // Assert
        assertNotNull(savedRegistration);
        assertEquals(1L, savedRegistration.getSkier().getNumSkier());
        verify(skierRepository, times(1)).findById(1L);
        verify(registrationRepository, times(1)).save(registration);
    }

    @Test
    void testAddRegistrationAndAssignToSkier_skierNotFound() {
        // Arrange
        when(skierRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Registration savedRegistration = registrationServices.addRegistrationAndAssignToSkier(registration, 1L);

        // Assert
        assertNull(savedRegistration);
        verify(skierRepository, times(1)).findById(1L);
        verify(registrationRepository, times(1)).save(any(Registration.class));
    }

    // Tests for assignRegistrationToCourse()

    @Test
    void testAssignRegistrationToCourse_successful() {
        // Arrange
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        // Act
        Registration updatedRegistration = registrationServices.assignRegistrationToCourse(1L, 1L);

        // Assert
        assertNotNull(updatedRegistration);
        assertEquals(1L, updatedRegistration.getCourse().getNumCourse());
        verify(registrationRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).findById(1L);
        verify(registrationRepository, times(1)).save(registration);
    }

    @Test
    void testAssignRegistrationToCourse_registrationNotFound() {
        // Arrange
        when(registrationRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Registration updatedRegistration = registrationServices.assignRegistrationToCourse(1L, 1L);

        // Assert
        assertNull(updatedRegistration);
        verify(registrationRepository, times(1)).findById(1L);
        verify(courseRepository, never()).findById(anyLong());
        verify(registrationRepository, never()).save(any(Registration.class));
    }

    // Tests for addRegistrationAndAssignToSkierAndCourse()

    @Test
    void testAddRegistrationAndAssignToSkierAndCourse_successful() {
        // Arrange
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(5, 1L, 1L)).thenReturn(0); // Returning Integer
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        skier.setDateOfBirth(LocalDate.of(1990, 1, 1));
        course.setTypeCourse(TypeCourse.INDIVIDUAL);

        // Act
        Registration savedRegistration = registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 1L);

        // Assert
        assertNotNull(savedRegistration);
        assertEquals(1L, savedRegistration.getSkier().getNumSkier());
        assertEquals(1L, savedRegistration.getCourse().getNumCourse());
        verify(skierRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).findById(1L);
        verify(registrationRepository, times(1)).save(registration);

        skier.setDateOfBirth(null);
        course.setTypeCourse(null);
    }

    @Test
    void testAddRegistrationAndAssignToSkierAndCourse_alreadyRegistered() {
        // Arrange
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(5, 1L, 1L)).thenReturn(1); // Returning Integer

        // Act
        Registration savedRegistration = registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 1L);

        // Assert
        assertNull(savedRegistration);
        verify(skierRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).findById(1L);
        verify(registrationRepository, never()).save(any(Registration.class));
    }

    // Tests for numWeeksCourseOfInstructorBySupport()

    @Test
    void testNumWeeksCourseOfInstructorBySupport_successful() {
        // Arrange
        List<Integer> weeks = Arrays.asList(1, 2, 3);
        when(registrationRepository.numWeeksCourseOfInstructorBySupport(1L, Support.SKI)).thenReturn(weeks);

        // Act
        List<Integer> result = registrationServices.numWeeksCourseOfInstructorBySupport(1L, Support.SKI);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(1, result.get(0));
        verify(registrationRepository, times(1)).numWeeksCourseOfInstructorBySupport(1L, Support.SKI);
    }

    @Test
    void testNumWeeksCourseOfInstructorBySupport_noWeeksFound() {
        // Arrange
        when(registrationRepository.numWeeksCourseOfInstructorBySupport(1L, Support.SKI)).thenReturn(Arrays.asList());

        // Act
        List<Integer> result = registrationServices.numWeeksCourseOfInstructorBySupport(1L, Support.SKI);

        // Assert
        assertTrue(result.isEmpty());
        verify(registrationRepository, times(1)).numWeeksCourseOfInstructorBySupport(1L, Support.SKI);
    }
}
