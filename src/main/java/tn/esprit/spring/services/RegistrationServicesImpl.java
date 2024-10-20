package tn.esprit.spring.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.repositories.IRegistrationRepository;
import tn.esprit.spring.repositories.ISkierRepository;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class RegistrationServicesImpl implements IRegistrationServices {

    private final IRegistrationRepository registrationRepository;
    private final ISkierRepository skierRepository;
    private final ICourseRepository courseRepository;

    @Override
    public Registration addRegistrationAndAssignToSkier(Registration registration, Long numSkier) {
        Skier skier = findSkierById(numSkier);
        registration.setSkier(skier);
        return registrationRepository.save(registration);
    }

    @Override
    public Registration assignRegistrationToCourse(Long numRegistration, Long numCourse) {
        Registration registration = findRegistrationById(numRegistration);
        Course course = findCourseById(numCourse);
        registration.setCourse(course);
        return registrationRepository.save(registration);
    }

    @Transactional
    @Override
    public Registration addRegistrationAndAssignToSkierAndCourse(Registration registration, Long numSkier, Long numCourse) {
        Skier skier = findSkierById(numSkier);
        Course course = findCourseById(numCourse);

        if (skier == null || course == null) {
            log.error("Skier or Course not found");
            return null;
        }

        if (isAlreadyRegistered(registration, skier, course)) {
            return null;
        }

        return processRegistration(registration, skier, course);
    }

    private Skier findSkierById(Long numSkier) {
        return skierRepository.findById(numSkier)
                .orElseThrow(() -> new EntityNotFoundException("Skier not found"));
    }

    private Course findCourseById(Long numCourse) {
        return courseRepository.findById(numCourse)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
    }

    private Registration findRegistrationById(Long numRegistration) {
        return registrationRepository.findById(numRegistration)
                .orElseThrow(() -> new EntityNotFoundException("Registration not found"));
    }

    private boolean isAlreadyRegistered(Registration registration, Skier skier, Course course) {
        if (registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(
                registration.getNumWeek(), skier.getNumSkier(), course.getNumCourse()) >= 1) {
            log.info("Already registered for course of the week: " + registration.getNumWeek());
            return true;
        }
        return false;
    }

    private Registration processRegistration(Registration registration, Skier skier, Course course) {
        int ageSkier = calculateAge(skier);
        log.info("Age: " + ageSkier);

        switch (course.getTypeCourse()) {
            case INDIVIDUAL:
                return assignRegistration(registration, skier, course);
            case COLLECTIVE_CHILDREN:
                return handleCollectiveChildrenRegistration(registration, skier, course, ageSkier);
            default:
                return handleCollectiveAdultRegistration(registration, skier, course, ageSkier);
        }
    }

    private int calculateAge(Skier skier) {
        return Period.between(skier.getDateOfBirth(), LocalDate.now()).getYears();
    }

    private Registration assignRegistration(Registration registration, Skier skier, Course course) {
        registration.setSkier(skier);
        registration.setCourse(course);
        return registrationRepository.save(registration);
    }

    private Registration handleCollectiveChildrenRegistration(Registration registration, Skier skier, Course course, int ageSkier) {
        if (ageSkier < 16) {
            log.info("Ok CHILD !");
            if (registrationRepository.countByCourseAndNumWeek(course, registration.getNumWeek()) < 6) {
                log.info("Course successfully added !");
                return assignRegistration(registration, skier, course);
            } else {
                log.info("Full Course! Please choose another week to register!");
            }
        } else {
            log.info("Age restriction: try registering for a Collective Adult Course...");
        }
        return registration;
    }

    private Registration handleCollectiveAdultRegistration(Registration registration, Skier skier, Course course, int ageSkier) {
        if (ageSkier >= 16) {
            log.info("Ok ADULT !");
            if (registrationRepository.countByCourseAndNumWeek(course, registration.getNumWeek()) < 6) {
                log.info("Course successfully added !");
                return assignRegistration(registration, skier, course);
            } else {
                log.info("Full Course! Please choose another week to register!");
            }
        } else {
            log.info("Age restriction: try registering for a Collective Child Course...");
        }
        return registration;
    }

    @Override
    public List<Integer> numWeeksCourseOfInstructorBySupport(Long numInstructor, Support support) {
        return registrationRepository.numWeeksCourseOfInstructorBySupport(numInstructor, support);
    }
}

