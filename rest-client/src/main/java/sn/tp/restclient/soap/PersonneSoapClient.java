package sn.tp.restclient.soap;

import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import sn.tp.restclient.dto.PersonneDto;
import sn.tp.restclient.dto.PersonneInput;
import sn.tp.restclient.generated.AddPersonneRequest;
import sn.tp.restclient.generated.AddPersonneResponse;
import sn.tp.restclient.generated.DeletePersonneRequest;
import sn.tp.restclient.generated.DeletePersonneResponse;
import sn.tp.restclient.generated.GetAllPersonnesRequest;
import sn.tp.restclient.generated.GetAllPersonnesResponse;
import sn.tp.restclient.generated.GetPersonneRequest;
import sn.tp.restclient.generated.GetPersonneResponse;
import sn.tp.restclient.generated.PersonneType;
import sn.tp.restclient.generated.UpdatePersonneRequest;
import sn.tp.restclient.generated.UpdatePersonneResponse;

@Component
public class PersonneSoapClient {

    private final WebServiceTemplate template;

    public PersonneSoapClient(WebServiceTemplate personneWebServiceTemplate) {
        this.template = personneWebServiceTemplate;
    }

    public PersonneDto ajouter(PersonneInput personne) {
        AddPersonneRequest request = new AddPersonneRequest();
        request.setNom(personne.nom());
        request.setPrenom(personne.prenom());
        request.setAge(personne.age());

        AddPersonneResponse response = appeler(request, AddPersonneResponse.class);
        return convertir(response.getPersonne());
    }

    public PersonneDto obtenir(long id) {
        GetPersonneRequest request = new GetPersonneRequest();
        request.setId(id);

        GetPersonneResponse response = appeler(request, GetPersonneResponse.class);
        if (response.getPersonne() == null) {
            return null;
        }

        return convertir(response.getPersonne());
    }

    public List<PersonneDto> tous() {
        GetAllPersonnesRequest request = new GetAllPersonnesRequest();
        GetAllPersonnesResponse response = appeler(request, GetAllPersonnesResponse.class);

        return response.getPersonne()
                .stream()
                .map(this::convertir)
                .toList();
    }

    public PersonneDto modifier(long id, PersonneInput personne) {
        UpdatePersonneRequest request = new UpdatePersonneRequest();
        request.setId(id);
        request.setNom(personne.nom());
        request.setPrenom(personne.prenom());
        request.setAge(personne.age());

        UpdatePersonneResponse response = appeler(request, UpdatePersonneResponse.class);
        if (response.getPersonne() == null) {
            return null;
        }

        return convertir(response.getPersonne());
    }

    public boolean supprimer(long id) {
        DeletePersonneRequest request = new DeletePersonneRequest();
        request.setId(id);

        DeletePersonneResponse response = appeler(request, DeletePersonneResponse.class);
        return response.isDeleted();
    }

    private PersonneDto convertir(PersonneType personne) {
        return new PersonneDto(
                personne.getId(),
                personne.getNom(),
                personne.getPrenom(),
                personne.getAge()
        );
    }

    private <T> T appeler(Object request, Class<T> responseType) {
        Object response = template.marshalSendAndReceive(request);
        return responseType.cast(response);
    }
}
