package sn.tp.soapservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.tp.soapservice.entity.PersonneEntity;

public interface PersonneRepository extends JpaRepository<PersonneEntity, Long> {
    // Spring Data JPA fournit automatiquement les opérations CRUD.
}
