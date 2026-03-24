package it.uniroma2.dicii.bd.model.dto;

import java.time.LocalDate;

public record InserisciConfezioneRequest(
        LocalDate dataScadenza,
        String nomeProdotto,
        String nomeAzienda,
        int numeroCassetto,
        char letteraScaffale
) {}

