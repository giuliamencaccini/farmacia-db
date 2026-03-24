package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.domain.Indirizzo;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class AggiungiIndirizzoFornitoreProcedureDAO
        implements GenericProcedureDAO<Indirizzo, Boolean> {

    @Override
    public Boolean execute(Indirizzo input) throws DAOException {
        try {
            Connection connection = ConnectionFactory.getConnection();
            CallableStatement cs = connection.prepareCall("{call aggiungiIndirizzoFornitore(?,?,?,?)}");
            cs.setString(1, input.getVia());
            cs.setString(2, input.getCivico());
            cs.setString(3, input.getCap());
            cs.setString(4, input.getNomeAzienda());

            cs.execute();
            return true;
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
    }
}
