DROP TABLE T_ROE_EXCHANGE_RATE;

CREATE TABLE T_ROE_EXCHANGE_RATE (
    "CREATE_ID" NUMBER PRIMARY KEY,
    "DELETE_ID" NUMBER DEFAULT 0 NOT NULL,
    "TONUC" string,
    "FROMCURR" string,
    "EFFDT" DATE,
    "DISDT" DATE,
    "CONVTYPE" CHAR(2),
    "CONVRATE" FLOAT(126),
    "CREATE_TIME" DATE
);

select
    *
from
    test;
	
	
create table test(id string, name string);

select * from t_