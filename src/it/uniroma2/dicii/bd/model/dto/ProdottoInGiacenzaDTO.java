package it.uniroma2.dicii.bd.model.dto;

import java.time.LocalDate;

public record ProdottoInGiacenzaDTO(
        String nomeProdotto,
        String nomeAzienda,
        int quantita,
        LocalDate prossimaScadenza
) {}

