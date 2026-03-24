-- POPOLAMENTO DI TEST
USE farmacia;
-- 1) AZIENDE FORNITRICI
CALL inserisciFornitore('Pfizer','Email','info@pfizer.com','Ippocrate','15','20100');
CALL inserisciFornitore('Angelini','Email','contatti@angelini.it','Amelia','70','00181');
CALL inserisciFornitore('Menarini','Email','supporto@menarini.it','Sette Santi','3','50131');
CALL inserisciFornitore('Bayer','Email','servizio@bayer.it','Europa','10','20100');

-- 2) INDIRIZZI (CAP 5 cifre)
INSERT INTO Indirizzo (Via, Civico, CAP, NomeAzienda) VALUES
                                                          ('Ippocrate', '16', '20100', 'Pfizer'),
                                                          ('Amelia',  '72', '00181', 'Angelini'),
                                                          ('Sette Santi','5', '50131', 'Menarini'),
                                                          ('Europa',    '13', '20100', 'Bayer');

-- 3) METODI CONTATTO (telefono/fax solo numeri 6-15, email valida)
INSERT INTO MetodoContatto (TipoContatto, Valore, NomeAzienda) VALUES
                                                                   ('Telefono','0287654321','Pfizer'),
                                                                   ('Fax','0287654322','Pfizer'),
                                                                   ('Telefono','0644455566','Angelini'),
                                                                   ('Telefono','0551234567','Menarini'),
                                                                   ('Fax','0551234568','Menarini'),
                                                                   ('Telefono', '0211122233','Bayer');

-- 4) SCAFFALI + CASSETTI
INSERT INTO Scaffale (Lettera) VALUES ('A'), ('B'), ('C');

INSERT INTO Cassetto (Numero, LetteraScaffale) VALUES
                                                   (1,'A'),(2,'A'),(3,'A'), (1,'B'),(2,'B'),(3,'B'), (1,'C'),(2,'C');

-- 5) PRODOTTI
-- Parafarmaco/Cosmetico -> MutuabileSSN=0 e PrescrizioneMedica=0
INSERT INTO Prodotto (Nome, NomeAzienda, Tipo, MutuabileSSN, PrescrizioneMedica) VALUES
-- Medicinali
('Tachipirina', 'Angelini', 'Medicinale', 0, 0),
('Moment','Angelini','Medicinale', 0, 0),
('Augmentin','Pfizer','Medicinale', 1, 1),
('Aspirina','Bayer','Medicinale', 0, 0),
-- Parafarmaci
('Gaviscon','Pfizer','Parafarmaco', 0, 0),
('Supradyn','Bayer','Parafarmaco', 0, 0),
-- Cosmetici
('Detergente Viso','Menarini','Cosmetico', 0, 0),
('Crema Mani','Menarini','Cosmetico', 0, 0);

-- 6) INDICAZIONI D'USO
INSERT INTO IndicazioneUso (Indicazione) VALUES
                                             ('Febbre'), ('Dolore'), ('Antibiotico'), ('Acidità di stomaco'),
                                             ('Vitamine e sali minerali'),('Detersione viso'),('Idratazione mani');

-- 7) USO POSSIBILE (relazione prodotto-indicazione)
INSERT INTO UsoPossibile (NomeProdotto, NomeAzienda, Indicazione) VALUES
                                                                      ('Tachipirina', 'Angelini','Febbre'),
                                                                      ('Moment','Angelini','Dolore'),
                                                                      ('Augmentin','Pfizer','Antibiotico'),
                                                                      ('Aspirina','Bayer','Dolore'),
                                                                      ('Gaviscon','Pfizer','Acidità di stomaco'),
                                                                      ('Supradyn','Bayer','Vitamine e sali minerali'),
                                                                      ('Detergente Viso','Menarini','Detersione viso'),
                                                                      ('Crema Mani','Menarini','Idratazione mani');

-- 8) CONFEZIONI
INSERT INTO Confezione (DataScadenza, NomeProdotto, NomeAzienda, NumeroCassetto, LetteraScaffale) VALUES
-- Tachipirina (A1)
('2026-02-05','Tachipirina','Angelini',1,'A'),
('2026-06-30','Tachipirina','Angelini',1,'A'),
-- Moment (A2)
('2026-03-10','Moment','Angelini',2,'A'),
('2026-12-31','Moment','Angelini',2,'A'),
-- Augmentin (B1)
('2026-04-20','Augmentin','Pfizer',1,'B'),
('2026-04-28','Augmentin','Pfizer',1,'B'),
-- Aspirina (B2)
('2026-08-01','Aspirina','Bayer',2,'B'),
('2027-05-01','Aspirina','Bayer',2,'B'),
-- Gaviscon (B3)
('2026-02-15','Gaviscon','Pfizer',3,'B'),
('2026-11-11','Gaviscon','Pfizer',3,'B'),
-- Supradyn (C1)
('2026-02-01','Supradyn','Bayer',1,'C'),
('2026-09-09','Supradyn','Bayer',1,'C'),
-- Cosmetici (C2)
('2027-12-31','Detergente Viso','Menarini',2,'C'),
('2028-12-31','Crema Mani','Menarini',2,'C');

