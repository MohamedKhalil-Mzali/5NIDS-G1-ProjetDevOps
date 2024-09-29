package tn.esprit.spring.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.Instructor;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.repositories.IInstructorRepository;
import lombok.extern.sl4j.Sl4j;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
@Sl4j
public class InstructorServicesImpl implements IInstructorServices{

    private IInstructorRepository instructorRepository;
    private ICourseRepository courseRepository;

    @Override
    public Instructor addInstructor(Instructor instructor) {
        log.info("[+] Ajout");
        return instructorRepository.save(instructor);
    }

    @Override
    public List<Instructor> retrieveAllInstructors() {
        log.info("[+] Afficher tout");
        return instructorRepository.findAll();
    }

    @Override
    public Instructor updateInstructor(Instructor instructor) {
        log.info("[+] Mise a jour");
        return instructorRepository.save(instructor);
    }

    @Override
    public Instructor retrieveInstructor(Long numInstructor) {
        log.info("[+] Affichage par numInstructor");
        return instructorRepository.findById(numInstructor).orElse(null);
    }

    @Override
    public Instructor addInstructorAndAssignToCourse(Instructor instructor, Long numCourse) {
        Course course = courseRepository.findById(numCourse).orElse(null);
        Set<Course> courseSet = new HashSet<>();
        courseSet.add(course);
        instructor.setCourses(courseSet);
        return instructorRepository.save(instructor);
    }


}
