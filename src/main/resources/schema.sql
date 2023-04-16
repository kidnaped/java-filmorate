drop table if exists FILM, FILM_GENRE, FILM_LIKED, GENRE, MPA, APP_USER, FRIENDSHIP;

create table if not exists MPA
(
    MPA_ID   INTEGER auto_increment
        primary key,
    MPA_NAME CHARACTER VARYING(8) not null
);

create table if not exists FILM
(
    FILM_ID      INTEGER auto_increment
        primary key,
    FILM_NAME    CHARACTER VARYING(24)  not null,
    DESCRIPTION  CHARACTER VARYING(256) not null,
    RELEASE_DATE DATE                   not null,
    DURATION     INTEGER                not null,
    MPA          CHARACTER VARYING(10)  not null
);

create table if not exists APP_USER
(
    USER_ID   INTEGER auto_increment
        primary key,
    EMAIL     CHARACTER VARYING(24) not null,
    LOGIN     CHARACTER VARYING(24) not null,
    USER_NAME CHARACTER VARYING(24),
    BIRTHDAY  DATE                  not null
);

create table if not exists GENRE
(
    GENRE_ID   INTEGER auto_increment
        primary key,
    GENRE_NAME CHARACTER VARYING(24) not null
);

create table if not exists FILM_GENRE
(
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER not null,
    constraint FILM_GENRE_PK
        primary key (FILM_ID, GENRE_ID),
    constraint FILM_GENRE_FILM_ID_FK
        foreign key (FILM_ID) references FILM,
    constraint FILM_GENRE_GENRE_ID_FK
        foreign key (GENRE_ID) references GENRE
);

create table if not exists FILM_LIKED
(
    FILM_LIKE_ID INTEGER auto_increment
        primary key,
    FILM_ID      INTEGER not null
        references FILM,
    USER_ID      INTEGER not null
        references APP_USER
);

create table if not exists FRIENDSHIP
(
    USER_ID   INTEGER not null
        references APP_USER,
    FRIEND_ID INTEGER not null
        references APP_USER,
    primary key (USER_ID, FRIEND_ID)
);