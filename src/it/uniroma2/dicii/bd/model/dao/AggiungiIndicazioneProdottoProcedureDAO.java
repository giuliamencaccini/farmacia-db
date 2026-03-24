package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.dto.IndicazioneProdottoRequest;
import java.sql.*;

public class AggiungiIndicazioneProdottoProcedureDAO
        implements GenericProcedureDAO<IndicazioneProdottoRequest, Void> {

    @Override
    public Void execute(IndicazioneProdottoRequest input) throws DAOException {

        try {
            Connection connection = ConnectionFactory.getConnection();
            CallableStatement cs = connection.prepareCall("{call aggiungiIndicazioneProdotto(?,?,?)}");
            cs.setString(1, input.nomeProdotto());
            cs.setString(2, input.nomeAzienda());
            cs.setString(3, input.indicazione());

            cs.execute();
            return null;

        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
    }
}

