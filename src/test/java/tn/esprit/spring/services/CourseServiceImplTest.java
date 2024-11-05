package tn.esprit.spring.services;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.repositories.ICourseRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServicesImplTest {

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private CourseServicesImpl courseServices;

    @Test
    public void testRetrieveAllCourses() {
        // Arrange
        Course course1 = new Course();
        Course course2 = new Course();
        when(courseRepository.findAll()).thenReturn(List.of(course1, course2));

        // Act
        List<Course> courses = courseServices.retrieveAllCourses();

        // Assert
        assertNotNull(courses);
        assertEquals(2, courses.size());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    public void testAddCourse() {
        // Arrange
        Course course = new Course();
        when(courseRepository.save(course)).thenReturn(course);

        // Act
        Course result = courseServices.addCourse(course);

        // Assert
        assertNotNull(result);
        assertEquals(course, result);
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    public void testUpdateCourse() {
        // Arrange
        Course course = new Course();
        when(courseRepository.save(course)).thenReturn(course);

        // Act
        Course result = courseServices.updateCourse(course);

        // Assert
        assertNotNull(result);
        assertEquals(course, result);
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    public void testRetrieveCourse() {
        // Arrange
        Long courseId = 1L;
        Course course = new Course();
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Act
        Course result = courseServices.retrieveCourse(courseId);

        // Assert
        assertNotNull(result);
        assertEquals(course, result);
        verify(courseRepository, times(1)).findById(courseId);
    }

    @Test
    public void testRetrieveCourseNotFound() {
        // Arrange
        Long courseId = 1L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act
        Course result = courseServices.retrieveCourse(courseId);

        // Assert
        assertNull(result);
        verify(courseRepository, times(1)).findById(courseId);
    }
}
