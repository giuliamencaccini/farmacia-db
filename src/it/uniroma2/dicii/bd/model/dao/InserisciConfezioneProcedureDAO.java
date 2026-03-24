package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.dto.InserisciConfezioneRequest;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

public class InserisciConfezioneProcedureDAO
        implements GenericProcedureDAO<InserisciConfezioneRequest, Boolean> {

    @Override
    public Boolean execute(InserisciConfezioneRequest req) throws DAOException {
        try {
            Connection connection = ConnectionFactory.getConnection();
            CallableStatement cs = connection.prepareCall("{call inserisciConfezione(?,?,?,?,?)}");
            cs.setDate(1, Date.valueOf(req.dataScadenza()));
            cs.setString(2, req.nomeProdotto());
            cs.setString(3, req.nomeAzienda());
            cs.setInt(4, req.numeroCassetto());
            cs.setString(5, String.valueOf(req.letteraScaffale()));

            cs.execute();
            return true;
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
    }
}


