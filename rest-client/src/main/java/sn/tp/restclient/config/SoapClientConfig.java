package sn.tp.restclient.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;

@Configuration
public class SoapClientConfig {

    @Bean
    public Jaxb2Marshaller personneMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("sn.tp.restclient.generated");
        return marshaller;
    }

    @Bean
    public WebServiceTemplate personneWebServiceTemplate(
            Jaxb2Marshaller personneMarshaller,
            @Value("${soap.service.url}") String soapUrl) {
        WebServiceTemplate template = new WebServiceTemplate();
        template.setMarshaller(personneMarshaller);
        template.setUnmarshaller(personneMarshaller);
        template.setDefaultUri(soapUrl);

        // Evite de bloquer indefiniment si le fournisseur SOAP est indisponible.
        HttpUrlConnectionMessageSender sender = new HttpUrlConnectionMessageSender();
        sender.setConnectionTimeout(Duration.ofSeconds(5));
        sender.setReadTimeout(Duration.ofSeconds(15));
        template.setMessageSender(sender);

        return template;
    }
}
