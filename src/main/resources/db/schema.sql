create table forum.folders
(
    folder_id   int auto_increment
        primary key,
    parent_id   int                                   null,
    name        varchar(64)                           not null,
    slug        varchar(64)                           not null,
    created_at  timestamp default current_timestamp() not null,
    updated_at  datetime                              null,
    description varchar(255)                          null,
    imdb_id     varchar(30)                           null,
    constraint folders_sibling_slug_uq
        unique (parent_id, slug),
    constraint folders_parent_fk
        foreign key (parent_id) references forum.folders (folder_id),
    constraint folders_name_len_chk
        check (char_length(`name`) between 1 and 64),
    constraint folders_slug_chk
        check (`slug` regexp '^[a-z0-9]+(?:-[a-z0-9]+)*$'),
    constraint folders_slug_lower_chk
        check (cast(`slug` as char charset binary) = lcase(`slug`))
);

create index idx_folders_parent
    on forum.folders (parent_id);

create table forum.media_data
(
    imdb_id       varchar(30)  not null
        primary key,
    title         varchar(100) null,
    year          varchar(30)  null,
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

create table forum.tags
(
    tag_id int auto_increment
        primary key,
    name varchar(50) not null,
    constraint tags_name_uq
        unique (name),
    constraint tags_lower_chk
        check (cast(`name` as char charset binary) = lcase(`name`))
);

create table forum.users
(
    user_id    int auto_increment
        primary key,
    first_name varchar(32)                             not null,
    last_name  varchar(32)                             not null,
    username   varchar(50)                             not null,
    email      varchar(100)                            not null,
    password   varchar(100)                            not null,
    created_at timestamp   default current_timestamp() not null,
    phone      varchar(32)                             null,
    avatar_url varchar(512)                            null,
    is_blocked tinyint(1)  default 0                   not null,
    is_deleted tinyint(1)  default 0                   not null,
    role       varchar(20) default 'USER'              not null,
    constraint users_email_uq
        unique (email),
    constraint users_pk_2
        unique (username),
    constraint chk_email_format
        check (trim(`email`) regexp '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$'),
    constraint chk_first_name_length
        check (char_length(`first_name`) between 4 and 32),
    constraint chk_last_name_length
        check (char_length(`last_name`) between 4 and 32),
    constraint chk_username_character_length_type
        check (`username` regexp '^[A-Za-z][A-Za-z0-9._-]{3,49}$')
);

create table forum.posts
(
    post_id    int auto_increment
        primary key,
    user_id    int                                    not null,
    title      varchar(64)                            not null,
    content    text                                   not null,
    created_at timestamp  default current_timestamp() not null,
    updated_at datetime                               null,
    deleted_at datetime                               null,
    is_deleted tinyint(1) default 0                   not null,
    folder_id int not null,
    constraint posts_folders_folder_id_fk
        foreign key (folder_id) references forum.folders (folder_id),
    constraint posts_users_user_id_fk
        foreign key (user_id) references forum.users (user_id),
    constraint chk_post_content_length
        check (char_length(`content`) between 32 and 8192),
    constraint chk_post_title_length
        check (char_length(`title`) between 16 and 64)
);

create table forum.comments
(
    comment_id int auto_increment
        primary key,
    post_id    int                                    not null,
    user_id    int                                    not null,
    content    text                                   not null,
    created_at timestamp  default current_timestamp() not null,
    updated_at datetime                               null,
    deleted_at datetime                               null,
    is_deleted tinyint(1) default 0                   not null,
    constraint comments_posts_post_id_fk
        foreign key (post_id) references forum.posts (post_id)
            on delete cascade,
    constraint comments_users_user_id_fk
        foreign key (user_id) references forum.users (user_id),
    constraint chk_comment_content_length
        check (char_length(`content`) between 1 and 8192)
);

create table forum.comment_likes
(
    comment_id int not null,
    user_id    int not null,
    primary key (comment_id, user_id),
    constraint comment_likes_ibfk_1
        foreign key (comment_id) references forum.comments (comment_id),
    constraint comment_likes_ibfk_2
        foreign key (user_id) references forum.users (user_id)
);

create index user_id
    on forum.comment_likes (user_id);

create index idx_comments_post_created
    on forum.comments (post_id, created_at);

create index idx_comments_user_created
    on forum.comments (user_id, created_at);

create table forum.likes
(
    user_id int not null,
    post_id int not null,
    primary key (user_id, post_id),
    constraint likes_users_user_id_fk
        foreign key (user_id) references forum.users (user_id),
    constraint posts_users_posts_post_id__fk
        foreign key (post_id) references forum.posts (post_id)
            on delete cascade
);

create index idx_likes_post
    on forum.likes (post_id);

create index idx_posts_folder_created
    on forum.posts (folder_id, created_at);

create index idx_posts_user_created
    on forum.posts (user_id, created_at);

create table forum.posts_users_views
(
    posts_users_views_id int auto_increment
        primary key,
    user_id              int  not null,
    post_id              int  not null,
    view_date            date not null,
    constraint uq_posts_users_views_unique
        unique (post_id, user_id, view_date),
    constraint posts_users_views_posts_post_id_fk
        foreign key (post_id) references forum.posts (post_id)
            on delete cascade,
    constraint posts_users_views_users_user_id_fk
        foreign key (user_id) references forum.users (user_id)
            on delete cascade
);

create index idx_post_views_post_date
    on forum.posts_users_views (post_id, view_date);

create index idx_post_views_user_date
    on forum.posts_users_views (user_id, view_date);

create table forum.tags_posts
(
    tag_id  int not null,
    post_id int not null,
    primary key (tag_id, post_id),
    constraint tags_posts__tag_id_fk
        foreign key (tag_id) references forum.tags (tag_id)
            on delete cascade,
    constraint tags_posts_posts_post_id_fk
        foreign key (post_id) references forum.posts (post_id)
            on delete cascade
);

create index idx_tags_posts_post
    on forum.tags_posts (post_id);

create index idx_users_first_name
    on forum.users (first_name);

create index idx_users_role
    on forum.users (role);

