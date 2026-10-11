package sn.tp.restclient.dto;

/**
 * Réponse JSON exposée aux clients de l'API REST.
 */
public record PersonneDto(
        Long id,
        String nom,
        String prenom,
        int age
) {
}
