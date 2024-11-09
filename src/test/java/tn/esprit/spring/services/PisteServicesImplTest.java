package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.entities.Piste;
import tn.esprit.spring.repositories.IPisteRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PisteServicesImplTest {

    @Mock
    private IPisteRepository pisteRepository;

    @InjectMocks
    private PisteServicesImpl pisteServices;

    private Piste piste;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        piste = new Piste();
        piste.setNumPiste(1L);
        piste.setNamePiste("Green Valley");
        piste.setLength(1200);
        piste.setSlope(30);
    }

    // Tests for retrieveAllPistes()

    @Test
    void testRetrieveAllPistes_successful() {
        // Arrange
        List<Piste> pisteList = Arrays.asList(piste);
        when(pisteRepository.findAll()).thenReturn(pisteList);

        // Act
        List<Piste> pistes = pisteServices.retrieveAllPistes();

        // Assert
        assertEquals(1, pistes.size());
        assertEquals(piste.getNumPiste(), pistes.get(0).getNumPiste());
        verify(pisteRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveAllPistes_emptyList() {
        // Arrange
        when(pisteRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Piste> pistes = pisteServices.retrieveAllPistes();

        // Assert
        assertTrue(pistes.isEmpty());
        verify(pisteRepository, times(1)).findAll();
    }

    // Tests for addPiste()

    @Test
    void testAddPiste_successful() {
        // Arrange
        when(pisteRepository.save(any(Piste.class))).thenReturn(piste);

        // Act
        Piste savedPiste = pisteServices.addPiste(piste);

        // Assert
        assertNotNull(savedPiste);
        assertEquals(piste.getNumPiste(), savedPiste.getNumPiste());
        verify(pisteRepository, times(1)).save(piste);
    }

    @Test
    void testAddPiste_nullPiste() {
        // Arrange
        when(pisteRepository.save(null)).thenThrow(IllegalArgumentException.class);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> pisteServices.addPiste(null));
        verify(pisteRepository, times(1)).save(null);
    }

    // Tests for removePiste()

    @Test
    void testRemovePiste_successful() {
        // Act
        pisteServices.removePiste(1L);

        // Assert
        verify(pisteRepository, times(1)).deleteById(1L);
    }

    @Test
    void testRemovePiste_nonExistingId() {
        // Arrange
        doThrow(new IllegalArgumentException("Invalid ID")).when(pisteRepository).deleteById(2L);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> pisteServices.removePiste(2L));
        verify(pisteRepository, times(1)).deleteById(2L);
    }

    // Tests for retrievePiste()

    @Test
    void testRetrievePiste_found() {
        // Arrange
        when(pisteRepository.findById(1L)).thenReturn(Optional.of(piste));

        // Act
        Piste foundPiste = pisteServices.retrievePiste(1L);

        // Assert
        assertNotNull(foundPiste);
        assertEquals(piste.getNumPiste(), foundPiste.getNumPiste());
        verify(pisteRepository, times(1)).findById(1L);
    }

    @Test
    void testRetrievePiste_notFound() {
        // Arrange
        when(pisteRepository.findById(2L)).thenReturn(Optional.empty());

        // Act
        Piste foundPiste = pisteServices.retrievePiste(2L);

        // Assert
        assertNull(foundPiste);
        verify(pisteRepository, times(1)).findById(2L);
    }
}
