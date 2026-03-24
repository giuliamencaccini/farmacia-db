package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.domain.AziendaFornitrice;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class InserisciFornitoreProcedureDAO
        implements GenericProcedureDAO<AziendaFornitrice, Boolean> {

    @Override
    public Boolean execute(AziendaFornitrice input) throws DAOException { // Rimosso throws SQLException
        try {
            Connection connection = ConnectionFactory.getConnection();
            CallableStatement cs = connection.prepareCall("{call inserisciFornitore(?,?,?,?,?,?)}");
            cs.setString(1, input.getNome());
            cs.setString(2, input.getContatto().getTipo());
            cs.setString(3, input.getContatto().getValore());
            cs.setString(4, input.getIndirizzo().getVia());
            cs.setString(5, input.getIndirizzo().getCivico());
            cs.setString(6, input.getIndirizzo().getCap());

            cs.execute();
            return true;
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
    }
}

