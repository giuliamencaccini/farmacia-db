package it.uniroma2.dicii.bd.view;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import it.uniroma2.dicii.bd.model.domain.AziendaFornitrice;
import it.uniroma2.dicii.bd.model.domain.Indirizzo;
import it.uniroma2.dicii.bd.model.domain.MetodoContatto;
import it.uniroma2.dicii.bd.model.dto.ProdottoInGiacenzaDTO;
import it.uniroma2.dicii.bd.model.dto.ProdottoInScadenzaDTO;
import it.uniroma2.dicii.bd.model.dto.InserisciConfezioneRequest;
import it.uniroma2.dicii.bd.model.dto.InserisciProdottoRequest;

public class AmministratoreView {

    private final Scanner scanner = new Scanner(System.in);

    // MENU AMMINISTRAZIONE
    public int showMenu() {
        System.out.println("****************************************");
        System.out.println("*       AMMINISTRAZIONE FARMACIA       *");
        System.out.println("****************************************");
        System.out.println("1) Genera report scadenze");
        System.out.println("2) Genera report giacenze");
        System.out.println("3) Inserisci fornitore");
        System.out.println("4) Inserisci prodotto");
        System.out.println("5) Inserisci confezione");
        System.out.println("6) Aggiungi contatto fornitore");
        System.out.println("7) Aggiungi indirizzo fornitore");
        System.out.println("8) Aggiungi indicazioni d'uso a prodotto");
        System.out.println("9) Esci");

        while (true) {
            System.out.print("Scelta: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= 9) {
                    return choice;
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("Opzione non valida.");
        }
    }

    // REPORT
    public Integer askGiorniScadenza() {
        System.out.print("Numero di giorni (invio per default = 7 giorni): ");
        String input = scanner.nextLine();

        if (input.isBlank()) {
            return null; // default gestito dal DB
        }

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Valore non valido.");
            return null;
        }
    }

    public void showReportScadenze(List<ProdottoInScadenzaDTO> scadenze) {
        System.out.println("\n================ REPORT SCADENZE ================");

        if (scadenze.isEmpty()) {
            System.out.println("Nessun prodotto in scadenza.");
            System.out.println("=================================================\n");
            return;
        }

        System.out.printf("%-25s %-12s %-10s%n",
                "Prodotto", "Scadenza", "Posizione");

        for (ProdottoInScadenzaDTO p : scadenze) {
            System.out.printf("%-25s %-12s %s%d%n",
                    p.nomeProdotto(),
                    p.dataScadenza(),
                    p.letteraScaffale(),
                    p.numeroCassetto());
        }

        System.out.println("=================================================\n");
    }

    public void showReportGiacenze(List<ProdottoInGiacenzaDTO> giacenze) {
        System.out.println("\n========================== REPORT GIACENZE ============================");

        if (giacenze.isEmpty()) {
            System.out.println("Magazzino vuoto.");
            System.out.println("======================================================================\n");
            return;
        }

        System.out.printf("%-25s %-20s %-8s %-12s%n",
                "Prodotto", "Azienda", "Qta", "Prossima scad.");

        for (ProdottoInGiacenzaDTO g : giacenze) {
            System.out.printf("%-25s %-20s %-8d %-12s%n",
                    g.nomeProdotto(),
                    g.nomeAzienda(),
                    g.quantita(),
                    g.prossimaScadenza());
        }

        System.out.println("======================================================================\n");
    }

    // INSERIMENTI - GESTIONE ANAGRAFICHE
    public AziendaFornitrice askInserisciFornitore() {
        System.out.print("Nome fornitore: ");
        String nome = scanner.nextLine();

        System.out.print("Contatto principale (Telefono / Fax / Email): ");
        String tipo = scanner.nextLine();

        System.out.print("Valore contatto: ");
        String valore = scanner.nextLine();

        System.out.print("Indirizzo di fatturazione, via: ");
        String via = scanner.nextLine();

        System.out.print("Civico: ");
        String civico = scanner.nextLine();

        System.out.print("CAP: ");
        String cap = scanner.nextLine();

        if (nome.isBlank() || tipo.isBlank() || valore.isBlank()
                || via.isBlank() || civico.isBlank() || cap.isBlank()) {
            showError("Tutti i campi sono obbligatori.");
            return null;
        }

        MetodoContatto contatto = new MetodoContatto(nome, tipo, valore);
        Indirizzo indirizzo = new Indirizzo(via, civico, cap, nome);

        return new AziendaFornitrice(nome, contatto, indirizzo);
    }

    public InserisciProdottoRequest askInserisciProdotto() {
        System.out.print("Nome prodotto: ");
        String nome = scanner.nextLine();

        System.out.print("Nome azienda fornitrice: ");
        String azienda = scanner.nextLine();

        System.out.print("Tipo (Medicinale / Parafarmaco / Cosmetico): ");
        String tipo = scanner.nextLine();

        System.out.print("Mutuabile SSN? (si/no): ");
        boolean mutuabile = scanner.nextLine().equalsIgnoreCase("si");

        System.out.print("Richiede prescrizione? (si/no): ");
        boolean prescrizione = scanner.nextLine().equalsIgnoreCase("si");

        // Validazione semplice della View (opzionale ma consigliata)
        if (nome.isBlank() || azienda.isBlank()) {
            showError("Nome prodotto e azienda sono obbligatori.");
            return null;
        }

        return new InserisciProdottoRequest(nome, azienda, tipo, mutuabile, prescrizione);
    }

    public InserisciConfezioneRequest askInserisciConfezione() {
        try {
            System.out.print("Data scadenza (yyyy-MM-dd): ");
            LocalDate data = LocalDate.parse(scanner.nextLine());

            System.out.print("Nome prodotto: ");
            String prodotto = scanner.nextLine();

            System.out.print("Nome azienda: ");
            String azienda = scanner.nextLine();

            System.out.print("Numero cassetto: ");
            int cassetto = Integer.parseInt(scanner.nextLine());

            System.out.print("Lettera scaffale: ");
            String s = scanner.nextLine().trim().toUpperCase();
            if (s.length() != 1) {
                System.out.println("Lettera scaffale non valida.");
                return null;
            }

            char scaffale = s.charAt(0);

            return new InserisciConfezioneRequest(
                    data,
                    prodotto,
                    azienda,
                    cassetto,
                    scaffale
            );
        } catch (Exception e) {
            System.out.println("Errore nei dati inseriti.");
            return null;
        }
    }

    public String[] askAggiungiIndicazioneProdotto() {
        System.out.print("Nome prodotto: ");
        String nomeProdotto = scanner.nextLine();

        System.out.print("Nome azienda: ");
        String nomeAzienda = scanner.nextLine();

        System.out.print("Indicazione d'uso: ");
        String indicazione = scanner.nextLine();

        if (nomeProdotto.isBlank() || nomeAzienda.isBlank() || indicazione.isBlank()) {
            showError("Campi obbligatori.");
            return null;
        }

        return new String[]{nomeProdotto, nomeAzienda, indicazione};
    }

    public MetodoContatto askAggiungiContatto() {
        System.out.print("Nome azienda: ");
        String azienda = scanner.nextLine();
        System.out.print("Tipo contatto: ");
        String tipo = scanner.nextLine();
        System.out.print("Valore: ");
        String valore = scanner.nextLine();

        if (azienda.isBlank() || tipo.isBlank() || valore.isBlank()) {
            showError("Campi obbligatori.");
            return null;
        }
        return new MetodoContatto(azienda, tipo, valore);
    }

    public Indirizzo askAggiungiIndirizzo() {
        System.out.print("Via: ");
        String via = scanner.nextLine();
        System.out.print("Civico: ");
        String civico = scanner.nextLine();
        System.out.print("CAP: ");
        String cap = scanner.nextLine();
        System.out.print("Nome azienda: ");
        String azienda = scanner.nextLine();

        if (via.isBlank() || civico.isBlank() || cap.isBlank() || azienda.isBlank()) {
            showError("Campi obbligatori.");
            return null;
        }

        return new Indirizzo(via, civico, cap, azienda);
    }

    // OUTPUT
    public void showOperationResult(boolean success) {
        if (success) {
            System.out.println("\nOperazione completata con successo.\n");
        } else {
            System.out.println("\nOperazione fallita.\n");
        }
    }

    public void showError(String message) {
        System.out.println("\nErrore: " + message + "\n");
    }
}

