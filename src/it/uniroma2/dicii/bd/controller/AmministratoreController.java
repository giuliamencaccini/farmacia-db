package it.uniroma2.dicii.bd.controller;

import it.uniroma2.dicii.bd.exception.ApplicationException;
import it.uniroma2.dicii.bd.exception.ControllerException;
import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.dao.*;
import it.uniroma2.dicii.bd.model.domain.Role;
import it.uniroma2.dicii.bd.model.domain.AziendaFornitrice;
import it.uniroma2.dicii.bd.model.domain.MetodoContatto;
import it.uniroma2.dicii.bd.model.domain.Indirizzo;
import it.uniroma2.dicii.bd.model.dto.*;
import it.uniroma2.dicii.bd.view.AmministratoreView;

import java.util.List;
import java.sql.SQLException;

public class AmministratoreController implements Controller {

    private final AmministratoreView view = new AmministratoreView();

    @Override
    public void start() throws ApplicationException {
        try {
            ConnectionFactory.changeRole(Role.AMMINISTRATORE);
        } catch (SQLException e) {
            throw new ApplicationException("Errore cambio ruolo", e);
        }

        while (true) {
            int choice = view.showMenu();
            try {
                switch (choice) {
                    case 1 -> generaReportScadenze();
                    case 2 -> generaReportGiacenze();
                    case 3 -> inserisciFornitore();
                    case 4 -> inserisciProdotto();
                    case 5 -> inserisciConfezione();
                    case 6 -> aggiungiContattoFornitore();
                    case 7 -> aggiungiIndirizzoFornitore();
                    case 8 -> aggiungiIndicazioneProdotto();
                    case 9 -> System.exit(0);
                    default -> throw new ControllerException("Scelta non valida");
                }
            } catch (ControllerException e) {
                view.showError(e.getMessage());
            }
        }
    }

    // REPORT
    private void generaReportScadenze() throws ControllerException {
        Integer giorni = view.askGiorniScadenza();
        try {
            List<ProdottoInScadenzaDTO> scadenze = new GeneraReportScadenzeProcedureDAO().execute(giorni);
            view.showReportScadenze(scadenze);
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(),e);
        }
    }

    private void generaReportGiacenze() throws ControllerException {
        try {
            List<ProdottoInGiacenzaDTO> giacenze = new GeneraReportGiacenzeProcedureDAO().execute(null);
            view.showReportGiacenze(giacenze);
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(),e);
        }
    }

    //INSERIMENTI E ANAGRAFICHE
    private void inserisciFornitore() throws ControllerException {
        AziendaFornitrice fornitore = view.askInserisciFornitore();
        if (fornitore == null) return;

        try {
            new InserisciFornitoreProcedureDAO().execute(fornitore);
            view.showOperationResult(true);
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(),e);
        }
    }

    private void inserisciProdotto() throws ControllerException {
        InserisciProdottoRequest req = view.askInserisciProdotto();
        if (req == null) return;

        try {
            new InserisciProdottoProcedureDAO().execute(req);
            view.showOperationResult(true);
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(),e);
        }
    }

    private void inserisciConfezione() throws ControllerException {
        InserisciConfezioneRequest req = view.askInserisciConfezione();
        if (req == null) return;

        try {
            new InserisciConfezioneProcedureDAO().execute(req);
            view.showOperationResult(true);
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(),e);
        }
    }

    private void aggiungiContattoFornitore() throws ControllerException {
        MetodoContatto contatto = view.askAggiungiContatto();
        if (contatto == null) return;

        try {
            new AggiungiContattoFornitoreProcedureDAO().execute(contatto);
            view.showOperationResult(true);
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(),e);
        }
    }

    private void aggiungiIndirizzoFornitore() throws ControllerException {
        Indirizzo indirizzo = view.askAggiungiIndirizzo();
        if (indirizzo == null) return;

        try {
            new AggiungiIndirizzoFornitoreProcedureDAO().execute(indirizzo);
            view.showOperationResult(true);
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(),e);
        }
    }

    private void aggiungiIndicazioneProdotto() throws ControllerException {
        String[] dati = view.askAggiungiIndicazioneProdotto();
        if (dati == null) return;

        try {
            IndicazioneProdottoRequest req = new IndicazioneProdottoRequest(dati[0], dati[1], dati[2]);
            new AggiungiIndicazioneProdottoProcedureDAO().execute(req);
            view.showOperationResult(true);
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(),e);
        }
    }
}


