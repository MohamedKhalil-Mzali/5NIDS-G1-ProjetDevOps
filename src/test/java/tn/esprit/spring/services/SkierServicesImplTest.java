package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.*;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SkierServicesImplTest {

    @Mock
    private ISkierRepository skierRepository;

    @Mock
    private ISubscriptionRepository subscriptionRepository;

    @Mock
    private ICourseRepository courseRepository;

    @Mock
    private IRegistrationRepository registrationRepository;

    @Mock
    private IPisteRepository pisteRepository;

    @InjectMocks
    private SkierServicesImpl skierServices;

    private Skier skier;
    private Subscription subscription;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        skier = new Skier();
        skier.setNumSkier(1L);
        skier.setFirstName("John");
        skier.setLastName("Doe");

        subscription = new Subscription();
        subscription.setNumSub(1L);
        subscription.setStartDate(LocalDate.now());
        subscription.setTypeSub(TypeSubscription.ANNUAL);
    }

    // Test for retrieveAllSkiers method
    @Test
    public void testRetrieveAllSkiers() {
        List<Skier> skiers = Arrays.asList(skier);
        when(skierRepository.findAll()).thenReturn(skiers);

        List<Skier> retrievedSkiers = skierServices.retrieveAllSkiers();

        assertNotNull(retrievedSkiers);
        assertEquals(1, retrievedSkiers.size());

        verify(skierRepository, times(1)).findAll();
    }

    @Test
    public void testRetrieveAllSkiers_EmptyList() {
        when(skierRepository.findAll()).thenReturn(new ArrayList<>());

        List<Skier> retrievedSkiers = skierServices.retrieveAllSkiers();

        assertTrue(retrievedSkiers.isEmpty());

        verify(skierRepository, times(1)).findAll();
    }

    // Test for addSkier method
    @Test
    public void testAddSkier_AnnualSubscription() {
        skier.setSubscription(subscription);
        when(skierRepository.save(skier)).thenReturn(skier);

        Skier savedSkier = skierServices.addSkier(skier);

        assertNotNull(savedSkier);
        assertEquals(LocalDate.now().plusYears(1), savedSkier.getSubscription().getEndDate());

        verify(skierRepository, times(1)).save(skier);
    }

    @Test
    public void testAddSkier_SemestrielSubscription() {
        subscription.setTypeSub(TypeSubscription.SEMESTRIEL);
        skier.setSubscription(subscription);
        when(skierRepository.save(skier)).thenReturn(skier);

        Skier savedSkier = skierServices.addSkier(skier);

        assertNotNull(savedSkier);
        assertEquals(LocalDate.now().plusMonths(6), savedSkier.getSubscription().getEndDate());

        verify(skierRepository, times(1)).save(skier);
    }

    // Test for assignSkierToSubscription method
    @Test
    public void testAssignSkierToSubscription() {
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));
        when(skierRepository.save(skier)).thenReturn(skier);

        Skier assignedSkier = skierServices.assignSkierToSubscription(1L, 1L);

        assertNotNull(assignedSkier);
        assertEquals(subscription, assignedSkier.getSubscription());

        verify(skierRepository, times(1)).findById(1L);
        verify(subscriptionRepository, times(1)).findById(1L);
        verify(skierRepository, times(1)).save(skier);
    }

    @Test
    public void testAssignSkierToSubscription_SkierNotFound() {
        when(skierRepository.findById(1L)).thenReturn(Optional.empty());

        Skier assignedSkier = skierServices.assignSkierToSubscription(1L, 1L);

        assertNull(assignedSkier);
        verify(skierRepository, times(1)).findById(1L);
        verify(subscriptionRepository, never()).findById(anyLong());
        verify(skierRepository, never()).save(any(Skier.class));
    }

    // Test for removeSkier method
    @Test
    public void testRemoveSkier() {
        skierServices.removeSkier(1L);
        verify(skierRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testRemoveSkier_SkierNotFound() {
        doThrow(new NoSuchElementException()).when(skierRepository).deleteById(1L);

        assertThrows(NoSuchElementException.class, () -> {
            skierServices.removeSkier(1L);
        });

        verify(skierRepository, times(1)).deleteById(1L);
    }

    // Test for retrieveSkier method
    @Test
    public void testRetrieveSkier() {
        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));

        Skier retrievedSkier = skierServices.retrieveSkier(1L);

        assertNotNull(retrievedSkier);
        assertEquals(skier, retrievedSkier);

        verify(skierRepository, times(1)).findById(1L);
    }

    @Test
    public void testRetrieveSkier_SkierNotFound() {
        when(skierRepository.findById(1L)).thenReturn(Optional.empty());

        Skier retrievedSkier = skierServices.retrieveSkier(1L);

        assertNull(retrievedSkier);
        verify(skierRepository, times(1)).findById(1L);
    }

    // Test for retrieveSkiersBySubscriptionType method
    @Test
    public void testRetrieveSkiersBySubscriptionType() {
        List<Skier> skiers = Arrays.asList(skier);
        when(skierRepository.findBySubscription_TypeSub(TypeSubscription.ANNUAL)).thenReturn(skiers);

        List<Skier> retrievedSkiers = skierServices.retrieveSkiersBySubscriptionType(TypeSubscription.ANNUAL);

        assertNotNull(retrievedSkiers);
        assertEquals(1, retrievedSkiers.size());

        verify(skierRepository, times(1)).findBySubscription_TypeSub(TypeSubscription.ANNUAL);
    }

    @Test
    public void testRetrieveSkiersBySubscriptionType_Empty() {
        when(skierRepository.findBySubscription_TypeSub(TypeSubscription.ANNUAL)).thenReturn(new ArrayList<>());

        List<Skier> retrievedSkiers = skierServices.retrieveSkiersBySubscriptionType(TypeSubscription.ANNUAL);

        assertTrue(retrievedSkiers.isEmpty());

        verify(skierRepository, times(1)).findBySubscription_TypeSub(TypeSubscription.ANNUAL);
    }
}
