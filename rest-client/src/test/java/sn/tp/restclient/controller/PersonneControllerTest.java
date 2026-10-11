package sn.tp.restclient.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sn.tp.restclient.dto.PersonneDto;
import sn.tp.restclient.dto.PersonneInput;
import sn.tp.restclient.soap.PersonneSoapClient;

@WebMvcTest(PersonneController.class)
class PersonneControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PersonneSoapClient soap;

    private static final String JSON_PERSONNE = """
            {
              "nom": "Diop",
              "prenom": "Aminata",
              "age": 25
            }
            """;

    @Test
    void listeRetourneDuJson() throws Exception {
        PersonneDto personne = new PersonneDto(1L, "Diop", "Aminata", 25);
        when(soap.tous()).thenReturn(List.of(personne));

        mvc.perform(get("/api/personnes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Diop"));
    }

    @Test
    void creationRetourne201EtLocation() throws Exception {
        PersonneDto personne = new PersonneDto(12L, "Diop", "Aminata", 25);
        when(soap.ajouter(new PersonneInput("Diop", "Aminata", 25)))
                .thenReturn(personne);

        mvc.perform(post("/api/personnes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_PERSONNE))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/personnes/12"))
                .andExpect(jsonPath("$.id").value(12));
    }

    @Test
    void donne400SurSaisieInvalide() throws Exception {
        String json = """
                {
                  "nom": "",
                  "prenom": "Awa",
                  "age": -4
                }
                """;

        mvc.perform(post("/api/personnes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void donne400SansAge() throws Exception {
        String json = """
                {
                  "nom": "Diop",
                  "prenom": "Awa"
                }
                """;

        mvc.perform(post("/api/personnes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void lectureRetourne200() throws Exception {
        when(soap.obtenir(12L)).thenReturn(new PersonneDto(12L, "Diop", "Aminata", 25));

        mvc.perform(get("/api/personnes/12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prenom").value("Aminata"));
    }

    @Test
    void donne404PourUnIdInexistant() throws Exception {
        when(soap.obtenir(9999L)).thenReturn(null);

        mvc.perform(get("/api/personnes/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void modificationRetourne200() throws Exception {
        PersonneInput saisie = new PersonneInput("Diop", "Aminata", 25);
        PersonneDto personne = new PersonneDto(12L, "Diop", "Aminata", 25);
        when(soap.modifier(12L, saisie)).thenReturn(personne);

        mvc.perform(put("/api/personnes/12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_PERSONNE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12));
    }

    @Test
    void suppressionRetourne204() throws Exception {
        when(soap.supprimer(12L)).thenReturn(true);

        mvc.perform(delete("/api/personnes/12"))
                .andExpect(status().isNoContent());

        verify(soap).supprimer(12L);
    }

    @Test
    void suppressionInexistanteRetourne404() throws Exception {
        when(soap.supprimer(9999L)).thenReturn(false);

        mvc.perform(delete("/api/personnes/9999"))
                .andExpect(status().isNotFound());
    }
}
