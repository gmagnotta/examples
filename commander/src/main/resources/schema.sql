alter table if exists Equipment drop constraint if exists FK35o6ksxn1jotjfay2i6b4175o;

alter table if exists Member drop constraint if exists FKjvj63dq23ec003g3bot1hq5yd;

drop table if exists Battalion cascade;

drop table if exists Equipment cascade;

drop table if exists Member cascade;

drop sequence if exists battalionSequence;

drop sequence if exists equipmentSequence;

drop sequence if exists memberSequence;

create sequence battalionSequence start with 100 increment by 50;

create sequence equipmentSequence start with 100 increment by 50;

create sequence memberSequence start with 100 increment by 50;

create table Battalion (
    altitude float(53) not null,
    latitude float(53) not null,
    longitude float(53) not null,
    id bigint not null,
    description varchar(255),
    name varchar(255),
    status varchar(255),
    primary key (id)
);

create table Equipment (
    battalion bigint,
    id bigint not null,
    code varchar(255),
    name varchar(255),
    status varchar(255),
    type varchar(255),
    primary key (id)
);

create table Member (
    battalion bigint,
    id bigint not null,
    email varchar(255) unique,
    name varchar(255),
    rank varchar(255),
    primary key (id)
);

alter table if exists Equipment 
    add constraint FK35o6ksxn1jotjfay2i6b4175o 
    foreign key (battalion) 
    references Battalion;

alter table if exists Member 
    add constraint FKjvj63dq23ec003g3bot1hq5yd 
    foreign key (battalion) 
    references Battalion;