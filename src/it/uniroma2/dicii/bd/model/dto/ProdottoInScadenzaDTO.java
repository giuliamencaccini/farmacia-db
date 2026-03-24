package it.uniroma2.dicii.bd.model.dto;

import java.time.LocalDate;

public record ProdottoInScadenzaDTO(
        String nomeProdotto,
        LocalDate dataScadenza,
        String letteraScaffale,
        int numeroCassetto
) {}
