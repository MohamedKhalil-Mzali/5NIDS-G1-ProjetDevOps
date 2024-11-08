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
