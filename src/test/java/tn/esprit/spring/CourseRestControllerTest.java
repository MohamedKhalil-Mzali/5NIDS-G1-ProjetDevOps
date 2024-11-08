package tn.esprit.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.spring.controllers.CourseRestController;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.services.ICourseServices;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

@WebMvcTest(CourseRestController.class)
public class CourseRestControllerTest {

    @Autowired
    private MockMvc mockMvc;  // MockMvc to simulate HTTP requests

    @Mock
    private ICourseServices courseServices;  // Mock the service layer

    @InjectMocks
    private CourseRestController courseRestController;  // Inject mock services into the controller

    @Test
    void testAddCourse() throws Exception {
        // Arrange: Create an empty Course object (no properties set)
        Course course = new Course();

        // Mock the service call to add a course (returning an empty course object)
        when(courseServices.addCourse(any(Course.class))).thenReturn(course);

        // Act & Assert: Perform POST request and verify HTTP status
        mockMvc.perform(post("/course/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(course)))  // Send empty course in request body
               .andExpect(status().isOk());  // Expect HTTP 200 OK

        // Verify that the service method was called exactly once
        verify(courseServices, times(1)).addCourse(any(Course.class));
    }

    @Test
    void testGetAllCourses() throws Exception {
        // Arrange: Mock the service method to return an empty list of courses
        when(courseServices.retrieveAllCourses()).thenReturn(Collections.emptyList());

        // Act & Assert: Perform GET request and verify HTTP status
        mockMvc.perform(get("/course/all"))
               .andExpect(status().isOk());  // Expect HTTP 200 OK

        // Verify that the service method was called exactly once
        verify(courseServices, times(1)).retrieveAllCourses();
    }
}
