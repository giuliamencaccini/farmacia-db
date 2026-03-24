package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.model.dto.ProdottoInGiacenzaDTO;
import it.uniroma2.dicii.bd.exception.DAOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GeneraReportGiacenzeProcedureDAO
        implements GenericProcedureDAO<Void, List<ProdottoInGiacenzaDTO>> {

    @Override
    public List<ProdottoInGiacenzaDTO> execute(Void input) throws DAOException {

        List<ProdottoInGiacenzaDTO> giacenze = new ArrayList<>();

        try {
            Connection connection = ConnectionFactory.getConnection();
            CallableStatement cs =
                    connection.prepareCall("{call generaReportGiacenze()}");

            boolean status = cs.execute();
            if (status) {
                ResultSet rs = cs.getResultSet();
                while (rs.next()) {
                    giacenze.add(new ProdottoInGiacenzaDTO(
                            rs.getString("NomeProdotto"),
                            rs.getString("NomeAzienda"),
                            rs.getInt("Quantita"),
                            rs.getDate("ProssimaScadenza").toLocalDate()
                    ));
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Errore report giacenze: " + e.getMessage());
        }

        return giacenze;
    }
}


