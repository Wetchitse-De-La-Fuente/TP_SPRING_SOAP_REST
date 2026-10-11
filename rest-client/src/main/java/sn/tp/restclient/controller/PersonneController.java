package sn.tp.restclient.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sn.tp.restclient.dto.PersonneDto;
import sn.tp.restclient.dto.PersonneInput;
import sn.tp.restclient.soap.PersonneSoapClient;

@RestController
@RequestMapping("/api/personnes")
public class PersonneController {

    private final PersonneSoapClient soap;

    public PersonneController(PersonneSoapClient soap) {
        this.soap = soap;
    }

    @GetMapping
    public List<PersonneDto> toutes() {
        return soap.tous();
    }

    @GetMapping("/{id}")
    public PersonneDto obtenir(@PathVariable long id) {
        PersonneDto personne = soap.obtenir(id);

        if (personne == null) {
            throw absente(id);
        }

        return personne;
    }

    @PostMapping
    public ResponseEntity<PersonneDto> ajouter(@Valid @RequestBody PersonneInput input) {
        PersonneDto personne = soap.ajouter(input);
        URI adresse = URI.create("/api/personnes/" + personne.id());

        return ResponseEntity.created(adresse).body(personne);
    }

    @PutMapping("/{id}")
    public PersonneDto modifier(
            @PathVariable long id,
            @Valid @RequestBody PersonneInput input
    ) {
        PersonneDto personne = soap.modifier(id, input);

        if (personne == null) {
            throw absente(id);
        }

        return personne;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable long id) {
        if (!soap.supprimer(id)) {
            throw absente(id);
        }

        return ResponseEntity.noContent().build();
    }

    private ResponseStatusException absente(long id) {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Personne " + id + " introuvable"
        );
    }
}
