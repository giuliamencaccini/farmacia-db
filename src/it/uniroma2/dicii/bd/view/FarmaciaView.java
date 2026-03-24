package it.uniroma2.dicii.bd.view;

import it.uniroma2.dicii.bd.model.domain.ConfezioneDisponibile;
import it.uniroma2.dicii.bd.model.domain.Prodotto;
import it.uniroma2.dicii.bd.model.dto.DisponibilitaProdottoRequest;
import it.uniroma2.dicii.bd.model.dto.ProdottoSearchRequest;
import it.uniroma2.dicii.bd.model.dto.RegistraVenditaRequest;
import it.uniroma2.dicii.bd.model.dto.DisponibilitaProdottoResult;
import it.uniroma2.dicii.bd.model.domain.Vendita;

import java.util.List;
import java.util.Scanner;

public class FarmaciaView {

    private final Scanner scanner = new Scanner(System.in);

    // MENU FARMACIA
    public int showMenu() {
        System.out.println("****************************************");
        System.out.println("*            FARMACIA  MENU            *");
        System.out.println("****************************************");
        System.out.println("1) Visualizza lista prodotti");
        System.out.println("2) Verifica disponibilità prodotto");
        System.out.println("3) Registra vendita");
        System.out.println("4) Esci");

        while (true) {
            System.out.print("Scelta: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= 4) {
                    return choice;
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("Opzione non valida.");
        }
    }

    // VISUALIZZA PRODOTTI - CATALOGO
    public ProdottoSearchRequest askProdottoSearchRequest() {
        try {
            System.out.println("\nTipologia prodotto:");
            System.out.println("1) Medicinali");
            System.out.println("2) Parafarmaci");
            System.out.println("3) Cosmetici");
            System.out.println("4) Tutti");

            System.out.print("Scelta: ");
            int tipo = Integer.parseInt(scanner.nextLine());

            if (tipo < 1 || tipo > 4) {
                System.out.println("Scelta non valida.");
                return null;
            }

            System.out.print("Filtro nome (invio per nessun filtro): ");
            String filtro = scanner.nextLine();
            if (filtro.isBlank()) {
                filtro = null;
            }

            return new ProdottoSearchRequest(tipo, filtro);
        } catch (NumberFormatException e) {
            System.out.println("Input non valido.");
            return null;
        }
    }

    public void showProdottoList(List<Prodotto> prodotti) {
        if (prodotti.isEmpty()) {
            System.out.println("\nNessun prodotto trovato.\n");
            return;
        }

        System.out.println("\n--- LISTA PRODOTTI ---");

        // COLONNE
        String format = "%-25s %-15s %-15s %-10s %-10s %-30s";

        // INTESTAZIONE
        System.out.printf(
                format + "%n",
                "Nome",
                "Tipo",
                "Azienda",
                "Mutuabile",
                "Ricetta",
                "Indicazioni d'uso"
        );
        System.out.println("-----------------------------------------------------------------------------------------------------");

        // RIGHE
        for (Prodotto p : prodotti) {
            String uso = p.getIndicazioniUso();
            if (uso == null || uso.isBlank()) {
                uso = "—";
            }

            System.out.printf(
                    format + "%n",
                    p.getNome(),
                    p.getTipo(),
                    p.getAzienda(),
                    p.getMutuabile(),
                    p.getRicetta(),
                    uso
            );
        }

        System.out.println();
    }

    public void showVenditaCompletata(Vendita v) {
        System.out.println("\n========================================");
        System.out.println("     VENDITA REGISTRATA CON SUCCESSO    ");
        System.out.println("========================================");
        System.out.printf(" COD. VENDITA:    %d%n", v.getId());
        System.out.printf(" COD. CONFEZIONE: %d%n", v.getCodiceConfezione());
        System.out.println("========================================\n");
    }

    // DISPONIBILITÀ
    public DisponibilitaProdottoRequest askDisponibilitaProdotto() {
        System.out.print("\nNome prodotto: ");
        String nomeProdotto = scanner.nextLine().trim();

        System.out.print("Nome azienda: ");
        String nomeAzienda = scanner.nextLine().trim();

        if (nomeProdotto.isBlank() || nomeAzienda.isBlank()) {
            System.out.println("Campi obbligatori.");
            return null;
        }

        return new DisponibilitaProdottoRequest(nomeProdotto, nomeAzienda);
    }

    // UTILIZZATA SIA IN VENDITA CHE N VERIFICA DISPONIBILITA
    public void showDisponibilitaDettagliata(DisponibilitaProdottoResult res) {
        System.out.println("Quantità disponibile: " + res.quantita());

        if (!res.confezioni().isEmpty()) {
            System.out.println("Confezioni disponibili:");
            for (ConfezioneDisponibile c : res.confezioni()) {
                System.out.printf(
                        "Codice %-4d | Scadenza %-10s | Posizione %-5s%n",
                        c.getCodice(),
                        c.getDataScadenza(),
                        c.getPosizione()
                );
            }
        }
        System.out.println();
    }

    // REGISTRA VENDITA
    public RegistraVenditaRequest askRegistraVenditaRequest() {
        try {
            System.out.print("\nCodice confezione: ");
            int codiceConfezione = Integer.parseInt(scanner.nextLine());

            System.out.print("Codice fiscale facoltativo (invio se assente): ");
            String cf = scanner.nextLine();
            if (cf.isBlank()) {
                cf = null;
            }

            System.out.print("Prescrizione presentata? (si/no): ");
            boolean prescrizione = scanner.nextLine().equalsIgnoreCase("si");

            System.out.print("Usare mutuabilità SSN? (si/no): ");
            boolean mutuabilita = scanner.nextLine().equalsIgnoreCase("si");

            return new RegistraVenditaRequest(
                    codiceConfezione,
                    "",// nomeProdotto (placeholder)
                    "",// nomeAzienda  (placeholder)
                    cf,
                    prescrizione,
                    mutuabilita
            );
        } catch (NumberFormatException e) {
            System.out.println("Input non valido.");
            return null;
        }
    }

    // OUTPUT
    public void showError(String message) {
        System.out.println("\nErrore: " + message + "\n");
    }
}