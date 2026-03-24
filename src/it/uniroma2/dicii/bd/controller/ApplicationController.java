package it.uniroma2.dicii.bd.controller;

import it.uniroma2.dicii.bd.exception.ApplicationException;
import it.uniroma2.dicii.bd.model.domain.Credentials;

public class ApplicationController implements Controller {
    Credentials cred;

    @Override
    public void start() throws ApplicationException {
        LoginController loginController = new LoginController();
        loginController.start();
        cred = loginController.getCred();

        if (cred == null || cred.getRole() == null) {
            throw new RuntimeException("Credenziali non valide.");
        }

        switch (cred.getRole()) {
            case FARMACIA -> new FarmaciaController().start();
            case AMMINISTRATORE -> new AmministratoreController().start();
            default -> throw new RuntimeException("Credenziali non valide.");
        }
    }

}