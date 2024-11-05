package tn.esprit.spring.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tn.esprit.spring.entities.Course;
import tn.esprit.spring.repositories.ICourseRepository;

public class CourseServicesImplTest {

    @InjectMocks
    private CourseServicesImpl courseServices;

    @Mock
    private ICourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRetrieveAllCourses() {
        Course course1 = new Course();
        Course course2 = new Course();
        List<Course> courses = Arrays.asList(course1, course2);

        when(courseRepository.findAll()).thenReturn(courses);

        List<Course> result = courseServices.retrieveAllCourses();

        verify(courseRepository, times(1)).findAll();
        assertEquals(2, result.size());
    }

    @Test
    void testAddCourse() {
        Course course = new Course();
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        Course createdCourse = courseServices.addCourse(course);

        verify(courseRepository, times(1)).save(course);
        assertNotNull(createdCourse);
    }

    @Test
    void testUpdateCourse() {
        Course course = new Course();
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        Course updatedCourse = courseServices.updateCourse(course);

        verify(courseRepository, times(1)).save(course);
        assertNotNull(updatedCourse);
    }

    @Test
    void testRetrieveCourse() {
        Course course = new Course();
        course.setNumCourse(1L);
        when(courseRepository.findById(anyLong())).thenReturn(java.util.Optional.of(course));

        Course retrievedCourse = courseServices.retrieveCourse(1L);

        verify(courseRepository, times(1)).findById(1L);
        assertEquals(course.getNumCourse(), retrievedCourse.getNumCourse());
    }

    @Test
    void testRetrieveCourseNotFound() {
        when(courseRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        Course retrievedCourse = courseServices.retrieveCourse(1L);

        verify(courseRepository, times(1)).findById(1L);
        assertNull(retrievedCourse);
    }
}
