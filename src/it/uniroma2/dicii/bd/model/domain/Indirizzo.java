package it.uniroma2.dicii.bd.model.domain;

public class Indirizzo {
    private String via;
    private String civico;
    private String cap;
    private String nomeAzienda; // Riferimento all'azienda

    public Indirizzo(String via, String civico, String cap, String nomeAzienda) {
        this.via = via;
        this.civico = civico;
        this.cap = cap;
        this.nomeAzienda = nomeAzienda;
    }

    public String getVia() { return via; }
    public String getCivico() { return civico; }
    public String getCap() { return cap; }
    public String getNomeAzienda() { return nomeAzienda; }
}