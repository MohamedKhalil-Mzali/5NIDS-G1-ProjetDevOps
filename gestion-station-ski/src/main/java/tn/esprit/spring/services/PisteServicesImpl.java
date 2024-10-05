package tn.esprit.spring.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Piste;
import tn.esprit.spring.repositories.IPisteRepository;
import lombok.extern.sl4j.Sl4j
import java.util.List;
@AllArgsConstructor
@Service
@Sl4j
public class PisteServicesImpl implements  IPisteServices{

    private IPisteRepository pisteRepository;

    @Override
    public List<Piste> retrieveAllPistes() {
      log.info("afficher tout");
        return pisteRepository.findAll();
    }

    @Override
    public Piste addPiste(Piste piste) {
       log.info("ajouter");
        return pisteRepository.save(piste);
    }

    @Override
    public void removePiste(Long numPiste) {
     log.info("supprimer");
        pisteRepository.deleteById(numPiste);
    }

    @Override
    public Piste retrievePiste(Long numPiste) {
            log.info("afficher ");
        return pisteRepository.findById(numPiste).orElse(null);
    }
}
