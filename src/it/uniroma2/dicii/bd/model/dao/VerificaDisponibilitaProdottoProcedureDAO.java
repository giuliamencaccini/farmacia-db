package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.domain.ConfezioneDisponibile;
import it.uniroma2.dicii.bd.model.dto.DisponibilitaProdottoRequest;
import it.uniroma2.dicii.bd.model.dto.DisponibilitaProdottoResult;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VerificaDisponibilitaProdottoProcedureDAO
        implements GenericProcedureDAO<DisponibilitaProdottoRequest, DisponibilitaProdottoResult> {

    @Override
    public DisponibilitaProdottoResult execute(DisponibilitaProdottoRequest input) throws DAOException {
        try {
            Connection connection = ConnectionFactory.getConnection();
            CallableStatement cs = connection.prepareCall( "{call verificaDisponibilitaProdotto(?,?)}");
            cs.setString(1, input.nomeProdotto());
            cs.setString(2, input.nomeAzienda());

            cs.execute();

            //RESULT SET 1: quantità
            int quantita = 0;
            try (ResultSet rs1 = cs.getResultSet()) {
                if (rs1 != null && rs1.next()) {
                    quantita = rs1.getInt("QuantitaDisponibile");
                }
            }

            //RESULT SET 2: confezioni
            List<ConfezioneDisponibile> confezioni = new ArrayList<>();
            if (cs.getMoreResults()) {
                try (ResultSet rs2 = cs.getResultSet()) {
                    while (rs2 != null && rs2.next()) {
                        confezioni.add(
                                new ConfezioneDisponibile(
                                        rs2.getInt("Codice"),
                                        rs2.getDate("DataScadenza").toLocalDate(),
                                        rs2.getString("LetteraScaffale"),
                                        rs2.getInt("NumeroCassetto")
                                )
                        );
                    }
                }
            }

            return new DisponibilitaProdottoResult(quantita, confezioni);
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
    }
}

