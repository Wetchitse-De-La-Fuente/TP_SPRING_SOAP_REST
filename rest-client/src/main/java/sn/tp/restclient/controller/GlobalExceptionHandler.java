package sn.tp.restclient.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.soap.client.SoapFaultClientException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebServiceIOException.class)
    public ProblemDetail serviceIndisponible(WebServiceIOException exception) {
        ProblemDetail probleme = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Le service SOAP est inaccessible. Verifiez le port 8081."
        );
        probleme.setTitle("Service SOAP indisponible");
        return probleme;
    }

    @ExceptionHandler(SoapFaultClientException.class)
    public ProblemDetail erreurSoap(SoapFaultClientException exception) {
        ProblemDetail probleme = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_GATEWAY,
                "Le fournisseur SOAP a retourne une erreur : "
                        + exception.getFaultStringOrReason()
        );
        probleme.setTitle("Erreur SOAP");
        return probleme;
    }

    @ExceptionHandler(WebServiceException.class)
    public ProblemDetail erreurPasserelle(WebServiceException exception) {
        ProblemDetail probleme = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_GATEWAY,
                "Une erreur est survenue pendant l'appel SOAP."
        );
        probleme.setTitle("Erreur de passerelle SOAP");
        return probleme;
    }
}
