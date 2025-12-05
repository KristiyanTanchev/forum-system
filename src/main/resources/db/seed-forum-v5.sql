USE forum;

-- ============================================
-- OPTIONAL: CLEAR EXISTING DATA FOR A CLEAN SEED
-- ============================================
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE forum.comment_likes;
TRUNCATE TABLE forum.likes;
TRUNCATE TABLE forum.posts_users_views;
TRUNCATE TABLE forum.tags_posts;
TRUNCATE TABLE forum.comments;
TRUNCATE TABLE forum.posts;
TRUNCATE TABLE forum.tags;
TRUNCATE TABLE forum.folders;
TRUNCATE TABLE forum.users;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- ROOT + MOVIES + SERIES FOLDERS
-- ============================================
INSERT INTO forum.folders (folder_id, parent_id, name, slug, created_at)
VALUES (1, NULL, 'Root', 'root', '2023-01-01 10:00:00'),
       (2, 1, 'Movies', 'movies', '2023-01-03 10:00:00'),
       (3, 1, 'Series', 'series', '2023-01-03 10:05:00');

-- 15 movie folders under Movies (folder_id = 2)
INSERT INTO forum.folders (folder_id, parent_id, name, slug, created_at)
VALUES (4, 2, 'The Matrix', 'the-matrix', '2023-01-10 09:00:00'),
       (5, 2, 'Inception', 'inception', '2023-01-11 09:00:00'),
       (6, 2, 'Interstellar', 'interstellar', '2023-01-12 09:00:00'),
       (7, 2, 'The Godfather', 'the-godfather', '2023-01-13 09:00:00'),
       (8, 2, 'Pulp Fiction', 'pulp-fiction', '2023-01-14 09:00:00'),
       (9, 2, 'The Dark Knight', 'the-dark-knight', '2023-01-15 09:00:00'),
       (10, 2, 'Fight Club', 'fight-club', '2023-01-16 09:00:00'),
       (11, 2, 'Parasite', 'parasite', '2023-01-17 09:00:00'),
       (12, 2, 'Whiplash', 'whiplash', '2023-01-18 09:00:00'),
       (13, 2, 'The Lord of the Rings', 'lord-of-the-rings', '2023-01-19 09:00:00'),
       (14, 2, 'Forrest Gump', 'forrest-gump', '2023-01-20 09:00:00'),
       (15, 2, 'The Shawshank Redemption', 'shawshank-redemption', '2023-01-21 09:00:00'),
       (16, 2, 'Spirited Away', 'spirited-away', '2023-01-22 09:00:00'),
       (17, 2, 'Mad Max: Fury Road', 'mad-max-fury-road', '2023-01-23 09:00:00'),
       (18, 2, 'Blade Runner 2049', 'blade-runner-2049', '2023-01-24 09:00:00');

-- 15 series folders under Series (folder_id = 3)
INSERT INTO forum.folders (folder_id, parent_id, name, slug, created_at)
VALUES (19, 3, 'Breaking Bad', 'breaking-bad', '2023-02-01 09:00:00'),
       (20, 3, 'Game of Thrones', 'game-of-thrones', '2023-02-02 09:00:00'),
       (21, 3, 'The Office (US)', 'the-office-us', '2023-02-03 09:00:00'),
       (22, 3, 'Stranger Things', 'stranger-things', '2023-02-04 09:00:00'),
       (23, 3, 'The Wire', 'the-wire', '2023-02-05 09:00:00'),
       (24, 3, 'The Expanse', 'the-expanse', '2023-02-06 09:00:00'),
       (25, 3, 'Dark', 'dark', '2023-02-07 09:00:00'),
       (26, 3, 'Chernobyl', 'chernobyl', '2023-02-08 09:00:00'),
       (27, 3, 'Better Call Saul', 'better-call-saul', '2023-02-09 09:00:00'),
       (28, 3, 'True Detective', 'true-detective', '2023-02-10 09:00:00'),
       (29, 3, 'The Mandalorian', 'the-mandalorian', '2023-02-11 09:00:00'),
       (30, 3, 'The Boys', 'the-boys', '2023-02-12 09:00:00'),
       (31, 3, 'Westworld', 'westworld', '2023-02-13 09:00:00'),
       (32, 3, 'Sherlock', 'sherlock', '2023-02-14 09:00:00'),
       (33, 3, 'The Last of Us', 'the-last-of-us', '2023-02-15 09:00:00');

-- ============================================
-- TAGS (NON-GENRE)
-- ============================================
INSERT INTO forum.tags (tag_id, name)
VALUES (1, 'rewatch-worthy'),
       (2, 'slow-burn'),
       (3, 'character-driven'),
       (4, 'plot-heavy'),
       (5, 'great-soundtrack'),
       (6, 'mind-bending'),
       (7, 'underrated-gem'),
       (8, 'comfort-watch');

-- ============================================
-- USERS: 5 ADMINS + 40 REGULAR USERS
-- ============================================
SET @pw := '$2a$10$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi';

-- Admins (1–5)
INSERT INTO forum.users (user_id, first_name, last_name, username, email, password, is_admin, created_at)
VALUES (1, 'Alex', 'Adminson', 'admin1', 'admin1@example.com', @pw, 1, '2023-01-05 09:00:00'),
       (2, 'Brian', 'Adminson', 'admin2', 'admin2@example.com', @pw, 1, '2023-01-06 09:00:00'),
       (3, 'Clara', 'Adminson', 'admin3', 'admin3@example.com', @pw, 1, '2023-01-07 09:00:00'),
       (4, 'Diana', 'Adminson', 'admin4', 'admin4@example.com', @pw, 1, '2023-01-08 09:00:00'),
       (5, 'Ethan', 'Adminson', 'admin5', 'admin5@example.com', @pw, 1, '2023-01-09 09:00:00');

-- Regular users (6–45)
INSERT INTO forum.users (user_id, first_name, last_name, username, email, password, is_admin, created_at)
VALUES (6, 'Felix', 'Walker', 'user06', 'user06@example.com', @pw, 0, '2023-01-10 10:00:00'),
       (7, 'Grace', 'Walker', 'user07', 'user07@example.com', @pw, 0, '2023-01-11 10:00:00'),
       (8, 'Henry', 'Bennett', 'user08', 'user08@example.com', @pw, 0, '2023-01-12 10:00:00'),
       (9, 'Irene', 'Bennett', 'user09', 'user09@example.com', @pw, 0, '2023-01-13 10:00:00'),
       (10, 'Jonas', 'Cooper', 'user10', 'user10@example.com', @pw, 0, '2023-01-14 10:00:00'),
       (11, 'Karen', 'Cooper', 'user11', 'user11@example.com', @pw, 0, '2023-01-15 10:00:00'),
       (12, 'Liam', 'Foster', 'user12', 'user12@example.com', @pw, 0, '2023-01-16 10:00:00'),
       (13, 'Marta', 'Foster', 'user13', 'user13@example.com', @pw, 0, '2023-01-17 10:00:00'),
       (14, 'Noah', 'Granger', 'user14', 'user14@example.com', @pw, 0, '2023-01-18 10:00:00'),
       (15, 'Olga', 'Granger', 'user15', 'user15@example.com', @pw, 0, '2023-01-19 10:00:00'),
       (16, 'Peter', 'Harper', 'user16', 'user16@example.com', @pw, 0, '2023-01-20 10:00:00'),
       (17, 'Quinn', 'Harper', 'user17', 'user17@example.com', @pw, 0, '2023-01-21 10:00:00'),
       (18, 'Riley', 'Iverson', 'user18', 'user18@example.com', @pw, 0, '2023-01-22 10:00:00'),
       (19, 'Sofia', 'Iverson', 'user19', 'user19@example.com', @pw, 0, '2023-01-23 10:00:00'),
       (20, 'Tomas', 'Johnson', 'user20', 'user20@example.com', @pw, 0, '2023-01-24 10:00:00'),
       (21, 'Ulrich', 'Johnson', 'user21', 'user21@example.com', @pw, 0, '2023-01-25 10:00:00'),
       (22, 'Vera', 'Kingston', 'user22', 'user22@example.com', @pw, 0, '2023-01-26 10:00:00'),
       (23, 'Wendy', 'Kingston', 'user23', 'user23@example.com', @pw, 0, '2023-01-27 10:00:00'),
       (24, 'Xenia', 'Lewis', 'user24', 'user24@example.com', @pw, 0, '2023-01-28 10:00:00'),
       (25, 'Yanis', 'Lewis', 'user25', 'user25@example.com', @pw, 0, '2023-01-29 10:00:00'),
       (26, 'Zoran', 'Miller', 'user26', 'user26@example.com', @pw, 0, '2023-01-30 10:00:00'),
       (27, 'Amira', 'Miller', 'user27', 'user27@example.com', @pw, 0, '2023-01-31 10:00:00'),
       (28, 'Bianca', 'Nelson', 'user28', 'user28@example.com', @pw, 0, '2023-02-01 10:00:00'),
       (29, 'Caleb', 'Nelson', 'user29', 'user29@example.com', @pw, 0, '2023-02-02 10:00:00'),
       (30, 'Derek', 'Owenson', 'user30', 'user30@example.com', @pw, 0, '2023-02-03 10:00:00'),
       (31, 'Elena', 'Owenson', 'user31', 'user31@example.com', @pw, 0, '2023-02-04 10:00:00'),
       (32, 'Fabio', 'Patterson', 'user32', 'user32@example.com', @pw, 0, '2023-02-05 10:00:00'),
       (33, 'Greta', 'Patterson', 'user33', 'user33@example.com', @pw, 0, '2023-02-06 10:00:00'),
       (34, 'Helga', 'Quentin', 'user34', 'user34@example.com', @pw, 0, '2023-02-07 10:00:00'),
       (35, 'Ismail', 'Quentin', 'user35', 'user35@example.com', @pw, 0, '2023-02-08 10:00:00'),
       (36, 'Jakob', 'Robinson', 'user36', 'user36@example.com', @pw, 0, '2023-02-09 10:00:00'),
       (37, 'Katia', 'Robinson', 'user37', 'user37@example.com', @pw, 0, '2023-02-10 10:00:00'),
       (38, 'Lukas', 'Stevens', 'user38', 'user38@example.com', @pw, 0, '2023-02-11 10:00:00'),
       (39, 'Milan', 'Stevens', 'user39', 'user39@example.com', @pw, 0, '2023-02-12 10:00:00'),
       (40, 'Nadia', 'Turner', 'user40', 'user40@example.com', @pw, 0, '2023-02-13 10:00:00'),
       (41, 'Oskar', 'Turner', 'user41', 'user41@example.com', @pw, 0, '2023-02-14 10:00:00'),
       (42, 'Paula', 'Vaughn', 'user42', 'user42@example.com', @pw, 0, '2023-02-15 10:00:00'),
       (43, 'Ronan', 'Vaughn', 'user43', 'user43@example.com', @pw, 0, '2023-02-16 10:00:00'),
       (44, 'Sara', 'Williams', 'user44', 'user44@example.com', @pw, 0, '2023-02-17 10:00:00'),
       (45, 'Tessa', 'Williams', 'user45', 'user45@example.com', @pw, 0, '2023-02-18 10:00:00');

-- ============================================
-- POSTS: 15 PER LEAF FOLDER (FOLDERS 4–33)
-- CONTENT REFERENCES THE MOVIE/SERIES
-- ============================================
DELIMITER //

CREATE PROCEDURE seed_posts()
BEGIN
    DECLARE f INT DEFAULT 4;
    DECLARE i INT;
    DECLARE author_id INT;
    DECLARE folder_title VARCHAR(64);
    DECLARE base_date DATETIME;

    SET base_date = '2023-03-01 20:00:00';

    WHILE f <= 33
        DO
            SELECT name INTO folder_title FROM forum.folders WHERE folder_id = f;

            SET i = 1;
            WHILE i <= 15
                DO
                    -- Rotate authors across regular users (6–45)
                    SET author_id = 6 + ((f - 4) * 15 + i) MOD 40;

                    INSERT INTO forum.posts (user_id, title, content, folder_id, created_at)
                    VALUES (author_id,
                            CONCAT(folder_title, ' - discussion thread #', LPAD(i, 2, '0')),
                            CONCAT(
                                    'How did you experience "', folder_title,
                                    '" on your latest watch? In this thread we talk about specific scenes, character arcs, pacing, ',
                                    'and how the story holds up over time. Share your detailed thoughts, including what worked for you ',
                                    'and what you felt was weaker, so others can decide if it is worth a rewatch.'
                            ),
                            f,
                            DATE_ADD(base_date, INTERVAL ((f - 4) * 15 + i) DAY));

                    SET i = i + 1;
                END WHILE;

            SET f = f + 1;
        END WHILE;
END//
DELIMITER ;

CALL seed_posts();
DROP PROCEDURE seed_posts;

-- ============================================
-- TAGS ↔ POSTS: EACH POST GETS ONE TAG CYCLING 1–8
-- TOTAL POSTS = 30 FOLDERS * 15 = 450
-- ============================================
DELIMITER //

CREATE PROCEDURE seed_tags_posts()
BEGIN
    DECLARE p INT DEFAULT 1;
    DECLARE max_posts INT DEFAULT 450;
    DECLARE t_id INT;

    WHILE p <= max_posts
        DO
            SET t_id = 1 + (p MOD 8); -- cycle 1..8
            INSERT INTO forum.tags_posts (tag_id, post_id)
            VALUES (t_id, p);
            SET p = p + 1;
        END WHILE;
END//
DELIMITER ;

CALL seed_tags_posts();
DROP PROCEDURE seed_tags_posts;

-- ============================================
-- COMMENTS: 6 PER POST (REALISTIC DISCUSSION)
-- ============================================
DELIMITER //

CREATE PROCEDURE seed_comments()
BEGIN
    DECLARE p INT DEFAULT 1;
    DECLARE max_posts INT DEFAULT 450;
    DECLARE i INT;
    DECLARE commenter_id INT;
    DECLARE post_created DATETIME;
    DECLARE comment_text TEXT;

    WHILE p <= max_posts
        DO
            SELECT created_at INTO post_created FROM forum.posts WHERE post_id = p;

            SET i = 1;
            WHILE i <= 6
                DO
                    -- Rotate commenters across all users (1–45)
                    SET commenter_id = 1 + ((p * 7 + i * 3) MOD 45);

                    CASE
                        WHEN i = 1 THEN SET comment_text = CONCAT(
                                'First impressions on this rewatch: the opening act still hits hard and sets the tone well. ',
                                'I noticed a lot more small details this time that foreshadow later twists.'
                                                           );
                        WHEN i = 2 THEN SET comment_text = CONCAT(
                                'I love how the characters are written here. Even minor side characters feel like real people ',
                                'with their own motivations rather than just moving the plot forward.'
                                                           );
                        WHEN i = 3 THEN SET comment_text = CONCAT(
                                'The pacing in the middle section drags a little for me, but the payoff in the final third ',
                                'makes it worth it. Curious if others felt the same or if it was fine for you.'
                                                           );
                        WHEN i = 4 THEN SET comment_text = CONCAT(
                                'On the technical side, the sound design and score are fantastic. With good speakers or headphones ',
                                'you really feel every quiet moment and big set piece.'
                                                           );
                        WHEN i = 5 THEN SET comment_text = CONCAT(
                                'This is one of those titles I recommend carefully. It is excellent, but I think you need to be ',
                                'in the right mood and willing to pay attention to really appreciate it.'
                                                           );
                        ELSE SET comment_text = CONCAT(
                                'I am surprised how well this holds up years after release. Some visual effects age a bit, ',
                                'but the themes and character work feel very current and relatable.'
                                                );
                        END CASE;

                    INSERT INTO forum.comments (post_id, user_id, content, created_at)
                    VALUES (p,
                            commenter_id,
                            comment_text,
                            DATE_ADD(post_created, INTERVAL (i * 3) HOUR));

                    SET i = i + 1;
                END WHILE;

            SET p = p + 1;
        END WHILE;
END//
DELIMITER ;

CALL seed_comments();
DROP PROCEDURE seed_comments;

-- ============================================
-- POST LIKES: SUBSET OF USERS LIKE EACH POST
-- ============================================
DELIMITER //

CREATE PROCEDURE seed_post_likes()
BEGIN
    DECLARE p INT DEFAULT 1;
    DECLARE max_posts INT DEFAULT 450;
    DECLARE u INT;

    WHILE p <= max_posts
        DO
            SET u = 1;
            WHILE u <= 45
                DO
                    -- About ~1/5 of users like each post in a deterministic pattern
                    IF ((p + u * 3) MOD 11 = 0) THEN
                        INSERT IGNORE INTO forum.likes (user_id, post_id)
                        VALUES (u, p);
                    END IF;

                    SET u = u + 1;
                END WHILE;

            SET p = p + 1;
        END WHILE;
END//
DELIMITER ;

CALL seed_post_likes();
DROP PROCEDURE seed_post_likes;

-- ============================================
-- POST VIEWS: USERS VIEW POSTS ON VARIOUS DATES
-- ============================================
DELIMITER //

CREATE PROCEDURE seed_post_views()
BEGIN
    DECLARE p INT DEFAULT 1;
    DECLARE max_posts INT DEFAULT 450;
    DECLARE u INT;
    DECLARE post_created DATETIME;
    DECLARE view_d DATE;

    WHILE p <= max_posts
        DO
            SELECT created_at INTO post_created FROM forum.posts WHERE post_id = p;

            SET u = 1;
            WHILE u <= 45
                DO
                    -- Each user views only some posts, pattern-based
                    IF ((p + 2 * u) MOD 7 = 0) THEN
                        SET view_d = DATE(DATE_ADD(post_created, INTERVAL (u MOD 25) DAY));
                        INSERT IGNORE INTO forum.posts_users_views (user_id, post_id, view_date)
                        VALUES (u, p, view_d);
                    END IF;

                    SET u = u + 1;
                END WHILE;

            SET p = p + 1;
        END WHILE;
END//
DELIMITER ;

CALL seed_post_views();
DROP PROCEDURE seed_post_views;

-- ============================================
-- COMMENT LIKES
-- ============================================
DELIMITER //

CREATE PROCEDURE seed_comment_likes()
BEGIN
    DECLARE c INT DEFAULT 1;
    DECLARE max_c INT;
    DECLARE u INT;

    -- Determine how many comments we created
    SELECT MAX(comment_id) INTO max_c FROM forum.comments;

    WHILE c <= max_c
        DO
            SET u = 1;
            WHILE u <= 45
                DO
                    -- Sparse likes: only some users like some comments
                    IF ((c + u * 5) MOD 23 = 0) THEN
                        INSERT IGNORE INTO forum.comment_likes (comment_id, user_id)
                        VALUES (c, u);
                    END IF;

                    SET u = u + 1;
                END WHILE;

            SET c = c + 1;
        END WHILE;
END//
DELIMITER ;

CALL seed_comment_likes();
DROP PROCEDURE seed_comment_likes;

-- ============================================
-- DONE
-- ============================================
