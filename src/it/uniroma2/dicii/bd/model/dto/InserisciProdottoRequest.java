package it.uniroma2.dicii.bd.model.dto;

public record InserisciProdottoRequest(
        String nome,
        String nomeAzienda,
        String tipo,
        boolean mutuabileSSN,
        boolean prescrizioneMedica
) {}
