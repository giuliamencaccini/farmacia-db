package it.uniroma2.dicii.bd.controller;

import it.uniroma2.dicii.bd.exception.ApplicationException;
import it.uniroma2.dicii.bd.exception.ControllerException;
import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.dao.ConnectionFactory;
import it.uniroma2.dicii.bd.model.dao.VisualizzaListaProdottiProcedureDAO;
import it.uniroma2.dicii.bd.model.dao.VerificaDisponibilitaProdottoProcedureDAO;
import it.uniroma2.dicii.bd.model.dao.RegistraVenditaProcedureDAO;
import it.uniroma2.dicii.bd.model.domain.Prodotto;
import it.uniroma2.dicii.bd.model.domain.Role;
import it.uniroma2.dicii.bd.model.domain.Vendita;
import it.uniroma2.dicii.bd.model.dto.DisponibilitaProdottoRequest;
import it.uniroma2.dicii.bd.model.dto.ProdottoSearchRequest;
import it.uniroma2.dicii.bd.model.dto.RegistraVenditaRequest;
import it.uniroma2.dicii.bd.model.dto.DisponibilitaProdottoResult;
import it.uniroma2.dicii.bd.view.FarmaciaView;

import java.sql.SQLException;
import java.util.List;

public class FarmaciaController implements Controller {

    private final FarmaciaView view = new FarmaciaView();

    @Override
    public void start() throws ApplicationException {
        try {
            ConnectionFactory.changeRole(Role.FARMACIA);
        } catch (SQLException e) {
            throw new ApplicationException(e.getMessage(), e);
        }

        while (true) {
            int choice = view.showMenu();
            try {
                switch (choice) {
                    case 1 -> listProducts();
                    case 2 -> checkAvailability();
                    case 3 -> registerSale();
                    case 4 -> System.exit(0);
                    default -> throw new ControllerException("Scelta non valida");
                }
            } catch (ControllerException e) {
                view.showError(e.getMessage());
            }
        }
    }

    //OPERAZIONI FARMACISTA
    private void listProducts() throws ControllerException {
        ProdottoSearchRequest request = view.askProdottoSearchRequest();
        if (request == null) return;

        try {
            List<Prodotto> prodotti = new VisualizzaListaProdottiProcedureDAO().execute(request);
            view.showProdottoList(prodotti);
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(),e);
        }
    }

    private void checkAvailability() throws ControllerException {
        DisponibilitaProdottoRequest req = view.askDisponibilitaProdotto();
        if (req == null) return;

        try {
            DisponibilitaProdottoResult result =
                    new VerificaDisponibilitaProdottoProcedureDAO().execute(req);

            view.showDisponibilitaDettagliata(result);
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(),e);
        }
    }

    private void registerSale() throws ControllerException {
        DisponibilitaProdottoRequest req = view.askDisponibilitaProdotto();
        if (req == null) return;

        try {
            DisponibilitaProdottoResult result =
                    new VerificaDisponibilitaProdottoProcedureDAO().execute(req);

            if (result.quantita() == 0) {
                view.showError("Nessuna confezione disponibile per questo prodotto.");
                return;
            }

            view.showDisponibilitaDettagliata(result);

            RegistraVenditaRequest request = view.askRegistraVenditaRequest();
            if (request == null) return;

            RegistraVenditaRequest requestProtetta = new RegistraVenditaRequest(
                    request.codiceConfezione(),
                    req.nomeProdotto(),
                    req.nomeAzienda(),
                    request.cf(),
                    request.prescrizione(),
                    request.mutuabilita()
            );

            Vendita v = new RegistraVenditaProcedureDAO().execute(requestProtetta);
            view.showVenditaCompletata(v);

        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }
}





