drop table if exists test1;
drop table if exists test2;

create table test1 (
	 code varchar(100) primary key 
	,description varchar(100) 
);

INSERT INTO test1 VALUES ('A', 'Description A');
INSERT INTO test1 VALUES ('B', 'Description B');

create table test2 (
	 id integer primary key 
	,name varchar(100) 
);

INSERT INTO test2 VALUES (1, 'test1');
INSERT INTO test2 VALUES (2, 'test2');
