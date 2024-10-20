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

    private IRegistrationRepository registrationRepository;
    private ISkierRepository skierRepository;
    private ICourseRepository courseRepository;

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
    public Registration addRegistrationAndAssignToSkierAndCourse(Registration registration, Long numSkieur, Long numCours) {
        Skier skier = findSkierById(numSkieur);
        Course course = findCourseById(numCours);

        if (skier == null || course == null) {
            return null;
        }

        if (isAlreadyRegistered(registration, skier, course)) {
            return null;
        }

        return processRegistration(registration, skier, course);
    }

    private Skier findSkierById(Long numSkieur) {
        return skierRepository.findById(numSkieur)
                .orElseThrow(() -> new EntityNotFoundException("Skier not found"));
    }

    private Course findCourseById(Long numCours) {
        return courseRepository.findById(numCours)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
    }

    private Registration findRegistrationById(Long numRegistration) {
        return registrationRepository.findById(numRegistration)
                .orElseThrow(() -> new EntityNotFoundException("Registration not found"));
    }

    private boolean isAlreadyRegistered(Registration registration, Skier skier, Course course) {
        if (registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(
                registration.getNumWeek(), skier.getNumSkier(), course.getNumCourse()) >= 1) {
            log.info("Sorry, you're already registered for this course of the week: " + registration.getNumWeek());
            return true;
        }
        return false;
    }

    private Registration processRegistration(Registration registration, Skier skier, Course course) {
        int ageSkieur = calculateAge(skier);
        log.info("Age " + ageSkieur);

        switch (course.getTypeCourse()) {
            case INDIVIDUAL:
                return assignRegistration(registration, skier, course);
            case COLLECTIVE_CHILDREN:
                return handleCollectiveChildrenRegistration(registration, skier, course, ageSkieur);
            default:
                return handleCollectiveAdultRegistration(registration, skier, course, ageSkieur);
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

    private Registration handleCollectiveChildrenRegistration(Registration registration, Skier skier, Course course, int ageSkieur) {
        if (ageSkieur < 16) {
            log.info("Ok CHILD !");
            if (registrationRepository.countByCourseAndNumWeek(course, registration.getNumWeek()) < 6) {
                log.info("Course successfully added !");
                return assignRegistration(registration, skier, course);
            } else {
                log.info("Full Course ! Please choose another week to register !");
            }
        } else {
            log.info("Sorry, your age doesn't allow you to register for this course ! Try to register for a Collective Adult Course...");
        }
        return registration;
    }

    private Registration handleCollectiveAdultRegistration(Registration registration, Skier skier, Course course, int ageSkieur) {
        if (ageSkieur >= 16) {
            log.info("Ok ADULT !");
            if (registrationRepository.countByCourseAndNumWeek(course, registration.getNumWeek()) < 6) {
                log.info("Course successfully added !");
                return assignRegistration(registration, skier, course);
            } else {
                log.info("Full Course ! Please choose another week to register !");
            }
        } else {
            log.info("Sorry, your age doesn't allow you to register for this course ! Try to register for a Collective Child Course...");
        }
        return registration;
    }

    @Override
    public List<Integer> numWeeksCourseOfInstructorBySupport(Long numInstructor, Support support) {
        return registrationRepository.numWeeksCourseOfInstructorBySupport(numInstructor, support);
    }
}
