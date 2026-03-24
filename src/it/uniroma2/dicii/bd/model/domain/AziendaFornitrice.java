package it.uniroma2.dicii.bd.model.domain;

public class AziendaFornitrice {
    private String nome;
    private MetodoContatto contatto;
    private Indirizzo indirizzo;

    public AziendaFornitrice(String nome, MetodoContatto contatto, Indirizzo indirizzo) {
        this.nome = nome;
        this.contatto = contatto;
        this.indirizzo = indirizzo;
    }

    public MetodoContatto getContatto() {
        return contatto;
    }

    public Indirizzo getIndirizzo() {
        return indirizzo;
    }

    public String getNome() {
        return nome;
    }
}