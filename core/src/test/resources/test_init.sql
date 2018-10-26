drop table if exists test1;

create table test1 (
	 code varchar(100) primary key 
	,description varchar(100) 
);

INSERT INTO test1 VALUES ('A', 'Description A');
INSERT INTO test1 VALUES ('B', 'Description B');
