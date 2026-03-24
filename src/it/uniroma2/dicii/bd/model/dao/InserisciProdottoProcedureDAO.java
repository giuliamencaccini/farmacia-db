package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.dto.InserisciProdottoRequest;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class InserisciProdottoProcedureDAO
        implements GenericProcedureDAO<InserisciProdottoRequest, Boolean> {

    @Override
    public Boolean execute(InserisciProdottoRequest req) throws DAOException {
        try {
            Connection connection = ConnectionFactory.getConnection();
            CallableStatement cs = connection.prepareCall("{call inserisciProdotto(?,?,?,?,?)}");
            cs.setString(1, req.nome());
            cs.setString(2, req.nomeAzienda());
            cs.setString(3, req.tipo());
            cs.setBoolean(4, req.mutuabileSSN());
            cs.setBoolean(5, req.prescrizioneMedica());

            cs.execute();
            return true;
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
    }
}

