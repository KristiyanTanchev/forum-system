USE forum;

ALTER TABLE folders
    ADD COLUMN imdb_id VARCHAR(30);

create table media_data
(
    imdb_id       varchar(30)  not null
        primary key,
    title         varchar(100) null,
    year          int          null,
    release_date  date         null,
    genres        varchar(100) null,
    plot          text         null,
    language      varchar(100) null,
    country       varchar(100) null,
    poster        varchar(255) null,
    imdb_rating   float        null,
    type          varchar(30)  null,
    total_seasons int          null
);

