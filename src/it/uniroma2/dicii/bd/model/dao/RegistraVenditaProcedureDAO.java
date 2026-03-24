package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.domain.Vendita;
import it.uniroma2.dicii.bd.model.dto.RegistraVenditaRequest;

import java.sql.*;

public class RegistraVenditaProcedureDAO
        implements GenericProcedureDAO<RegistraVenditaRequest, Vendita> {

    @Override
    public Vendita execute(RegistraVenditaRequest request) throws DAOException {
        try {
            Connection connection = ConnectionFactory.getConnection();
            CallableStatement cs = connection.prepareCall("{call registraVendita(?,?,?,?,?,?,?)}");
            cs.setInt(1, request.codiceConfezione());
            cs.setString(2, request.nomeProdotto());
            cs.setString(3, request.nomeAzienda());

            // Gestione CF: se vuoto o nullo, passiamo NULL al database
            if (request.cf() != null && !request.cf().isBlank()) {
                cs.setString(4, request.cf());
            } else {
                cs.setNull(4, Types.CHAR);
            }

            cs.setBoolean(5, request.prescrizione());
            cs.setBoolean(6, request.mutuabilita());

            // Parametro di OUT per il codice della vendita
            cs.registerOutParameter(7, Types.INTEGER);

            cs.execute();

            // Recupero dell'ID generato dal database
            int codiceVendita = cs.getInt(7);

            return new Vendita(codiceVendita, request.codiceConfezione());

        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
    }
}


