package it.uniroma2.dicii.bd.model.dto;

public record RegistraVenditaRequest(
        int codiceConfezione,
        String nomeProdotto,
        String nomeAzienda,
        String cf,
        boolean prescrizione,
        boolean mutuabilita
) {}
