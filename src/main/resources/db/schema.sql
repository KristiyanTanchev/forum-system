
create table tags
(
    tag_id int auto_increment
        primary key,
    name   varchar(50) not null
);

create table users
(
    user_id    int auto_increment
        primary key,
    first_name varchar(32)  not null,
    last_name  varchar(32)  not null,
    username   varchar(50)  not null,
    email      varchar(100) not null,
    password   varchar(100) not null,
    is_admin   tinyint(1)   not null,
    created_at timestamp    not null,
    constraint users_pk_2
        unique (username)
);

create table posts
(
    post_id int auto_increment
        primary key,
    user_id int         not null,
    title   varchar(64) not null,
    content text        not null,
    constraint posts_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

create table comments
(
    comment_id int auto_increment
        primary key,
    post_id    int  not null,
    user_id    int  not null,
    content    text not null,
    constraint comments_posts_post_id_fk
        foreign key (post_id) references posts (post_id),
    constraint comments_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

create table likes
(
    user_id int not null,
    post_id int not null,
    primary key (user_id, post_id),
    constraint likes_users_user_id_fk
        foreign key (user_id) references users (user_id),
    constraint posts_users_posts_post_id__fk
        foreign key (post_id) references posts (post_id)
);

create table tags_posts
(
    tag_id  int not null,
    post_id int not null,
    primary key (tag_id, post_id),
    constraint tags_posts__tag_id_fk
        foreign key (tag_id) references tags (tag_id),
    constraint tags_posts_posts_post_id_fk
        foreign key (post_id) references posts (post_id)
);

