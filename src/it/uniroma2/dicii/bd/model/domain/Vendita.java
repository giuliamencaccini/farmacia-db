package it.uniroma2.dicii.bd.model.domain;

public class Vendita {
    private final int id;
    private final int codiceConfezione;

    public Vendita(int id, int codiceConfezione) {
        this.id = id;
        this.codiceConfezione = codiceConfezione;
    }

    // Getter
    public int getId() { return id; }
    public int getCodiceConfezione() { return codiceConfezione; }
}