-- Progetto Basi di Dati A.A. 2025/2026
-- Sistema di gestione di una Farmacia

DROP SCHEMA IF EXISTS farmacia;
CREATE SCHEMA farmacia;

-- TABELLE
DROP TABLE IF EXISTS farmacia.AziendaFornitrice;
CREATE TABLE farmacia.AziendaFornitrice (
                                            Nome VARCHAR(50) NOT NULL,
                                            ContattoPrincipale VARCHAR(50) NOT NULL,
                                            IndirizzoFatturazione VARCHAR(100) NOT NULL,
                                            PRIMARY KEY (Nome)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS farmacia.Prodotto;
CREATE TABLE farmacia.Prodotto (
                                   Nome VARCHAR(50) NOT NULL,
                                   NomeAzienda VARCHAR(50) NOT NULL,
                                   Tipo ENUM ('Medicinale', 'Parafarmaco', 'Cosmetico') NOT NULL,
                                   MutuabileSSN BOOLEAN NOT NULL,
                                   PrescrizioneMedica BOOLEAN NOT NULL ,
                                   PRIMARY KEY (Nome, NomeAzienda),
                                   FOREIGN KEY (NomeAzienda)
                                       REFERENCES AziendaFornitrice (Nome)
                                       ON DELETE RESTRICT
                                       ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS farmacia.IndicazioneUso;
CREATE TABLE farmacia.IndicazioneUso (
                                         Indicazione VARCHAR(100) NOT NULL,
                                         PRIMARY KEY (Indicazione)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS farmacia.UsoPossibile;
CREATE TABLE farmacia.UsoPossibile (
                                       NomeProdotto VARCHAR(50) NOT NULL,
                                       NomeAzienda VARCHAR(50) NOT NULL,
                                       Indicazione VARCHAR(100) NOT NULL,
                                       PRIMARY KEY (NomeProdotto, NomeAzienda, Indicazione),
                                       FOREIGN KEY (NomeProdotto, NomeAzienda)
                                           REFERENCES Prodotto (Nome, NomeAzienda)
                                           ON DELETE CASCADE
                                           ON UPDATE CASCADE,
                                       FOREIGN KEY (Indicazione)
                                           REFERENCES IndicazioneUso (Indicazione)
                                           ON DELETE CASCADE
                                           ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS farmacia.Scaffale;
CREATE TABLE farmacia.Scaffale (
                                   Lettera CHAR(1) NOT NULL,
                                   PRIMARY KEY (Lettera)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS farmacia.Cassetto;
CREATE TABLE farmacia.Cassetto (
                                   Numero INT NOT NULL,
                                   LetteraScaffale CHAR(1) NOT NULL,
                                   PRIMARY KEY (Numero, LetteraScaffale),
                                   FOREIGN KEY (LetteraScaffale)
                                       REFERENCES Scaffale (Lettera)
                                       ON DELETE CASCADE
                                       ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS farmacia.Vendita;
CREATE TABLE farmacia.Vendita (
                                  Codice INT NOT NULL AUTO_INCREMENT,
                                  CF CHAR(16),
                                  PRIMARY KEY (Codice)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS farmacia.Confezione;
CREATE TABLE farmacia.Confezione (
                                     Codice INT NOT NULL AUTO_INCREMENT,
                                     DataScadenza DATE NOT NULL,
                                     NomeProdotto VARCHAR(50) NOT NULL,
                                     NomeAzienda VARCHAR(50) NOT NULL,
                                     NumeroCassetto INT NOT NULL,
                                     LetteraScaffale CHAR(1) NOT NULL,
                                     PRIMARY KEY (Codice),
                                     FOREIGN KEY (NomeProdotto, NomeAzienda)
                                         REFERENCES Prodotto (Nome, NomeAzienda)
                                         ON DELETE RESTRICT
                                         ON UPDATE CASCADE,
                                     FOREIGN KEY (NumeroCassetto, LetteraScaffale)
                                         REFERENCES Cassetto (Numero, LetteraScaffale)
                                         ON DELETE RESTRICT
                                         ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS farmacia.VenditaConfezione;
CREATE TABLE farmacia.VenditaConfezione (
                                            CodiceVendita INT NOT NULL,
                                            CodiceConfezione INT NOT NULL,

                                            PRIMARY KEY (CodiceVendita, CodiceConfezione),
                                            UNIQUE (CodiceConfezione),

                                            FOREIGN KEY (CodiceVendita)
                                                REFERENCES Vendita (Codice)
                                                ON DELETE CASCADE,

                                            FOREIGN KEY (CodiceConfezione)
                                                REFERENCES Confezione (Codice)
                                                ON DELETE RESTRICT
);

DROP TABLE IF EXISTS farmacia.Indirizzo;
CREATE TABLE farmacia.Indirizzo (
                                    Via VARCHAR(50) NOT NULL,
                                    Civico VARCHAR(10) NOT NULL,
                                    CAP CHAR(5) NOT NULL,
                                    NomeAzienda VARCHAR(50) NOT NULL,
                                    PRIMARY KEY (Via, Civico, CAP, NomeAzienda),
                                    FOREIGN KEY (NomeAzienda)
                                        REFERENCES AziendaFornitrice (Nome)
                                        ON DELETE CASCADE
                                        ON UPDATE CASCADE
) ENGINE=InnoDB;

DROP TABLE IF EXISTS farmacia.MetodoContatto;
CREATE TABLE farmacia.MetodoContatto (
                                         TipoContatto ENUM('Telefono', 'Fax', 'Email') NOT NULL,
                                         Valore VARCHAR(50) NOT NULL,
                                         NomeAzienda VARCHAR(50) NOT NULL,
                                         PRIMARY KEY (TipoContatto, Valore, NomeAzienda),
                                         FOREIGN KEY (NomeAzienda)
                                             REFERENCES AziendaFornitrice (Nome)
                                             ON DELETE CASCADE
                                             ON UPDATE CASCADE
) ENGINE=InnoDB;

-- INDICI

-- Confezioni in scadenza (report e controlli periodici)
CREATE INDEX idx_confezione_datascadenza
    ON farmacia.Confezione (DataScadenza);

-- Consultazione prodotti per tipologia
CREATE INDEX idx_prodotto_tipo
    ON farmacia.Prodotto (Tipo);

-- Ricerca prodotti per indicazione d'uso
CREATE INDEX idx_usopossibile_indicazione
    ON farmacia.UsoPossibile (Indicazione);

-- STORED PROCEDURES
DELIMITER $$
DROP PROCEDURE IF EXISTS farmacia.registraVendita$$
CREATE PROCEDURE farmacia.registraVendita(
    IN p_CodiceConfezione INT,
    IN p_NomeProdottoScelto VARCHAR(100),
    IN p_AziendaScelta VARCHAR(100),
    IN p_CF CHAR(16),
    IN p_PrescrizionePresentata BOOLEAN,
    IN p_UsaMutuabilita BOOLEAN,
    OUT p_CodiceVenditaGenerato INT
)
BEGIN
    DECLARE v_PrescrizioneRichiesta BOOLEAN;
    DECLARE v_MutuabileSSN BOOLEAN;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
ROLLBACK;
RESIGNAL;
END;

SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

-- Recupero caratteristiche del prodotto e blocco la confezione
SELECT p.PrescrizioneMedica, p.MutuabileSSN
INTO v_PrescrizioneRichiesta, v_MutuabileSSN
FROM Confezione c
         JOIN Prodotto p
              ON p.Nome = c.NomeProdotto
                  AND p.NomeAzienda = c.NomeAzienda
WHERE c.Codice = p_CodiceConfezione
  AND c.NomeProdotto = p_NomeProdottoScelto
  AND c.NomeAzienda = p_AziendaScelta
  AND c.DataScadenza >= CURDATE()
  AND NOT EXISTS (
    SELECT 1
    FROM VenditaConfezione vc
    WHERE vc.CodiceConfezione = c.Codice
)
    FOR UPDATE;

-- Controllo coerenza e disponibilità
IF v_PrescrizioneRichiesta IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
            'Codice errato: confezione non coerente o già venduta';
END IF;

    -- Controllo prescrizione
    IF v_PrescrizioneRichiesta = TRUE
       AND p_PrescrizionePresentata = FALSE THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
            'Prescrizione medica obbligatoria per questo prodotto';
END IF;

    -- Controllo mutuabilità del prodotto
    IF p_UsaMutuabilita = TRUE
       AND v_MutuabileSSN = FALSE THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
            'Il prodotto non è mutuabile SSN';
END IF;

    -- CF obbligatorio se si usa la mutuabilità
    IF p_UsaMutuabilita = TRUE
       AND p_CF IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT =
            'Codice fiscale obbligatorio per vendita mutuabile';
END IF;

    -- ESECUZIONE DELLA VENDITA
INSERT INTO Vendita (CF)
VALUES (p_CF);

SET p_CodiceVenditaGenerato = LAST_INSERT_ID();

INSERT INTO VenditaConfezione (CodiceVendita, CodiceConfezione)
VALUES (p_CodiceVenditaGenerato, p_CodiceConfezione);

COMMIT;
END$$

DROP PROCEDURE IF EXISTS farmacia.visualizzaListaProdotti$$
CREATE PROCEDURE farmacia.visualizzaListaProdotti(
    IN p_SceltaTipo INT, -- 1=Medicinale, 2=Parafarmaco, 3=Cosmetico, 4=Tutti
    IN p_FiltroTipo VARCHAR(50) -- filtro sul nome prodotto, opzionale
)
BEGIN
SELECT
    p.Nome,
    p.NomeAzienda,
    p.Tipo,
    CASE
        WHEN p.MutuabileSSN = 1 THEN 'Sì'
        ELSE 'No'
        END AS Mutuabile,
    CASE
        WHEN p.PrescrizioneMedica = 1 THEN 'Sì'
        ELSE 'No'
        END AS Ricetta,
    GROUP_CONCAT(
            u.Indicazione
                ORDER BY u.Indicazione
            SEPARATOR ', '
    ) AS IndicazioniUso
FROM Prodotto p
         LEFT JOIN UsoPossibile u
                   ON p.Nome = u.NomeProdotto
                       AND p.NomeAzienda = u.NomeAzienda
WHERE
    (p_SceltaTipo = 4 OR
     (p_SceltaTipo = 1 AND p.Tipo = 'Medicinale') OR
     (p_SceltaTipo = 2 AND p.Tipo = 'Parafarmaco') OR
     (p_SceltaTipo = 3 AND p.Tipo = 'Cosmetico'))
  AND (p_FiltroTipo IS NULL
    OR p.Nome LIKE CONCAT('%', p_FiltroTipo, '%'))
GROUP BY
    p.Nome,
    p.NomeAzienda,
    p.Tipo,
    p.MutuabileSSN,
    p.PrescrizioneMedica
ORDER BY
    p.Tipo,
    p.Nome;
END$$

DROP PROCEDURE IF EXISTS farmacia.verificaDisponibilitaProdotto$$
CREATE PROCEDURE farmacia.verificaDisponibilitaProdotto(
    IN p_NomeProdotto VARCHAR(50),
    IN p_NomeAzienda VARCHAR(50)
)
BEGIN
    DECLARE v_Quantita INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
ROLLBACK;
RESIGNAL;
END;

SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

-- Controllo esistenza prodotto
IF NOT EXISTS (
        SELECT 1
        FROM Prodotto
        WHERE Nome = p_NomeProdotto
          AND NomeAzienda = p_NomeAzienda
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Prodotto non esistente';
END IF;

    -- Calcolo quantità disponibile (non vendute e non scadute)
SELECT COUNT(*)
INTO v_Quantita
FROM Confezione c
WHERE c.NomeProdotto = p_NomeProdotto
  AND c.NomeAzienda = p_NomeAzienda
  AND c.DataScadenza >= CURDATE()
  AND NOT EXISTS (
    SELECT 1
    FROM VenditaConfezione vc
    WHERE vc.CodiceConfezione = c.Codice
);

-- PRIMO RESULT SET: quantità
SELECT v_Quantita AS QuantitaDisponibile;

-- SECONDO RESULT SET: lista codici confezione disponibili
SELECT
    c.Codice,
    c.DataScadenza,
    c.LetteraScaffale,
    c.NumeroCassetto
FROM Confezione c
WHERE c.NomeProdotto = p_NomeProdotto
  AND c.NomeAzienda = p_NomeAzienda
  AND c.DataScadenza >= CURDATE()
  AND NOT EXISTS (
    SELECT 1
    FROM VenditaConfezione vc
    WHERE vc.CodiceConfezione = c.Codice
)
ORDER BY c.DataScadenza, c.Codice;
COMMIT;
END$$

DROP PROCEDURE IF EXISTS farmacia.generaReportScadenze$$
CREATE PROCEDURE farmacia.generaReportScadenze(IN p_giorni INT)
BEGIN
    DECLARE done INT DEFAULT FALSE;

    DECLARE v_nomeProdotto VARCHAR(100);
    DECLARE v_dataScadenza DATE;
    DECLARE v_scaffale CHAR(1);
    DECLARE v_cassetto INT;

    DECLARE cur CURSOR FOR
SELECT
    NomeProdotto,
    DataScadenza,
    LetteraScaffale,
    NumeroCassetto
FROM Confezione
WHERE DataScadenza <= DATE_ADD(CURDATE(), INTERVAL p_giorni DAY)
  AND NOT EXISTS (
    SELECT 1
    FROM VenditaConfezione vc
    WHERE vc.CodiceConfezione = Confezione.Codice
)
  AND DataScadenza <= DATE_ADD(CURDATE(), INTERVAL p_giorni DAY)
ORDER BY DataScadenza ASC;

DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
ROLLBACK;
RESIGNAL;
END;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    IF p_giorni IS NULL OR p_giorni <= 0 THEN
        -- sto mettendo di default 7 giorni, report settimanale di default
        SET p_giorni = 7;
END IF;

    DROP TEMPORARY TABLE IF EXISTS ListaScadenze;
    CREATE TEMPORARY TABLE ListaScadenze (
        NomeProdotto VARCHAR(100),
        DataScadenza DATE,
        LetteraScaffale CHAR(1),
        NumeroCassetto INT
    );

    -- Livello di isolamento
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
SET TRANSACTION READ ONLY;
START TRANSACTION;

OPEN cur;
read_loop: LOOP
            FETCH cur INTO v_nomeProdotto, v_dataScadenza, v_scaffale, v_cassetto;
            IF done THEN
                LEAVE read_loop;
END IF;

INSERT INTO ListaScadenze
VALUES (v_nomeProdotto, v_dataScadenza, v_scaffale, v_cassetto);
END LOOP;
CLOSE cur;

SELECT * FROM ListaScadenze;

COMMIT;
END$$

DROP PROCEDURE IF EXISTS farmacia.generaReportGiacenze$$
CREATE PROCEDURE farmacia.generaReportGiacenze()
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
ROLLBACK;
RESIGNAL;
END;

    DROP TEMPORARY TABLE IF EXISTS ListaGiacenze;
    CREATE TEMPORARY TABLE ListaGiacenze (
        NomeProdotto VARCHAR(100),
        NomeAzienda VARCHAR(100),
        Quantita INT,
        ProssimaScadenza DATE
    );

    -- Livello di isolamento
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
SET TRANSACTION READ ONLY;
START TRANSACTION;

INSERT INTO ListaGiacenze
SELECT
    NomeProdotto,
    NomeAzienda,
    COUNT(*) AS Quantita,
    MIN(DataScadenza) AS ProssimaScadenza
FROM Confezione c
WHERE c.DataScadenza >= CURDATE()
  AND NOT EXISTS (
    SELECT 1
    FROM VenditaConfezione vc
    WHERE vc.CodiceConfezione = c.Codice
)
GROUP BY c.NomeProdotto, c.NomeAzienda;

SELECT * FROM ListaGiacenze;

COMMIT;
END$$

DROP PROCEDURE IF EXISTS farmacia.inserisciProdotto$$
CREATE PROCEDURE farmacia.inserisciProdotto(
    IN p_Nome VARCHAR(50),
    IN p_NomeAzienda VARCHAR(50),
    IN p_Tipo ENUM('Medicinale','Parafarmaco','Cosmetico'),
    IN p_MutuabileSSN BOOLEAN,
    IN p_PrescrizioneMedica BOOLEAN
        )
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
ROLLBACK;
RESIGNAL;
END;

SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

IF NOT EXISTS (
        SELECT 1 FROM AziendaFornitrice WHERE Nome = p_NomeAzienda
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Azienda fornitrice inesistente';
END IF;

    IF EXISTS (
        SELECT 1 FROM Prodotto
        WHERE Nome = p_Nome AND NomeAzienda = p_NomeAzienda
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Prodotto già esistente';
END IF;

INSERT INTO Prodotto
VALUES (p_Nome, p_NomeAzienda, p_Tipo, p_MutuabileSSN, p_PrescrizioneMedica);

COMMIT;
END$$


DROP PROCEDURE IF EXISTS farmacia.aggiungiIndicazioneProdotto$$
CREATE PROCEDURE farmacia.aggiungiIndicazioneProdotto(
    IN p_NomeProdotto VARCHAR(50),
    IN p_NomeAzienda VARCHAR(50),
    IN p_Indicazione VARCHAR(100)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
ROLLBACK;
RESIGNAL;
END;

SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

IF NOT EXISTS (
        SELECT 1
        FROM Prodotto
        WHERE Nome = p_NomeProdotto
          AND NomeAzienda = p_NomeAzienda
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Prodotto inesistente';
END IF;

    IF NOT EXISTS (
        SELECT 1 FROM IndicazioneUso
        WHERE Indicazione = p_Indicazione
    ) THEN
        INSERT INTO IndicazioneUso VALUES (p_Indicazione);
END IF;

INSERT INTO UsoPossibile
VALUES (p_NomeProdotto, p_NomeAzienda, p_Indicazione);

COMMIT;
END$$


DROP PROCEDURE IF EXISTS farmacia.inserisciConfezione$$
CREATE PROCEDURE farmacia.inserisciConfezione(
    IN p_DataScadenza DATE,
    IN p_NomeProdotto VARCHAR(50),
    IN p_NomeAzienda VARCHAR(50),
    IN p_NumeroCassetto INT,
    IN p_LetteraScaffale CHAR(1)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
ROLLBACK;
RESIGNAL;
END;

SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

IF p_DataScadenza < CURDATE() THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Confezione già scaduta';
END IF;

    IF NOT EXISTS (
        SELECT 1 FROM Prodotto
        WHERE Nome = p_NomeProdotto AND NomeAzienda = p_NomeAzienda
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Prodotto inesistente';
END IF;

    IF NOT EXISTS (
        SELECT 1 FROM Cassetto
        WHERE Numero = p_NumeroCassetto
          AND LetteraScaffale = p_LetteraScaffale
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Cassetto o Scaffale inesistente';
END IF;

INSERT INTO Confezione (
    DataScadenza, NomeProdotto, NomeAzienda,
    NumeroCassetto, LetteraScaffale
)
VALUES (
           p_DataScadenza, p_NomeProdotto, p_NomeAzienda,
           p_NumeroCassetto, p_LetteraScaffale
       );

COMMIT;
END$$

DROP PROCEDURE IF EXISTS farmacia.inserisciFornitore$$
CREATE PROCEDURE farmacia.inserisciFornitore(
    IN p_Nome VARCHAR(50),
    IN p_TipoContatto ENUM('Telefono', 'Fax', 'Email'),
    IN p_ValoreContatto VARCHAR(50),
    IN p_Via VARCHAR(50),
    IN p_Civico VARCHAR(10),
    IN p_CAP CHAR(5)
        )
BEGIN
    DECLARE v_IndirizzoFatturazione VARCHAR(100);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
ROLLBACK;
RESIGNAL;
END;

    SET v_IndirizzoFatturazione = CONCAT(p_Via, ' ', p_Civico, ', ', p_CAP);

SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

-- CONTROLLO ESISTENZA
IF EXISTS (
        SELECT 1
        FROM AziendaFornitrice
        WHERE Nome = p_Nome
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Fornitore già esistente';
END IF;

    -- 1. Inserimento fornitore
INSERT INTO AziendaFornitrice (Nome, ContattoPrincipale, IndirizzoFatturazione)
VALUES (p_Nome, p_ValoreContatto, v_IndirizzoFatturazione);

-- 2. Contatto
INSERT INTO MetodoContatto (TipoContatto, Valore, NomeAzienda)
VALUES (p_TipoContatto, p_ValoreContatto, p_Nome);

-- 3. Indirizzo
INSERT INTO Indirizzo (Via, Civico, CAP, NomeAzienda)
VALUES (p_Via, p_Civico, p_CAP, p_Nome);

COMMIT;
END$$


DROP PROCEDURE IF EXISTS farmacia.aggiungiContattoFornitore$$
CREATE PROCEDURE farmacia.aggiungiContattoFornitore(
    IN p_NomeAzienda VARCHAR(50),
    IN p_TipoContatto ENUM('Telefono','Fax','Email'),
    IN p_Valore VARCHAR(50)
        )
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
ROLLBACK;
RESIGNAL;
END;

SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

IF NOT EXISTS (
        SELECT 1 FROM AziendaFornitrice WHERE Nome = p_NomeAzienda
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Azienda inesistente';
END IF;

INSERT INTO MetodoContatto
VALUES (p_TipoContatto, p_Valore, p_NomeAzienda);

COMMIT;
END$$

DROP PROCEDURE IF EXISTS farmacia.aggiungiIndirizzoFornitore$$
CREATE PROCEDURE farmacia.aggiungiIndirizzoFornitore(
    IN p_Via VARCHAR(50),
    IN p_Civico VARCHAR(10),
    IN p_CAP CHAR(5),
    IN p_NomeAzienda VARCHAR(50)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
ROLLBACK;
RESIGNAL;
END;

SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

IF NOT EXISTS (
        SELECT 1 FROM AziendaFornitrice WHERE Nome = p_NomeAzienda
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Azienda inesistente';
END IF;

INSERT INTO Indirizzo
VALUES (p_Via, p_Civico, p_CAP, p_NomeAzienda);

COMMIT;
END$$

DROP PROCEDURE IF EXISTS farmacia.login$$
CREATE PROCEDURE farmacia.login (
    IN var_username VARCHAR(45),
    IN var_pass VARCHAR(45),
    OUT var_role INT
)
BEGIN
    DECLARE var_user_role ENUM('farmacia', 'amministratore');

    -- Recupero il ruolo se username/password corretti
SELECT ruolo
INTO var_user_role
FROM Utenti
WHERE username = var_username
  AND password = MD5(var_pass);

-- Mapping verso intero (come nel client)
IF var_user_role = 'amministratore' THEN
        SET var_role = 1;
    ELSEIF var_user_role = 'farmacia' THEN
        SET var_role = 2;
ELSE
        -- credenziali errate
        SET var_role = 3;
END IF;
END$$

DELIMITER ;

-- TRIGGER
DELIMITER $$

DROP TRIGGER IF EXISTS farmacia.check_formato_indirizzo$$
CREATE TRIGGER farmacia.check_formato_indirizzo
    BEFORE INSERT ON Indirizzo
    FOR EACH ROW
BEGIN
    IF NEW.CAP NOT REGEXP '^[0-9]{5}$' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'CAP non valido';
END IF;

IF NEW.Via IS NULL OR NEW.Via = '' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Via obbligatoria';
END IF;

    IF NEW.Civico IS NULL OR NEW.Civico = '' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Civico obbligatorio';
END IF;
END$$

DROP TRIGGER IF EXISTS farmacia.check_formato_metodo_contatto$$
CREATE TRIGGER farmacia.check_formato_metodo_contatto
    BEFORE INSERT ON MetodoContatto
    FOR EACH ROW
BEGIN
    IF NEW.TipoContatto = 'Email' THEN
        IF NEW.Valore NOT LIKE '%_@_%._%' THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Formato email non valido';
END IF;
END IF;

    IF NEW.TipoContatto IN ('Telefono', 'Fax') THEN
        IF NEW.Valore NOT REGEXP '^[0-9]{6,15}$' THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Formato numero non valido';
END IF;
END IF;
END$$

DROP TRIGGER IF EXISTS farmacia.check_formato_data$$
CREATE TRIGGER farmacia.check_formato_data
    BEFORE INSERT ON Confezione
    FOR EACH ROW
BEGIN
    -- Se la data è NULL o precedente a una data minima di sistema
    IF NEW.DataScadenza IS NULL OR YEAR(NEW.DataScadenza) < 2000 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Formato data non valido o data mancante';
END IF;
END$$

DROP TRIGGER IF EXISTS farmacia.check_coerenza_tipo_prodotto$$
CREATE TRIGGER farmacia.check_coerenza_tipo_prodotto
    BEFORE INSERT ON Prodotto
    FOR EACH ROW
BEGIN
    -- Se non è un medicinale, non può richiedere prescrizione o essere mutuabile
    IF NEW.Tipo IN ('Parafarmaco', 'Cosmetico') THEN
        IF NEW.MutuabileSSN = TRUE OR NEW.PrescrizioneMedica = TRUE THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Solo i medicinali possono essere mutuabili o richiedere prescrizione';
END IF;
END IF;
END$$

DELIMITER ;

-- EVENTI
-- NOTA: l'evento richiede event_scheduler = ON
-- SET GLOBAL event_scheduler = ON; (da eseguire dall'amministratore)
DROP EVENT IF EXISTS farmacia.ev_elimina_confezioni_scadute;
DELIMITER $$
CREATE EVENT farmacia.ev_elimina_confezioni_scadute
ON SCHEDULE
    EVERY 1 DAY
STARTS CURRENT_TIMESTAMP
DO
BEGIN
DELETE
FROM Confezione c
WHERE c.DataScadenza < CURDATE()
  AND NOT EXISTS (
    SELECT 1
    FROM VenditaConfezione vc
    WHERE vc.CodiceConfezione = c.Codice
);
END$$
DELIMITER ;

-- UTENTI E PRIVILEGI
DROP TABLE IF EXISTS farmacia.Utenti;
CREATE TABLE farmacia.Utenti (
                                 username VARCHAR(45) NOT NULL,
                                 password CHAR(32) NOT NULL,
                                 ruolo ENUM('farmacia', 'amministratore') NOT NULL,
                                 PRIMARY KEY (username)
) ENGINE=InnoDB;

DROP USER IF EXISTS login;
CREATE USER 'login' IDENTIFIED BY 'login';
GRANT EXECUTE ON PROCEDURE farmacia.login TO 'login';

DROP USER IF EXISTS farmacia;
CREATE USER 'farmacia' IDENTIFIED BY 'farmacia';

GRANT EXECUTE ON PROCEDURE farmacia.login TO 'farmacia';
GRANT EXECUTE ON PROCEDURE farmacia.registraVendita TO 'farmacia';
GRANT EXECUTE ON PROCEDURE farmacia.visualizzaListaProdotti TO 'farmacia';
GRANT EXECUTE ON PROCEDURE farmacia.verificaDisponibilitaProdotto TO 'farmacia';

DROP USER IF EXISTS amministratore;
CREATE USER 'amministratore' IDENTIFIED BY 'amministratore';

GRANT EXECUTE ON PROCEDURE farmacia.login TO 'amministratore';
GRANT EXECUTE ON PROCEDURE farmacia.generaReportScadenze TO 'amministratore';
GRANT EXECUTE ON PROCEDURE farmacia.generaReportGiacenze TO 'amministratore';
GRANT EXECUTE ON PROCEDURE farmacia.inserisciFornitore TO 'amministratore';
GRANT EXECUTE ON PROCEDURE farmacia.inserisciProdotto TO 'amministratore';
GRANT EXECUTE ON PROCEDURE farmacia.inserisciConfezione TO 'amministratore';
GRANT EXECUTE ON PROCEDURE farmacia.aggiungiContattoFornitore TO 'amministratore';
GRANT EXECUTE ON PROCEDURE farmacia.aggiungiIndirizzoFornitore TO 'amministratore';
GRANT EXECUTE ON PROCEDURE farmacia.aggiungiIndicazioneProdotto TO 'amministratore';
