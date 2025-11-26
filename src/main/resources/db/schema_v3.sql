# Delete old triggers
DELIMITER //

DROP TRIGGER IF EXISTS posts_bu;
//

DROP TRIGGER IF EXISTS comments_bu;
//

DROP TRIGGER IF EXISTS folders_bu;
//

DROP TRIGGER IF EXISTS posts_bi;
//

DROP TRIGGER IF EXISTS comments_bi;
//

DROP TRIGGER IF EXISTS users_bu_block_username_change;
//

DELIMITER ;

# Create the new table for a unique view per day
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
        foreign key (post_id) references forum.posts (post_id) ON DELETE CASCADE,
    constraint posts_users_views_users_user_id_fk
        foreign key (user_id) references forum.users (user_id) ON DELETE CASCADE
);

create index idx_post_views_post_date
    on forum.posts_users_views (post_id, view_date);

create index idx_post_views_user_date
    on forum.posts_users_views (user_id, view_date);



