package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.repositories.ICourseRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CourseServicesImplTest {

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private CourseServicesImpl courseServices;

    private Course course;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialize mocks

        course = new Course();
        course.setNumCourse(1L);
        course.setLevel(2);
        course.setPrice(300F);
        // Set other properties as needed
    }

    // Tests for retrieveAllCourses()

    @Test
    void testRetrieveAllCourses_successful() {
        // Arrange
        when(courseRepository.findAll()).thenReturn(Arrays.asList(course));

        // Act
        List<Course> courses = courseServices.retrieveAllCourses();

        // Assert
        assertEquals(1, courses.size());
        assertEquals(course.getNumCourse(), courses.get(0).getNumCourse());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveAllCourses_empty() {
        // Arrange
        when(courseRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Course> courses = courseServices.retrieveAllCourses();

        // Assert
        assertTrue(courses.isEmpty());
        verify(courseRepository, times(1)).findAll();
    }

    // Tests for addCourse()

    @Test
    void testAddCourse_successful() {
        // Arrange
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // Act
        Course savedCourse = courseServices.addCourse(course);

        // Assert
        assertNotNull(savedCourse);
        assertEquals(course.getNumCourse(), savedCourse.getNumCourse());
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void testAddCourse_nullCourse() {
        // Arrange
        when(courseRepository.save(null)).thenThrow(IllegalArgumentException.class);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> courseServices.addCourse(null));
        verify(courseRepository, times(1)).save(null);
    }

    // Tests for updateCourse()

    @Test
    void testUpdateCourse_successful() {
        // Arrange
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // Act
        Course updatedCourse = courseServices.updateCourse(course);

        // Assert
        assertEquals(course.getNumCourse(), updatedCourse.getNumCourse());
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void testUpdateCourse_nullCourse() {
        // Arrange
        when(courseRepository.save(null)).thenThrow(IllegalArgumentException.class);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> courseServices.updateCourse(null));
        verify(courseRepository, times(1)).save(null);
    }

    // Tests for retrieveCourse()

    @Test
    void testRetrieveCourse_found() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        // Act
        Course foundCourse = courseServices.retrieveCourse(1L);

        // Assert
        assertNotNull(foundCourse);
        assertEquals(course.getNumCourse(), foundCourse.getNumCourse());
        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    void testRetrieveCourse_notFound() {
        // Arrange
        when(courseRepository.findById(2L)).thenReturn(Optional.empty());

        // Act
        Course foundCourse = courseServices.retrieveCourse(2L);

        // Assert
        assertNull(foundCourse);
        verify(courseRepository, times(1)).findById(2L);
    }
}
