package tn.esprit.spring.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.TypeCourse;
import tn.esprit.spring.repositories.ICourseRepository;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class CourseServicesImpl implements  ICourseServices{

    private ICourseRepository courseRepository;

    @Override
    public List<Course> retrieveAllCourses() {
	log.info("[*] Affichage des cours ...");
        return courseRepository.findAll();
    }

    @Override
    public Course addCourse(Course course) {
	log.info("[+] Ajout du cours ...");
        return courseRepository.save(course);
    }

    @Override
    public Course updateCourse(Course course) {
	log.info("[+] Mise à jour du cours ...");
        return courseRepository.save(course);
    }

    @Override
    public Course retrieveCourse(Long numCourse) {
        log.info("[*] Affichage du cours ...");
	return courseRepository.findById(numCourse).orElse(null);
    }


}
