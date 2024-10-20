package tn.esprit.spring.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.exceptions.EntityNotFoundException;
import tn.esprit.spring.repositories.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Service
public class SkierServicesImpl implements ISkierServices {

    private final ISkierRepository skierRepository;
    private final IPisteRepository pisteRepository;
    private final ICourseRepository courseRepository;
    private final IRegistrationRepository registrationRepository;
    private final ISubscriptionRepository subscriptionRepository;

    @Override
    public List<Skier> retrieveAllSkiers() {
        return skierRepository.findAll();
    }

    @Override
    public Skier addSkier(Skier skier) {
        if (skier.getSubscription() != null) {
            setSubscriptionEndDate(skier);
        }
        return skierRepository.save(skier);
    }

    private void setSubscriptionEndDate(Skier skier) {
        if (skier.getSubscription() != null) {
            switch (skier.getSubscription().getTypeSub()) {
                case ANNUAL:
                    skier.getSubscription().setEndDate(skier.getSubscription().getStartDate().plusYears(1));
                    break;
                case SEMESTRIEL:
                    skier.getSubscription().setEndDate(skier.getSubscription().getStartDate().plusMonths(6));
                    break;
                case MONTHLY:
                    skier.getSubscription().setEndDate(skier.getSubscription().getStartDate().plusMonths(1));
                    break;
            }
        }
    }

    @Override
    public Skier assignSkierToSubscription(Long numSkier, Long numSubscription) {
        Optional<Skier> skierOpt = skierRepository.findById(numSkier);
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findById(numSubscription);

        if (skierOpt.isPresent() && subscriptionOpt.isPresent()) {
            Skier skier = skierOpt.get();
            skier.setSubscription(subscriptionOpt.get());
            return skierRepository.save(skier);
        }
        throw new EntityNotFoundException("Skier or Subscription not found");
    }

    @Override
    public Skier addSkierAndAssignToCourse(Skier skier, Long numCourse) {
        Skier savedSkier = skierRepository.save(skier);
        Course course = courseRepository.findById(numCourse)
                          .orElseThrow(() -> new EntityNotFoundException("Course not found"));

        Set<Registration> registrations = Optional.ofNullable(savedSkier.getRegistrations())
                                                  .orElse(new HashSet<>());

        for (Registration registration : registrations) {
            registration.setSkier(savedSkier);
            registration.setCourse(course);
            registrationRepository.save(registration);
        }
        return savedSkier;
    }

    @Override
    public void removeSkier(Long numSkier) {
        skierRepository.deleteById(numSkier);
    }

    @Override
    public Skier retrieveSkier(Long numSkier) {
        return skierRepository.findById(numSkier)
                .orElseThrow(() -> new EntityNotFoundException("Skier not found"));
    }

    @Override
    public Skier assignSkierToPiste(Long numSkier, Long numPiste) {
        Optional<Skier> skierOpt = skierRepository.findById(numSkier);
        Optional<Piste> pisteOpt = pisteRepository.findById(numPiste);

        if (skierOpt.isPresent() && pisteOpt.isPresent()) {
            Skier skier = skierOpt.get();
            Piste piste = pisteOpt.get();
            Set<Piste> pistes = Optional.ofNullable(skier.getPistes()).orElse(new HashSet<>());
            pistes.add(piste);
            skier.setPistes(pistes);
            return skierRepository.save(skier);
        }
        throw new EntityNotFoundException("Skier or Piste not found");
    }

    @Override
    public List<Skier> retrieveSkiersBySubscriptionType(TypeSubscription typeSubscription) {
        return skierRepository.findBySubscription_TypeSub(typeSubscription);
    }
}
