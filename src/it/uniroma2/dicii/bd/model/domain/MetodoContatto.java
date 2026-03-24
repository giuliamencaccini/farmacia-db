package it.uniroma2.dicii.bd.model.domain;

public class MetodoContatto {
    private String nomeAzienda; //per identificare a chi appartiene il contatto
    private String tipo;
    private String valore;

    public MetodoContatto(String nomeAzienda, String tipo, String valore) {
        this.nomeAzienda = nomeAzienda;
        this.tipo = tipo;
        this.valore = valore;
    }

    public String getNomeAzienda() { return nomeAzienda; }
    public String getTipo() { return tipo; }
    public String getValore() { return valore; }
}
