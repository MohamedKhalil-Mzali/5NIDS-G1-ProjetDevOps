package tn.esprit.spring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.spring.controllers.CourseRestController;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.TypeCourse; // Assurez-vous d'importer l'énumération correcte
import tn.esprit.spring.entities.Support; // Assurez-vous d'importer l'énumération correcte

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(exclude = {SecurityAutoConfiguration.class})
@AutoConfigureMockMvc // Permet d'utiliser MockMvc pour tester les requêtes HTTP
class GestionStationSkiApplicationTests {

    @Autowired
    private CourseRestController courseRestController; // Injectez le contrôleur

    @Autowired
    private MockMvc mockMvc; // MockMvc pour tester les requêtes

    @Test
    void contextLoads() {
        // Vérifie que le contexte charge correctement
        assertThat(courseRestController).isNotNull(); // Assure que le contrôleur est injecté
    }

    @Test
    void testAddCourse() throws Exception {
        
        Course course = new Course();
        course.setLevel(1);
        course.setTypeCourse(TypeCourse.COLLECTIVE_CHILDREN);
        course.setSupport(Support.ONLINE);
        course.setPrice(100.0f);
        course.setTimeSlot(2);

        // Envoyer une requête POST pour ajouter un cours
        mockMvc.perform(post("/course/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"level\":1,\"typeCourse\":\"COLLECTIVE_CHILDREN\",\"support\":\"ONLINE\",\"price\":100.0,\"timeSlot\":2}"))
                .andExpect(status().isOk()); 
    }

  
}
