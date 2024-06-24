-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
-- alter sequence myentity_seq restart with 4;

insert into Battalion (id,name,description,status,altitude,longitude,latitude) values (1,'Fox Team','fox-team','static',0.0,0.0,0.0);
insert into Battalion (id,name,description,status,altitude,longitude,latitude) values (2,'Hauk Team','hauk-team','static',0.0,0.0,0.0);

insert into Member (id,name,rank,email,battalion) values (1,'Col-01','lieutenant colonel','Col-01@mail.com',1);
insert into Member (id,name,rank,email,battalion) values (2,'Cap-02','captain','Cap-02@mail.com',1);
insert into Member (id,name,rank,email,battalion) values (3,'Cap-03','captain','Cap-03@mail.com',1);
insert into Member (id,name,rank,email,battalion) values (4,'Ser-04','sergeant','Ser-04@mail.com',1);
insert into Member (id,name,rank,email,battalion) values (5,'Ser-05','sergeant','Ser-05@mail.com',1);


insert into Member (id,name,rank,email,battalion) values (6,'Col-016','lieutenant colonel','Col-016@mail.com',2);
insert into Member (id,name,rank,email,battalion) values (7,'Cap-017','captain','Cap-017@mail.com',2);
insert into Member (id,name,rank,email,battalion) values (8,'Cap-018','captain','Cap-018@mail.com',2);
insert into Member (id,name,rank,email,battalion) values (9,'Ser-019','sergeant','Ser-019@mail.com',2);
insert into Member (id,name,rank,email,battalion) values (10,'Ser-020','sergeant','Ser-020@mail.com',2);



insert into equipment (id,name,code,type,status,battalion) values (1,'F16','F16-NL-01','Fixed-Wing','static',1);
insert into equipment (id,name,code,type,status,battalion) values (2,'Apache','Apache-NL-06','Rotary-Wing','static',1);
insert into equipment (id,name,code,type,status,battalion) values (3,'M1127','M1127-NL-08','Reconnaissance','static',1);
insert into equipment (id,name,code,type,status,battalion) values (4,'M1131','M1131-NL-015','Fire-Support','static',1);

insert into equipment (id,name,code,type,status,battalion) values (5,'Apache','Apache-NL-016','Rotary-Wing','static',2);
insert into equipment (id,name,code,type,status,battalion) values (6,'M1127','M1127-NL-017','Reconnaissance','static',2);
insert into equipment (id,name,code,type,status,battalion) values (7,'M1131','M1131-NL-021','Fire-Support','static',2);
insert into equipment (id,name,code,type,status,battalion) values (8,'M1131','M1131-NL-022','Fire-Support','static',2);
