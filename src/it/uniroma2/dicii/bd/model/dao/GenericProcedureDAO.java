package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import java.sql.SQLException;

public interface GenericProcedureDAO<I, O> {

    O execute(I input) throws DAOException, SQLException;

}
