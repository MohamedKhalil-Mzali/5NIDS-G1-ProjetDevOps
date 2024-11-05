package tn.esprit.spring.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.services.ICourseServices;

public class CourseRestControllerTest {

    @Mock
    private ICourseServices courseServices;

    @InjectMocks
    private CourseRestController courseRestController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(courseRestController).build();
    }

    @Test
    void testAddCourse() throws Exception {
        Course course = new Course();
        // Configurez l'objet Course ici (ex. setNumCourse, setLevel, etc.)

        when(courseServices.addCourse(any(Course.class))).thenReturn(course);

        mockMvc.perform(post("/course/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(course)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllCourses() throws Exception {
        Course course1 = new Course();
        Course course2 = new Course();
        List<Course> courses = Arrays.asList(course1, course2);

        when(courseServices.retrieveAllCourses()).thenReturn(courses);

        mockMvc.perform(get("/course/all"))
                .andExpect(status().isOk());
    }
}
