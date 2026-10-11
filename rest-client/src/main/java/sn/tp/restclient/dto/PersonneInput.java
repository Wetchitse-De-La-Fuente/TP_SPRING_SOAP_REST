package sn.tp.restclient.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Données JSON reçues par POST et PUT.
 * L'identifiant est fourni par le fournisseur SOAP / H2.
 */
public record PersonneInput(
        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @NotBlank(message = "Le prenom est obligatoire")
        String prenom,

        @NotNull(message = "L'age est obligatoire")
        @Min(value = 0, message = "L'age ne peut pas etre negatif")
        @Max(value = 150, message = "L'age ne peut pas depasser 150")
        Integer age
) {
}
