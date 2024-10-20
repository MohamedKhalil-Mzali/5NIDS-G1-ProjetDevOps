package tn.esprit.spring.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Skier;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.repositories.ISkierRepository;
import tn.esprit.spring.repositories.ISubscriptionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Service
public class SubscriptionServicesImpl implements ISubscriptionServices {

    private final ISubscriptionRepository subscriptionRepository;
    private final ISkierRepository skierRepository;

    @Override
    public Subscription addSubscription(Subscription subscription) {
        subscription.setEndDate(calculateEndDate(subscription));
        return subscriptionRepository.save(subscription);
    }

    private LocalDate calculateEndDate(Subscription subscription) {
        switch (subscription.getTypeSub()) {
            case ANNUAL:
                return subscription.getStartDate().plusYears(1);
            case SEMESTRIEL:
                return subscription.getStartDate().plusMonths(6);
            case MONTHLY:
                return subscription.getStartDate().plusMonths(1);
            default:
                throw new IllegalArgumentException("Invalid subscription type");
        }
    }

    @Override
    public Subscription updateSubscription(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    @Override
    public Subscription retrieveSubscriptionById(Long numSubscription) {
        return subscriptionRepository.findById(numSubscription)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found"));
    }

    @Override
    public Set<Subscription> getSubscriptionByType(TypeSubscription type) {
        return subscriptionRepository.findByTypeSubOrderByStartDateAsc(type);
    }

    @Override
    public List<Subscription> retrieveSubscriptionsByDates(LocalDate startDate, LocalDate endDate) {
        return subscriptionRepository.getSubscriptionsByStartDateBetween(startDate, endDate);
    }

    @Override
    @Scheduled(cron = "*/30 * * * * *")
    public void logSubscriptions() {
        subscriptionRepository.findDistinctOrderByEndDateAsc().forEach(sub -> {
            Optional<Skier> aSkierOpt = skierRepository.findBySubscription(sub);
            aSkierOpt.ifPresent(aSkier -> 
                log.info("{} | {} | {} {}", sub.getNumSub(), sub.getEndDate(), aSkier.getFirstName(), aSkier.getLastName())
            );
        });
    }

    @Scheduled(cron = "0 0 * * * *") // Adjusted to run hourly for demonstration purposes
    public void showMonthlyRecurringRevenue() {
        Float monthlyRevenue = Optional.ofNullable(subscriptionRepository.recurringRevenueByTypeSubEquals(TypeSubscription.MONTHLY)).orElse(0f);
        Float semestrielRevenue = Optional.ofNullable(subscriptionRepository.recurringRevenueByTypeSubEquals(TypeSubscription.SEMESTRIEL)).orElse(0f) / 6;
        Float annualRevenue = Optional.ofNullable(subscriptionRepository.recurringRevenueByTypeSubEquals(TypeSubscription.ANNUAL)).orElse(0f) / 12;

        Float totalRevenue = monthlyRevenue + semestrielRevenue + annualRevenue;
        log.info("Monthly Revenue = {}", totalRevenue);
    }
}

