package tn.esprit.spring.controllers;

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
import tn.esprit.spring.services.ICourseServices;

public class CourseRestControllerTest {

    @InjectMocks
    private CourseRestController courseRestController;

    @Mock
    private ICourseServices courseServices;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddCourse() {
        Course course = new Course();
        course.setNumCourse(1L);
        when(courseServices.addCourse(any(Course.class))).thenReturn(course);

        Course createdCourse = courseRestController.addCourse(course);

        verify(courseServices, times(1)).addCourse(course);
        assert createdCourse.getNumCourse() == 1L;
    }

    @Test
    void testGetAllCourses() {
        Course course1 = new Course();
        Course course2 = new Course();
        List<Course> courses = Arrays.asList(course1, course2);

        when(courseServices.retrieveAllCourses()).thenReturn(courses);

        List<Course> result = courseRestController.getAllCourses();

        verify(courseServices, times(1)).retrieveAllCourses();
        assert result.size() == 2;
    }

    @Test
    void testUpdateCourse() {
        Course course = new Course();
        course.setNumCourse(1L);
        when(courseServices.updateCourse(any(Course.class))).thenReturn(course);

        Course updatedCourse = courseRestController.updateCourse(course);

        verify(courseServices, times(1)).updateCourse(course);
        assert updatedCourse.getNumCourse() == 1L;
    }

    @Test
    void testGetById() {
        Course course = new Course();
        course.setNumCourse(1L);
        when(courseServices.retrieveCourse(anyLong())).thenReturn(course);

        Course retrievedCourse = courseRestController.getById(1L);

        verify(courseServices, times(1)).retrieveCourse(1L);
        assert retrievedCourse.getNumCourse() == 1L;
    }
}
