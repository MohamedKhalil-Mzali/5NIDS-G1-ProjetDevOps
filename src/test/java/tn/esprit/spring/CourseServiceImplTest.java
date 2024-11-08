package tn.esprit.spring.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.repositories.ICourseRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CourseServicesImplTest {

    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private CourseServicesImpl courseServices;

    private Course course;

    @BeforeEach
    public void setup() {
        course = new Course();
    }

    @Test
    void testRetrieveAllCourses() {
        when(courseRepository.findAll()).thenReturn(Collections.singletonList(course));

        var result = courseServices.retrieveAllCourses();

        assertNotNull(result);
        assertFalse(result.isEmpty());

        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void testAddCourse() {
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        Course result = courseServices.addCourse(course);

        assertNotNull(result);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void testUpdateCourse() {
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        Course result = courseServices.updateCourse(course);

        assertNotNull(result);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void testRetrieveCourse() {
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(course));
        Course result = courseServices.retrieveCourse(1L);

        assertNotNull(result);
        verify(courseRepository, times(1)).findById(anyLong());
    }

    @Test
    void testRetrieveCourseNotFound() {
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());

        Course result = courseServices.retrieveCourse(1L);

        assertNull(result);
        verify(courseRepository, times(1)).findById(anyLong());
    }
}
