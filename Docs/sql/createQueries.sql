--POSTGRES
-- Creare tabela Tip_abonament
CREATE TABLE Tip_abonament (
    idTipAbonament SERIAL PRIMARY KEY, --se va lega cu fisierul CSV cu valabilitatile
    denumire VARCHAR(50) NOT NULL,
    descriere TEXT
);

-- Creare tabela Abonamente
CREATE TABLE Abonamente (
    idAbonament SERIAL PRIMARY KEY,
    numeAbonament VARCHAR(100) NOT NULL,
    pret NUMERIC(10, 2) NOT NULL,
    idTipAbonament INT REFERENCES Tip_abonament(idTipAbonament)
);

-- Creare tabela Clienti
CREATE TABLE Clienti (
    idClient SERIAL PRIMARY KEY,
    Nume VARCHAR(100) NOT NULL,
    Prenume VARCHAR(100) NOT NULL,
    dataNastere DATE NOT NULL,
    email VARCHAR(100) NOT NULL,
    nrTel VARCHAR(20) NOT NULL,
    idAbonament INT REFERENCES Abonamente(idAbonament),
    idsala INT -- FK: idSala (va fi legat de fisierul sali.xml)
);
