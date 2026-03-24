package it.uniroma2.dicii.bd.model.domain;

public class Prodotto {

    private final String nome;
    private final String tipo;
    private final String azienda;
    private final String mutuabile;
    private final String ricetta;
    private final String indicazioniUso;

    public Prodotto(String nome,
                    String tipo,
                    String azienda,
                    String mutuabile,
                    String ricetta,
                    String indicazioniUso) {
        this.nome = nome;
        this.tipo = tipo;
        this.azienda = azienda;
        this.mutuabile = mutuabile;
        this.ricetta = ricetta;
        this.indicazioniUso = indicazioniUso;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    public String getAzienda() {
        return azienda;
    }

    public String getMutuabile() {
        return mutuabile;
    }

    public String getRicetta() {
        return ricetta;
    }

    public String getIndicazioniUso() {
        return indicazioniUso;
    }
}




