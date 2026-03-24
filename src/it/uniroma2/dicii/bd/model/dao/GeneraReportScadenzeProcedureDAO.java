package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.model.dto.ProdottoInScadenzaDTO;
import it.uniroma2.dicii.bd.exception.DAOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GeneraReportScadenzeProcedureDAO
        implements GenericProcedureDAO<Integer, List<ProdottoInScadenzaDTO>> {

    @Override
    public List<ProdottoInScadenzaDTO> execute(Integer giorni) throws DAOException {

        List<ProdottoInScadenzaDTO> scadenze = new ArrayList<>();

        try {
            Connection connection = ConnectionFactory.getConnection();
            CallableStatement cs =
                    connection.prepareCall("{call generaReportScadenze(?)}");

            if (giorni != null) {
                cs.setInt(1, giorni);
            } else {
                cs.setNull(1, Types.INTEGER);
            }

            boolean status = cs.execute();
            if (status) {
                ResultSet rs = cs.getResultSet();
                while (rs.next()) {
                    scadenze.add(new ProdottoInScadenzaDTO(
                            rs.getString("NomeProdotto"),
                            rs.getDate("DataScadenza").toLocalDate(),
                            rs.getString("LetteraScaffale"),
                            rs.getInt("NumeroCassetto")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Errore report scadenze: " + e.getMessage());
        }

        return scadenze;
    }
}


