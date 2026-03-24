package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.domain.MetodoContatto;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class AggiungiContattoFornitoreProcedureDAO
        implements GenericProcedureDAO<MetodoContatto, Boolean> {

    @Override
    public Boolean execute(MetodoContatto input) throws DAOException {
        try {
            Connection connection = ConnectionFactory.getConnection();
            CallableStatement cs = connection.prepareCall("{call aggiungiContattoFornitore(?,?,?)}");
            cs.setString(1, input.getNomeAzienda());
            cs.setString(2, input.getTipo());
            cs.setString(3, input.getValore());

            cs.execute();
            return true;

        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
    }
}

