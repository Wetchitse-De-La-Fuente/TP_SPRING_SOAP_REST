package sn.tp.soapservice.endpoint;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import sn.tp.soapservice.entity.PersonneEntity;
import sn.tp.soapservice.generated.AddPersonneRequest;
import sn.tp.soapservice.generated.AddPersonneResponse;
import sn.tp.soapservice.generated.DeletePersonneRequest;
import sn.tp.soapservice.generated.DeletePersonneResponse;
import sn.tp.soapservice.generated.GetAllPersonnesRequest;
import sn.tp.soapservice.generated.GetAllPersonnesResponse;
import sn.tp.soapservice.generated.GetPersonneRequest;
import sn.tp.soapservice.generated.GetPersonneResponse;
import sn.tp.soapservice.generated.PersonneType;
import sn.tp.soapservice.generated.UpdatePersonneRequest;
import sn.tp.soapservice.generated.UpdatePersonneResponse;
import sn.tp.soapservice.service.PersonneService;

@Endpoint
public class PersonneEndpoint {

    private static final String NAMESPACE = "http://tp.sn/personnes";
    private final PersonneService service;

    public PersonneEndpoint(PersonneService service) {
        this.service = service;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "addPersonneRequest")
    @ResponsePayload
    public AddPersonneResponse addPersonne(@RequestPayload AddPersonneRequest request) {
        PersonneEntity personne = service.ajouter(
                request.getNom(),
                request.getPrenom(),
                request.getAge()
        );

        AddPersonneResponse response = new AddPersonneResponse();
        response.setPersonne(convertir(personne));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "getPersonneRequest")
    @ResponsePayload
    public GetPersonneResponse getPersonne(@RequestPayload GetPersonneRequest request) {
        GetPersonneResponse response = new GetPersonneResponse();

        service.obtenir(request.getId()).ifPresent(personne -> {
            response.setPersonne(convertir(personne));
        });

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "getAllPersonnesRequest")
    @ResponsePayload
    public GetAllPersonnesResponse getAllPersonnes(
            @RequestPayload GetAllPersonnesRequest request
    ) {
        GetAllPersonnesResponse response = new GetAllPersonnesResponse();

        for (PersonneEntity personne : service.tous()) {
            response.getPersonne().add(convertir(personne));
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "updatePersonneRequest")
    @ResponsePayload
    public UpdatePersonneResponse updatePersonne(@RequestPayload UpdatePersonneRequest request) {
        UpdatePersonneResponse response = new UpdatePersonneResponse();

        service.modifier(
                request.getId(),
                request.getNom(),
                request.getPrenom(),
                request.getAge()
        ).ifPresent(personne -> {
            response.setPersonne(convertir(personne));
        });

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "deletePersonneRequest")
    @ResponsePayload
    public DeletePersonneResponse deletePersonne(@RequestPayload DeletePersonneRequest request) {
        DeletePersonneResponse response = new DeletePersonneResponse();
        response.setDeleted(service.supprimer(request.getId()));
        return response;
    }

    private PersonneType convertir(PersonneEntity source) {
        PersonneType personneSoap = new PersonneType();
        personneSoap.setId(source.getId());
        personneSoap.setNom(source.getNom());
        personneSoap.setPrenom(source.getPrenom());
        personneSoap.setAge(source.getAge());

        return personneSoap;
    }
}
