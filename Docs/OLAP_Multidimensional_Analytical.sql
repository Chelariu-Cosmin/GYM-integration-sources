------ Preparing ---------------------------------------------------------------

DROP VIEW ABONAMENT_VIEW;
DROP VIEW TIP_ABONAMENT_VIEW;
DROP VIEW CLIENTI_VIEW;
DROP VIEW SALI_VIEW;
--------------------------------------------------------------------------------
DROP TABLE tip_abonament
DROP TABLE abonamente
DROP TABLE clienti

DROP TABLE COUNTRY
DROP TABLE ADDRESS
DROP TABLE perioada_valabilitate
--------------------------------------------------------------------------------
-- Data Source Remote/External Views -------------------------------------------
SELECT * FROM ABONAMENT_VIEW;
select * from TIP_ABONAMENT_VIEW;
select * from CLIENTI_VIEW;
select * from SALI_VIEW;
select * from CITIES_VIEW;

--- Cleaning
/*
DROP VIEW OLAP_DIM_CLIENTI_CITIES_SPORT_HALL;

*/
--POSTGRES
-- 1. Total Number of Clients per Subscription Type
SELECT  ta.denumire AS Subscription_Type, COUNT(c.idClient) AS Total_Clients
FROM "clienti" c
JOIN "abonamente" a ON c.idabonament = a.idAbonament
JOIN "tip_abonament" ta ON a.idTipAbonament = ta.idTipAbonament
GROUP BY ta.denumire
ORDER BY TotalClients DESC;

--2 Number of Clients by Month of Birth
SELECT 
    EXTRACT(MONTH FROM c.dataNastere) AS Birth_Month,
    COUNT(c.idClient) AS Total_Clients
FROM 
    "clienti" c
GROUP BY 
    EXTRACT(MONTH FROM c.dataNastere)
ORDER BY 
    BirthMonth;
	
--3. OLAP Function: Rolling Total of Clients by Subscription Type
SELECT 
    ta.denumire AS Subscription_Type,
    COUNT(c.idClient) OVER (ORDER BY ta.denumire) AS Cumulative_Clients
FROM 
    "clienti" c
JOIN 
    "abonamente" a ON c.idabonament = a.idAbonament
JOIN 
    "tip_abonament" ta ON a.idTipAbonament = ta.idTipAbonament;


--- D1: Abonamente - Afisare numarul de abonamente dupa tip
SELECT t."denumire" AS tip_abonament, COUNT(*) AS numar_abonamente
FROM "abonamente"@PostgreSQL35W a
INNER JOIN "tip_abonament"@PostgreSQL35W t ON a."idtipabonament" = t."idtipabonament"
GROUP BY t."denumire";

--- D1: Abonamente - numărul de abonamente pentru fiecare tip de abonament, împreună cu numărul total de abonamente.
WITH total_abonamente AS (
    SELECT COUNT(*) AS total FROM "abonamente"@PostgreSQL35W
),
numar_abonamente_pe_tip AS (
    SELECT 
        t."denumire" AS tip_abonament, 
        COUNT(*) AS numar_abonamente
    FROM "abonamente"@PostgreSQL35W a
    INNER JOIN "tip_abonament"@PostgreSQL35W t ON a."idtipabonament" = t."idtipabonament"
    GROUP BY t."denumire"
)
SELECT 
    n.tip_abonament, 
    n.numar_abonamente,
    t.total AS total_abonamente
FROM numar_abonamente_pe_tip n
CROSS JOIN total_abonamente t;

--- D1: Selecteaza perioada de valabilitate a abonamentului în zile corespunzătoare fiecărui tip de abonament
SELECT 
    t.denumire AS tip_abonament, COUNT(c.idclient) AS numar_clienti  pv.zile_valabilitate AS zile_valabilitate_abonament
FROM "clienti"@PostgreSQL35W c
JOIN "abonamente"@PostgreSQL35W a ON c."idabonament" = a."idabonament"
JOIN "tip_abonament"@PostgreSQL35W t ON a."idtipabonament" = t."idtipabonament"
JOIN PERIOADA_VALABILITATE pv ON t."idtipabonament" = pv."idtipabonament"
GROUP BY t.denumire, pv.zile_valabilitate;

--------------------------------------------------------------------------------------------------------------------------------
--- Dimensions
--- D1: Clients - Cities - SPORT_HALL
--- determina numărul de clienți pentru fiecare combinație de oraș și sală de sport
CREATE OR REPLACE VIEW OLAP_DIM_CLIENTI_CITIES_SPORT_HALL AS
SELECT
    C.city_name,
    S.sport_hall_name,
    COUNT(DISTINCT CS.client_id) AS num_clients
FROM OLAP_DIM_CLIENTI_CITIES_SPORT_HALL CS
JOIN OLAP_DIM_CITY C ON CS.city_id = C.city_id
JOIN OLAP_DIM_SPORT_HALL S ON CS.sport_hall_id = S.sport_hall_id
GROUP BY C.city_name, S.sport_hall_name
ORDER BY C.city_name, S.sport_hall_name;

SELECT * FROM OLAP_DIM_CLIENTI_CITIES_SPORT_HALL;


--------------------------------------------------------------------------------------------------------------------------------
--- D2: Clients - Subscriptions - Subscriptions Type - Validity Period - SPORT_HALL
--- informații despre numărul de clienți pentru fiecare tip abonament si perioada de valabilitate a abonamentului corespunzator salii de sport

SELECT
    TA.nume AS tip_abonament,
    PV.zile_valabilitate AS perioada_valabilitate,
    SV.nume_sala AS sala_sport,
    COUNT(DISTINCT C.id_client) AS numar_clienti
FROM
    CLIENTI_VIEW C
JOIN
    ABONAMENT_VIEW A ON C.idabonament = A.idadonament
JOIN
    TIP_ABONAMENT_VIEW TA ON A.idtipabonament = TA.idtipabonament
JOIN
    perioada_valabilitate PV ON A.id_adonament = PV.perioada_valabilitate_id
JOIN
    sali_view SV ON C.zip = SV.zip
GROUP BY
    TA.nume, PV.zile_valabilitate, SV.nume_sala
ORDER BY
    TA.nume, PV.zile_valabilitate, SV.nume_sala;

SELECT * FROM ABONAMENT_VIEW;
select * from TIP_ABONAMENT_VIEW;
select * from CLIENTI_VIEW;
select * from SALI_VIEW;
select * from CITIES_VIEW;
select * from perioada_valabilitate;


-- REST API

CREATE OR REPLACE FUNCTION post_sql_get_rest_jdbc_data(REST_URL VARCHAR2, SQL_QUERY VARCHAR2,
                                                         DB_URL VARCHAR2, USER_NAME VARCHAR2, 
                                                         PASS VARCHAR2, JDBC_DRIVER VARCHAR2) 
RETURN clob IS 
    l_req utl_http.req; 
    l_resp utl_http.resp; 
    l_buffer clob; 
BEGIN 
    l_req := utl_http.begin_request(REST_URL, 'POST'); 
    utl_http.set_header(l_req, 'Content-Length', length(SQL_QUERY)); 
    utl_http.set_header(l_req, 'Content-Type', 'text/plain'); 
    utl_http.set_body_charset('UTF-8'); 
    utl_http.set_header(l_req, 'DB_URL', DB_URL); 
    utl_http.set_header(l_req, 'USER', USER_NAME); 
    utl_http.set_header(l_req, 'PASS', PASS); 
    utl_http.set_header(l_req, 'JDBC_DRIVER', JDBC_DRIVER); 
    utl_http.WRITE_TEXT(l_req, SQL_QUERY); 
    l_resp := utl_http.get_response(l_req); 
    UTL_HTTP.READ_TEXT(l_resp, l_buffer); 
    utl_http.end_response(l_resp); 
    RETURN l_buffer; 
END;
/

	
GRANT EXECUTE ON SYS.UTL_HTTP TO system;


with rest_doc as
 (SELECT post_sql_get_rest_jdbc_data(
 'http://localhost:8090/data-source-service/jdbc/sql',
 'SELECT idclient, nume, prenume, email, nrtel FROM clienti',
 'jdbc:postgresql://localhost/GymDB',
 'postgres',
 '1234',
 'org.postgresql.Driver'
 ) doc
 from dual)
select x.*
 from rest_doc r,
 XMLTABLE('/results/result'
 PASSING XMLTYPE(r.doc)
 columns
 idclient integer path 'idclient'
 , nume varchar2(20) path 'nume'
 , prenume varchar2(20) path 'prenume'
, email varchar2(20) path 'email'
 , nrtel varchar2(20) path 'nrtel'
 ) x;