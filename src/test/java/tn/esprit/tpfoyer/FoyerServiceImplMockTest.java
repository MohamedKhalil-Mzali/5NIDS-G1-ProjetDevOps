package tn.esprit.tpfoyer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.tpfoyer.entity.Foyer;
import tn.esprit.tpfoyer.repository.FoyerRepository;
import tn.esprit.tpfoyer.service.FoyerServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@TestMethodOrder(OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class FoyerServiceImplMockTest {
    @Mock
    FoyerRepository foyerRepository;

    @InjectMocks
    FoyerServiceImpl foyerService;

    Foyer foyer = new Foyer("foyer1",20L);
    List<Foyer> listFoyer = new ArrayList<>() {
        {
            add(new Foyer("foyer2", 10L));
            add(new Foyer("foyer3", 15L));

        }
    };

    @Test
    @Order(1)
    void testRetrieveFoyer() {
        Mockito.when(foyerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(foyer));
        Foyer foyer1 = foyerService.retrieveFoyer(1L);
        Assertions.assertNotNull(foyer1);
    }

    @Test
    @Order(2)
    void testRetrieveAllFoyers() {
        Mockito.when(foyerRepository.findAll()).thenReturn(listFoyer);
        List<Foyer> listF = foyerService.retrieveAllFoyers();
        Assertions.assertEquals(2, listF.size());
    }

    @Test
    @Order(3)
    void testAddFoyer() {
        Foyer f = new Foyer();
        f.setNomFoyer("foyerTest");

        Mockito.when(foyerRepository.save(Mockito.any(Foyer.class)))
                .thenAnswer(invocation -> {
                    Foyer savedFoyer = invocation.getArgument(0);
                    listFoyer.add(savedFoyer);
                    return savedFoyer;
                });

        foyerService.addFoyer(f);

        Assertions.assertEquals(3, listFoyer.size());
    }
}