package sn.tp.soapservice.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.tp.soapservice.entity.PersonneEntity;
import sn.tp.soapservice.repository.PersonneRepository;

@Service
public class PersonneService {

    private final PersonneRepository repository;

    public PersonneService(PersonneRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PersonneEntity ajouter(String nom, String prenom, int age) {
        verifier(nom, prenom, age);

        PersonneEntity personne = new PersonneEntity(
                nom.trim(),
                prenom.trim(),
                age
        );

        return repository.save(personne);
    }

    @Transactional(readOnly = true)
    public Optional<PersonneEntity> obtenir(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<PersonneEntity> tous() {
        return repository.findAll(Sort.by("id").ascending());
    }

    @Transactional
    public Optional<PersonneEntity> modifier(Long id, String nom, String prenom, int age) {
        verifier(nom, prenom, age);

        return repository.findById(id).map(personne -> {
            personne.setNom(nom.trim());
            personne.setPrenom(prenom.trim());
            personne.setAge(age);

            return repository.save(personne);
        });
    }

    @Transactional
    public boolean supprimer(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        return true;
    }

    private void verifier(String nom, String prenom, int age) {
        boolean nomInvalide = nom == null || nom.isBlank();
        boolean prenomInvalide = prenom == null || prenom.isBlank();
        boolean ageInvalide = age < 0 || age > 150;

        if (nomInvalide || prenomInvalide || ageInvalide) {
            throw new IllegalArgumentException(
                    "Nom, prenom et age (0 a 150) sont obligatoires"
            );
        }
    }
}
