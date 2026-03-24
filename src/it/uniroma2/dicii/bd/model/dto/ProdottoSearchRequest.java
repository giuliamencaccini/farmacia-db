package it.uniroma2.dicii.bd.model.dto;

public record ProdottoSearchRequest(
        int sceltaTipo,      // 1=Med, 2=Paraf, 3=Cosm, 4=Tutti
        String filtroNome    // può essere null
) {}
