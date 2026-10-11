package sn.tp.soapservice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import sn.tp.soapservice.entity.PersonneEntity;

@DataJpaTest
class PersonneRepositoryTest {

    @Autowired
    private PersonneRepository repository;

    @Test
    void sauvegardeEtLitUnePersonne() {
        PersonneEntity personne = repository.save(
                new PersonneEntity("Diop", "Aminata", 25)
        );

        assertThat(personne.getId()).isNotNull();
        assertThat(repository.findById(personne.getId())).isPresent();
    }

    @Test
    void modifieEtSupprimeUnePersonne() {
        PersonneEntity personne = repository.save(
                new PersonneEntity("Diop", "Aminata", 25)
        );

        personne.setPrenom("Awa");
        repository.saveAndFlush(personne);

        String prenomEnregistre = repository.findById(personne.getId())
                .orElseThrow()
                .getPrenom();
        assertThat(prenomEnregistre).isEqualTo("Awa");

        repository.deleteById(personne.getId());
        repository.flush();

        assertThat(repository.findById(personne.getId())).isEmpty();
    }
}
