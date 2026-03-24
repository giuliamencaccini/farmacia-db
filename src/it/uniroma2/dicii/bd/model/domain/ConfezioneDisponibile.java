package it.uniroma2.dicii.bd.model.domain;

import java.time.LocalDate;

public class ConfezioneDisponibile {

    private final int codice;
    private final LocalDate dataScadenza;
    private final String letteraScaffale;
    private final int numeroCassetto;

    public ConfezioneDisponibile(int codice,
                                 LocalDate dataScadenza,
                                 String letteraScaffale,
                                 int numeroCassetto) {
        this.codice = codice;
        this.dataScadenza = dataScadenza;
        this.letteraScaffale = letteraScaffale;
        this.numeroCassetto = numeroCassetto;
    }

    public int getCodice() {
        return codice;
    }

    public LocalDate getDataScadenza() {
        return dataScadenza;
    }

    public String getPosizione() {
        return letteraScaffale + "-" + numeroCassetto;
    }
}

