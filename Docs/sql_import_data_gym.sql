-- PREPARE PATH TO FILES

drop directory ext_file_ds;
 
create or replace directory ext_file_ds 
as 'D:\Faculty\Sisteme Informatione de Integrare\GYMDB\sources';
grant all on directory ext_file_ds to public;

select *
  from all_directories;
 
SELECT *
FROM ALL_TAB_PRIVS
WHERE TABLE_NAME = 'EXT_FILE_DS';

SELECT * 
FROM DBA_DIRECTORIES 
WHERE DIRECTORY_NAME = 'EXT_FILE_DS';

-- CSV
 
drop table perioada_valabilitate;
create table perioada_valabilitate (
    id_perioada_valabilitate integer,
    id_tip_abonament    integer,
    zile_valabilitate integer
)
organization external ( type oracle_loader
    default directory ext_file_ds access parameters (
        records delimited by newline
            skip 1
        fields terminated by ';' missing field values are null
    ) location ( 'perioada_valabilitate.csv' )
) reject limit unlimited;
 
-- Display Data
select * from perioada_valabilitate;


-- XML
drop view sali_view;

create or replace view sali_view as
  SELECT x.idsala,
         x.nume_sala,
         x.address_id
  FROM XMLTABLE (
          '/salile/sala'
          PASSING XMLTYPE(
                  BFILENAME('EXT_FILE_DS','sali.xml'),
                  NLS_CHARSET_ID('AL32UTF8') 
          )
          COLUMNS
                  idsala INTEGER PATH 'idsala',
                  nume_sala VARCHAR2(8) PATH 'nume_sala',
                  address_id INTEGER PATH 'address_id'
  ) x;
 
-- Display Data
select * from sali_view;


 -- PREPARE LINK TO POSTGRES

rollback;
alter session close database link PostgreSQL35W;
 
drop database link PostgreSQL35W;
 
create database link PostgreSQL35W
    connect to "postgres" identified by "1234"
using '(DESCRIPTION =
    (ADDRESS = (PROTOCOL = TCP)(HOST = localhost)(PORT = 1521))
    (CONNECT_DATA =
      (SID = PG)
    )
    (HS = OK)
    )';
 
select * from user_db_links;
 

-- CLIENT VIEW

create or replace view client_view as
select * 
from "clienti"@PostgreSQL35W;


select *
from client_view;


-- ABONAMENT VIEW

create or replace view abonament_view as
select *
from "abonamente"@PostgreSQL35W;

select *
from abonament_view;


-- TIP ABONAMENT VIEW

create or replace view tip_abonament_view as
select *
from "tip_abonament"@PostgreSQL35W;

select *
from tip_abonament_view;


--JSON
--- Strategy_1: External VIEWS -------------------------------------------------
--- Function version for large JSON Documents

-- JSON
drop table country;
drop table address;
drop function read_json_from_file

create table country (
    country_id   number,
    country_name varchar(200)
);

create table address (
    address_id    number,
    street_number varchar(10),
    street_name   varchar(200),
    city          varchar(100),
    country_id    number
);


create or replace function read_json_from_file (
    p_directory_name in varchar2,  
    p_file_name      in varchar2  
) return clob is
    v_bfile      bfile;
    v_blob       blob;
    v_amount     integer;
    v_offset     integer := 1;
    v_chunk_size integer := 32767; 
    v_raw        raw(32767);
    v_clob       clob;
begin
    v_bfile  := bfilename(
                        p_directory_name,
                        p_file_name
               );
    dbms_lob.fileopen(
                     v_bfile,
                     dbms_lob.file_readonly
    );
    dbms_lob.createtemporary(
                            v_blob,
                            true
    );
    v_amount := dbms_lob.getlength(v_bfile);

    while v_offset <= v_amount loop
        dbms_lob.read(
                     v_bfile,
                     v_chunk_size,
                     v_offset,
                     v_raw
        );
        v_clob   := v_clob || utl_raw.cast_to_varchar2(v_raw);
        v_offset := v_offset + v_chunk_size;
    end loop;
    
    dbms_lob.fileclose(v_bfile);
    return v_clob;
exception
    when others then
        dbms_output.put_line('Error: ' || sqlerrm);
        return null;
end;
/

create or replace procedure insert_country_json_data (
    p_json_clob in out clob
) as
    v_country_id      number;
    v_country_name    varchar(200);
    v_offset          integer := 1;  -- Starting offset for search
    v_country_pattern varchar2(100) := '"country_id":[0-9]+,"country_name":"[^"]+"';
begin
    -- Loop to extract all occurrences of country_id and country_name
    while v_offset > 0 loop
        -- Extract country_id
        v_country_id   := to_number ( extract_number(
            regexp_substr(
                p_json_clob,'"country_id":[0-9]+',v_offset,1,'i'
            )
        ) );
        
        -- Extract country_name
        v_country_name := regexp_replace(
                                        regexp_substr(
                                                     p_json_clob,
                                                     '"country_name":"[^"]+"',
                                                     v_offset,
                                                     1,
                                                     'i'
                                        ),
                                        '("country_name":")|("$)',
                                        ''
                          );

        -- Check if country_id and country_name are found
        if
            v_country_id is not null
            and v_country_name is not null
        then
            -- Insert data into the table
            insert into country (
                country_id,
                country_name
            ) values (
                v_country_id,
                v_country_name
            );
            dbms_output.put_line('Data inserted successfully.');
        else
            dbms_output.put_line('Error: Unable to parse JSON data.');
        end if;

        -- Replace the extracted substring with empty string in the JSON clob
        p_json_clob    := regexp_replace(
                                     p_json_clob,
                                     v_country_pattern,
                                     '',
                                     1,
                                     1,
                                     'i'
                       );

        -- Find the position of the next occurrence of the country pattern
        v_offset       := regexp_instr(
                                p_json_clob,
                                v_country_pattern,
                                v_offset,
                                1,
                                0,
                                'i'
                    );
    end loop;
exception
    when others then
        dbms_output.put_line('Error: ' || sqlerrm);
end;
/

create or replace procedure insert_address_json_data (
    p_json_clob in out clob
) as
    v_address_id      number;
    v_street_number   varchar(10);
    v_street_name     varchar(200);
    v_city            varchar(100);
    v_country_id      number;
    v_offset          integer := 1;  -- Starting offset for search
    v_address_pattern varchar2(200) := '"address_id":[0-9]+,"street_number":"[^"]+","street_name":"[^"]+","city":"[^"]+","country_id":[0-9]+'
    ;
begin
    -- Loop to extract all occurrences of address data
    while v_offset > 0 loop
        -- Extract address_id
        v_address_id    := to_number ( extract_number(
            regexp_substr(
                p_json_clob,'"address_id":[0-9]+',v_offset,1,'i'
            )
        ) );
        
        -- Extract street_number
        v_street_number := regexp_replace(
                                         regexp_substr(
                                                      p_json_clob,
                                                      '"street_number":"[^"]+"',
                                                      v_offset,
                                                      1,
                                                      'i'
                                         ),
                                         '("street_number":")|("$)',
                                         ''
                           );
        
        -- Extract street_name
        v_street_name   := regexp_replace(
                                       regexp_substr(
                                                    p_json_clob,
                                                    '"street_name":"[^"]+"',
                                                    v_offset,
                                                    1,
                                                    'i'
                                       ),
                                       '("street_name":")|("$)',
                                       ''
                         );
        
        -- Extract city
        v_city          := regexp_replace(
                                regexp_substr(
                                             p_json_clob,
                                             '"city":"[^"]+"',
                                             v_offset,
                                             1,
                                             'i'
                                ),
                                '("city":")|("$)',
                                ''
                  );
        
        -- Extract country_id
        v_country_id    := to_number ( extract_number(
            regexp_substr(
                p_json_clob,'"country_id":[0-9]+',v_offset,1,'i'
            )
        ) );

        -- Check if all address fields are found
        if
            v_address_id is not null
            and v_street_number is not null
            and v_street_name is not null
            and v_city is not null
            and v_country_id is not null
        then
            -- Insert data into the table
            insert into address (
                address_id,
                street_number,
                street_name,
                city,
                country_id
            ) values (
                v_address_id,
                v_street_number,
                v_street_name,
                v_city,
                v_country_id
            );
            dbms_output.put_line('Address data inserted successfully.');
        else
            dbms_output.put_line('Error: Unable to parse address data.');
        end if;

        -- Replace the extracted address data with empty string in the JSON clob
        p_json_clob     := regexp_replace(
                                     p_json_clob,
                                     v_address_pattern,
                                     '',
                                     1,
                                     1,
                                     'i'
                       );

        -- Find the position of the next occurrence of the address pattern
        v_offset        := regexp_instr(
                                p_json_clob,
                                v_address_pattern,
                                v_offset,
                                1,
                                0,
                                'i'
                    );
    end loop;
exception
    when others then
        dbms_output.put_line('Error: ' || sqlerrm);
end;
/


-- Execute the procedure to insert country JSON data 
declare
    v_json_clob clob;
begin
    v_json_clob := read_json_from_file(
                                      'EXT_FILE_DS',
                                      'country_address.json'
                   );
    insert_country_json_data(v_json_clob);
    commit;
end;
/

-- Execute the procedure to insert address JSON data 
declare
    v_json_clob clob;
begin
    v_json_clob := read_json_from_file(
                                      'EXT_FILE_DS',
                                      'country_address.json'
                   );
    insert_address_json_data(v_json_clob);
    commit;
end;
/
select * from address

select * from country
