package it.uniroma2.dicii.bd.model.dto;

import it.uniroma2.dicii.bd.model.domain.ConfezioneDisponibile;
import java.util.List;

// DTO DI OUTPUT
public record DisponibilitaProdottoResult(
        int quantita,
        List<ConfezioneDisponibile> confezioni
) {}


