#  --- User changes ---
ALTER TABLE users
    MODIFY is_admin TINYINT(1) NOT NULL DEFAULT 0,
    MODIFY created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;


#Add is_blocked, is_deleted
ALTER TABLE users
    ADD phone VARCHAR(32) NULL,
    ADD avatar_url VARCHAR(512) NULL,
    ADD is_blocked BOOLEAN DEFAULT FALSE NOT NULL,
    ADD is_deleted BOOLEAN DEFAULT FALSE NOT NULL;

#Validations
ALTER TABLE users
    ADD CONSTRAINT chk_username_character_length_type
        CHECK (username REGEXP '^[A-Za-z][A-Za-z0-9._-]{3,49}$'),
    ADD CONSTRAINT chk_first_name_length
        CHECK (CHAR_LENGTH(first_name) BETWEEN 4 AND 32),
    ADD CONSTRAINT chk_last_name_length
        CHECK (CHAR_LENGTH(last_name) BETWEEN 4 AND 32),
    ADD CONSTRAINT users_email_uq
        UNIQUE (email),
    ADD CONSTRAINT chk_email_format
        CHECK (TRIM(email) REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$');


#  --- Comment changes ---
ALTER TABLE comments
    ADD created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD updated_at DATETIME  NULL,
    ADD deleted_at DATETIME  NULL,
    ADD is_deleted BOOLEAN   NOT NULL DEFAULT FALSE;

ALTER TABLE comments
    ADD CONSTRAINT chk_comment_content_length
        CHECK (CHAR_LENGTH(content) BETWEEN 1 AND 8192);


#  --- Post changes ---
ALTER TABLE posts
    ADD created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD updated_at DATETIME  NULL,
    ADD deleted_at DATETIME  NULL,
    ADD is_deleted BOOLEAN   NOT NULL DEFAULT FALSE;

ALTER TABLE posts
    ADD CONSTRAINT chk_post_title_length
        CHECK (CHAR_LENGTH(title) BETWEEN 16 AND 64),
    ADD CONSTRAINT chk_post_content_length
        CHECK (CHAR_LENGTH(content) BETWEEN 32 AND 8192);

#  --- Tags changes ---
ALTER TABLE tags_posts
    DROP FOREIGN KEY tags_posts__tag_id_fk;

ALTER TABLE tags_posts
    ADD CONSTRAINT tags_posts__tag_id_fk
        FOREIGN KEY (tag_id) REFERENCES tags(tag_id) ON DELETE CASCADE;


ALTER TABLE tags
    ADD CONSTRAINT tags_name_uq UNIQUE (name),
    ADD CONSTRAINT tags_lower_chk CHECK (BINARY name = LOWER(name));


#  --- Cascades ---
ALTER TABLE comments
    DROP FOREIGN KEY comments_posts_post_id_fk;

ALTER TABLE comments
    ADD CONSTRAINT comments_posts_post_id_fk
        FOREIGN KEY (post_id) REFERENCES posts (post_id) ON DELETE CASCADE;

ALTER TABLE likes
    DROP FOREIGN KEY posts_users_posts_post_id__fk;

ALTER TABLE likes
    ADD CONSTRAINT posts_users_posts_post_id__fk
        FOREIGN KEY (post_id) REFERENCES posts (post_id) ON DELETE CASCADE;

ALTER TABLE tags_posts
    DROP FOREIGN KEY tags_posts_posts_post_id_fk;

ALTER TABLE tags_posts
    ADD CONSTRAINT tags_posts_posts_post_id_fk
        FOREIGN KEY (post_id) REFERENCES posts (post_id) ON DELETE CASCADE;


#  --- Folders ---
CREATE TABLE folders
(
    folder_id  INT AUTO_INCREMENT PRIMARY KEY,
    parent_id  INT         NULL,
    name       VARCHAR(64) NOT NULL,
    slug       VARCHAR(64) NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME    NULL,

    CONSTRAINT folders_sibling_slug_uq UNIQUE (parent_id, slug),

    CONSTRAINT folders_name_len_chk CHECK (CHAR_LENGTH(name) BETWEEN 1 AND 64),
    CONSTRAINT folders_slug_lower_chk CHECK (BINARY slug = LOWER(slug)),
    CONSTRAINT folders_slug_chk CHECK (slug REGEXP '^[a-z0-9]+(?:-[a-z0-9]+)*$'),

    CONSTRAINT folders_parent_fk
        FOREIGN KEY (parent_id) REFERENCES folders (folder_id)
            ON DELETE RESTRICT
);


# --- POSTS: exactly one folder per post
ALTER TABLE posts
    ADD folder_id INT NOT NULL;

ALTER TABLE posts
    ADD CONSTRAINT posts_folders_folder_id_fk
        FOREIGN KEY (folder_id) REFERENCES folders (folder_id) ON DELETE RESTRICT ;


# --- Update triggers ---
DELIMITER //
CREATE TRIGGER posts_bu BEFORE UPDATE ON posts
    FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END//
CREATE TRIGGER comments_bu BEFORE UPDATE ON comments
    FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END//
CREATE TRIGGER folders_bu BEFORE UPDATE ON folders
    FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END//
DELIMITER ;

#  --- Blocked users not allowed to post/comment ---
DELIMITER //
CREATE TRIGGER posts_bi BEFORE INSERT ON posts
    FOR EACH ROW BEGIN
    IF (SELECT is_blocked FROM users WHERE user_id = NEW.user_id) = 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Blocked users cannot create posts';
    END IF;
END//
CREATE TRIGGER comments_bi BEFORE INSERT ON comments
    FOR EACH ROW BEGIN
    IF (SELECT is_blocked FROM users WHERE user_id = NEW.user_id) = 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Blocked users cannot comment';
    END IF;
END//
DELIMITER ;

#  --- Username must be immutable ---
DELIMITER //
CREATE TRIGGER users_bu_block_username_change
    BEFORE UPDATE ON users
    FOR EACH ROW
BEGIN
    IF NEW.username <> OLD.username THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Username cannot be changed';
    END IF;
END//
DELIMITER ;


#  --- Indexes ---
-- user search
CREATE INDEX idx_users_first_name   ON users (first_name);

-- listings/sorting
CREATE INDEX idx_posts_user_created     ON posts (user_id, created_at);
CREATE INDEX idx_comments_post_created  ON comments (post_id, created_at);
CREATE INDEX idx_comments_user_created  ON comments (user_id, created_at);
CREATE INDEX idx_likes_post             ON likes (post_id);
CREATE INDEX idx_tags_posts_post        ON tags_posts (post_id);

-- folders tree
CREATE INDEX idx_folders_parent         ON folders (parent_id);
CREATE INDEX idx_posts_folder_created   ON posts (folder_id, created_at);

