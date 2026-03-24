package it.uniroma2.dicii.bd.model.dao;

import it.uniroma2.dicii.bd.exception.DAOException;
import it.uniroma2.dicii.bd.model.domain.Prodotto;
import it.uniroma2.dicii.bd.model.dto.ProdottoSearchRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VisualizzaListaProdottiProcedureDAO
        implements GenericProcedureDAO<ProdottoSearchRequest, List<Prodotto>> {

    @Override
    public List<Prodotto> execute(ProdottoSearchRequest input) throws DAOException {
        List<Prodotto> result = new ArrayList<>();
        try {
            Connection connection = ConnectionFactory.getConnection();
            CallableStatement cs = connection.prepareCall("{call visualizzaListaProdotti(?,?)}");
            cs.setInt(1, input.sceltaTipo());
            cs.setString(2, input.filtroNome());

                try (ResultSet rs = cs.executeQuery()) {
                    while (rs.next()) {
                        result.add(new Prodotto(
                                rs.getString("Nome"),
                                rs.getString("Tipo"),
                                rs.getString("NomeAzienda"),
                                rs.getString("Mutuabile"),
                                rs.getString("Ricetta"),
                                rs.getString("IndicazioniUso")
                        ));
                    }
                }
                return result;
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
    }
}

