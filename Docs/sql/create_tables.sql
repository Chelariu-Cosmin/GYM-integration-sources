--oracle

BEGIN;
CREATE TABLE IF NOT EXISTS public."Abonamente"
(
    id_adonament integer NOT NULL,
    nume character varying NOT NULL,
    descriere character varying,
    pret bigint NOT NULL,
    id_tip_abonament bigint NOT NULL,
    PRIMARY KEY (id_adonament)
);

CREATE TABLE IF NOT EXISTS public."TipAbonament"
(
    id_tip_abonament bigint NOT NULL,
    nume character varying NOT NULL,
    descriere character varying,
    PRIMARY KEY (id_tip_abonament)
);

CREATE TABLE IF NOT EXISTS public."Clienti"
(
    id_client bigint NOT NULL,
    nume character varying NOT NULL,
    prenume character varying NOT NULL,
    email character varying NOT NULL,
    mobil character varying,
    "dataNastere" date,
    id_abonament bigint NOT NULL,
    PRIMARY KEY (id_client)
);

ALTER TABLE IF EXISTS public."Abonamente"
    ADD FOREIGN KEY (id_tip_abonament)
    REFERENCES public."TipAbonament" (id_tip_abonament) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public."Clienti"
    ADD FOREIGN KEY (id_abonament)
    REFERENCES public."Abonamente" (id_adonament) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;

END;