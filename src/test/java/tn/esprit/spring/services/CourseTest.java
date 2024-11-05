package tn.esprit.spring.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CourseTest {

    @Test
    public void testCourseCreation() {
        Course course = new Course();
        course.setNumCourse(1L);
        course.setLevel(1);
        course.setTypeCourse(TypeCourse.COLLECTIVE_CHILDREN);
        course.setPrice(150.0f);
        course.setTimeSlot(2);

        assertNotNull(course);
        assertEquals(1L, course.getNumCourse());
        assertEquals(1, course.getLevel());
        assertEquals(TypeCourse.COLLECTIVE_CHILDREN, course.getTypeCourse());
        assertEquals(150.0f, course.getPrice());
        assertEquals(2, course.getTimeSlot());
    }
}
