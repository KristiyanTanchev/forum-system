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
-- USERS: 3 ADMINS + 5 MODERATORS + 37 REGULAR USERS
-- ============================================
SET @pw := '$2a$10$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi';

-- Admins (1–3)
INSERT INTO forum.users (user_id, first_name, last_name, username, email, password, role, created_at)
VALUES (1, 'Alex', 'Adminson', 'admin1', 'admin1@example.com', @pw, 'ADMIN', '2023-01-05 09:00:00'),
       (2, 'Brian', 'Adminson', 'admin2', 'admin2@example.com', @pw, 'ADMIN', '2023-01-06 09:00:00'),
       (3, 'Clara', 'Adminson', 'admin3', 'admin3@example.com', @pw, 'ADMIN', '2023-01-07 09:00:00');

-- Moderators (4–8)
INSERT INTO forum.users (user_id, first_name, last_name, username, email, password, role, created_at)
VALUES (4, 'Diana', 'Moderworth', 'moderator1', 'moderator1@example.com', @pw, 'MODERATOR', '2023-01-08 09:00:00'),
       (5, 'Ethan', 'Moderworth', 'moderator2', 'moderator2@example.com', @pw, 'MODERATOR', '2023-01-09 09:00:00'),
       (6, 'Fiona', 'Moderworth', 'moderator3', 'moderator3@example.com', @pw, 'MODERATOR', '2023-01-10 09:00:00'),
       (7, 'George', 'Moderworth', 'moderator4', 'moderator4@example.com', @pw, 'MODERATOR', '2023-01-11 09:00:00'),
       (8, 'Hannah', 'Moderworth', 'moderator5', 'moderator5@example.com', @pw, 'MODERATOR', '2023-01-12 09:00:00');

-- Regular users (9–45)
INSERT INTO forum.users (user_id, first_name, last_name, username, email, password, role, created_at)
VALUES (9, 'Felix', 'Walker', 'user06', 'user06@example.com', @pw, 'USER', '2023-01-10 10:00:00'),
       (10, 'Grace', 'Walker', 'user07', 'user07@example.com', @pw, 'USER', '2023-01-11 10:00:00'),
       (11, 'Henry', 'Bennett', 'user08', 'user08@example.com', @pw, 'USER', '2023-01-12 10:00:00'),
       (12, 'Irene', 'Bennett', 'user09', 'user09@example.com', @pw, 'USER', '2023-01-13 10:00:00'),
       (13, 'Jonas', 'Cooper', 'user10', 'user10@example.com', @pw, 'USER', '2023-01-14 10:00:00'),
       (14, 'Karen', 'Cooper', 'user11', 'user11@example.com', @pw, 'USER', '2023-01-15 10:00:00'),
       (15, 'Liam', 'Foster', 'user12', 'user12@example.com', @pw, 'USER', '2023-01-16 10:00:00'),
       (16, 'Marta', 'Foster', 'user13', 'user13@example.com', @pw, 'USER', '2023-01-17 10:00:00'),
       (17, 'Noah', 'Granger', 'user14', 'user14@example.com', @pw, 'USER', '2023-01-18 10:00:00'),
       (18, 'Olga', 'Granger', 'user15', 'user15@example.com', @pw, 'USER', '2023-01-19 10:00:00'),
       (19, 'Peter', 'Harper', 'user16', 'user16@example.com', @pw, 'USER', '2023-01-20 10:00:00'),
       (20, 'Quinn', 'Harper', 'user17', 'user17@example.com', @pw, 'USER', '2023-01-21 10:00:00'),
       (21, 'Riley', 'Iverson', 'user18', 'user18@example.com', @pw, 'USER', '2023-01-22 10:00:00'),
       (22, 'Sofia', 'Iverson', 'user19', 'user19@example.com', @pw, 'USER', '2023-01-23 10:00:00'),
       (23, 'Tomas', 'Johnson', 'user20', 'user20@example.com', @pw, 'USER', '2023-01-24 10:00:00'),
       (24, 'Ulrich', 'Johnson', 'user21', 'user21@example.com', @pw, 'USER', '2023-01-25 10:00:00'),
       (25, 'Vera', 'Kingston', 'user22', 'user22@example.com', @pw, 'USER', '2023-01-26 10:00:00'),
       (26, 'Wendy', 'Kingston', 'user23', 'user23@example.com', @pw, 'USER', '2023-01-27 10:00:00'),
       (27, 'Xenia', 'Lewis', 'user24', 'user24@example.com', @pw, 'USER', '2023-01-28 10:00:00'),
       (28, 'Yanis', 'Lewis', 'user25', 'user25@example.com', @pw, 'USER', '2023-01-29 10:00:00'),
       (29, 'Zoran', 'Miller', 'user26', 'user26@example.com', @pw, 'USER', '2023-01-30 10:00:00'),
       (30, 'Amira', 'Miller', 'user27', 'user27@example.com', @pw, 'USER', '2023-01-31 10:00:00'),
       (31, 'Bianca', 'Nelson', 'user28', 'user28@example.com', @pw, 'USER', '2023-02-01 10:00:00'),
       (32, 'Caleb', 'Nelson', 'user29', 'user29@example.com', @pw, 'USER', '2023-02-02 10:00:00'),
       (33, 'Derek', 'Owenson', 'user30', 'user30@example.com', @pw, 'USER', '2023-02-03 10:00:00'),
       (34, 'Elena', 'Owenson', 'user31', 'user31@example.com', @pw, 'USER', '2023-02-04 10:00:00'),
       (35, 'Fabio', 'Patterson', 'user32', 'user32@example.com', @pw, 'USER', '2023-02-05 10:00:00'),
       (36, 'Greta', 'Patterson', 'user33', 'user33@example.com', @pw, 'USER', '2023-02-06 10:00:00'),
       (37, 'Helga', 'Quentin', 'user34', 'user34@example.com', @pw, 'USER', '2023-02-07 10:00:00'),
       (38, 'Ismail', 'Quentin', 'user35', 'user35@example.com', @pw, 'USER', '2023-02-08 10:00:00'),
       (39, 'Jakob', 'Robinson', 'user36', 'user36@example.com', @pw, 'USER', '2023-02-09 10:00:00'),
       (40, 'Katia', 'Robinson', 'user37', 'user37@example.com', @pw, 'USER', '2023-02-10 10:00:00'),
       (41, 'Lukas', 'Stevens', 'user38', 'user38@example.com', @pw, 'USER', '2023-02-11 10:00:00'),
       (42, 'Milan', 'Stevens', 'user39', 'user39@example.com', @pw, 'USER', '2023-02-12 10:00:00'),
       (43, 'Nadia', 'Turner', 'user40', 'user40@example.com', @pw, 'USER', '2023-02-13 10:00:00'),
       (44, 'Oskar', 'Turner', 'user41', 'user41@example.com', @pw, 'USER', '2023-02-14 10:00:00'),
       (45, 'Paula', 'Vaughn', 'user42', 'user42@example.com', @pw, 'USER', '2023-02-15 10:00:00');

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
                    -- Rotate authors across regular users (9–45) - 37 users total
                    SET author_id = 9 + ((f - 4) * 15 + i) MOD 37;

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


-- ============================================
-- Added posts
-- ============================================
INSERT INTO forum.posts (post_id, user_id, title, content, created_at, updated_at, deleted_at, is_deleted, folder_id)
VALUES (457, 4, 'Welcome to CineTalk – Read This First', '**Welcome to CineTalk!**

This is the central hub for all discussions about movies and series. Before you start posting, please take a moment to read the basic <u>guidelines</u> below:

- Be respectful and constructive.
- Use spoiler formatting for major plot points.
- Pick the right folder for your topic (Movies vs Series vs specific title).

You can use:

- **Bold** for emphasis
- *Italic* for softer emphasis
- `inline code` if you want to show commands or snippets

If you are new here, feel free to:

1. Introduce yourself in this thread.
2. Share your all time favorite movie or series.
3. Tell us what you are planning to watch next.

>! Example spoiler: This hides a major twist from other readers !<

Have fun and enjoy your stay on **CineTalk**!', '2025-03-01 18:00:00', '2025-12-08 17:20:41', null, 0, 1);
INSERT INTO forum.posts (post_id, user_id, title, content, created_at, updated_at, deleted_at, is_deleted, folder_id)
VALUES (458, 5, 'How to Use Tags and Spoilers', '**Tags**  help keep the forum readable and safe for people who have not seen something yet.

### Tags

We use non genre tags like:

- `rewatch-worthy`
- `slow-burn`
- `character-driven`
- `great-soundtrack`

When you create a post, try to pick a tag that best describes the **feel** of the movie or series rather than just the genre.


Please also keep thread titles spoiler free whenever possible. For example:

- ✅ `Interstellar – thoughts on the ending (spoilers)`
- ❌ `Interstellar – Cooper enters the tesseract and...`', '2024-08-02 19:30:00', '2025-12-08 17:20:51', null, 0, 1);
INSERT INTO forum.posts (post_id, user_id, title, content, created_at, updated_at, deleted_at, is_deleted, folder_id)
VALUES (459, 6, 'Movies Folder Guidelines – Spoilers and Thread Titles', 'Welcome to the **Movies** section!

This folder is for feature length films only. For series, please head over to the **Series** folder.

### Good thread titles

Try to structure your titles like this:

- `Inception – first time watch impressions (spoilers)`
- `The Godfather – rewatch discussion`
- `Parasite – class themes and symbolism`

This helps other users quickly see:

1. Which movie you are talking about.
2. Whether the thread contains spoilers.

### Spoiler etiquette

Even inside a spoiler marked thread, please still use:

>! spoiler formatting for big twists !<

Some people like to read general impressions without seeing every plot detail.

Thanks for helping keep the Movies folder organized and easy to browse!', '2025-01-03 18:15:00', null, null, 0, 2);
INSERT INTO forum.posts (post_id, user_id, title, content, created_at, updated_at, deleted_at, is_deleted, folder_id)
VALUES (460, 7, 'Where to Start: Great Movies to Discuss', 'Not sure where to jump in? Here are some **great starting points** in the Movies section:

- **The Matrix** – philosophy, action, and iconic visuals.
- **The Lord of the Rings** – epic fantasy and worldbuilding.
- **Parasite** – modern classic with sharp social commentary.
- **Spirited Away** – hand drawn animation and magical realism.

You can:

- Join existing threads in the specific movie folders.
- Start a new topic like *First time watching The Matrix – does it still hold up?*.
- Compare movies, for example *Interstellar vs. The Martian* in terms of science and tone.

<u>Tip:</u> Do not be afraid to revive older threads. A thoughtful new comment is always welcome, even on a discussion that started months ago.',
        '2025-08-04 20:00:00', null, null, 0, 2);
INSERT INTO forum.posts (post_id, user_id, title, content, created_at, updated_at, deleted_at, is_deleted, folder_id)
VALUES (461, 8, 'Series Folder Guidelines – Seasons and Episode Threads', 'This folder is for **TV and streaming series**. Because series can be long and complex, we try to keep discussions organized.

### Recommended formats

Use titles like:

- `Breaking Bad – Season 3 rewatch`
- `The Last of Us – Episode 5 impressions`
- `Dark – trying to untangle the timelines`

Inside your post, you can structure things with headings:

#### Example

**Overall thoughts**

Write a short, spoiler light summary.

**Favorite scenes**

List specific moments (with spoiler tags if needed).

**Questions**

Ask about details you did not fully understand.

Remember to mark anything beyond the episode or season in the title using:

>! spoiler formatting !<

This helps people follow along at their own pace.', '2025-03-05 17:45:00', null, null, 0, 3);
INSERT INTO forum.posts (post_id, user_id, title, content, created_at, updated_at, deleted_at, is_deleted, folder_id)
VALUES (462, 4, 'Binge vs Weekly – How Do You Watch Series?', '**Question from the moderation team:**

Do you prefer to **binge** entire seasons at once, or watch **week by week**?

Some things you might talk about:

- Does binging make you forget details, or does it help you stay immersed?
- Are there shows that work better weekly, like mystery or puzzle shows?
- Are there comfort series that you rewatch in the background?

Feel free to mention specific titles, for example:

- *Stranger Things* as a binge.
- *The Mandalorian* as a weekly event.
- *Better Call Saul* for slow burn character development.

<u>Bonus question:</u> Has your viewing style changed since streaming became common?', '2025-12-06 21:10:00', null,
        null, 0, 3);
INSERT INTO forum.posts (post_id, user_id, title, content, created_at, updated_at, deleted_at, is_deleted, folder_id)
VALUES (471, 21, 'Westworld Season 1 – why it still works as a complete story', 'I just finished a rewatch of **Westworld Season 1** and honestly, it still works almost perfectly as a *standalone* story.

### What still holds up

- The opening loop with Dolores and the flies
- The slow realization that timelines are not what they seem
- The way the maze is not what the guests think it is
- New bullet point

Even if you are not a fan of later seasons, Season 1 feels like a complete arc about consciousness, suffering, and choice.

[SPOILER] The reveal about William and the Man in Black is still one of the best long-game twists in TV for me.

What do you think: if the show had ended after Season 1, would it be remembered as a near-perfect sci-fi mini-series?

What do you think about this one:
[Black mirror](https://www.imdb.com/title/tt2085059/)', '2024-10-05 21:15:00', null, null, 0, 31);
INSERT INTO forum.posts (post_id, user_id, title, content, created_at, updated_at, deleted_at, is_deleted, folder_id)
VALUES (472, 34, 'Did Westworld get too complicated or was it always like this?', 'I see a lot of people say **Westworld fell apart** because it became too confusing. I am not sure I fully agree.

### My take

- The show was always about layers of reality and unreliable perception.
- It asks you to pay attention and accept that you are missing pieces for a while.
- Some storylines are less satisfying, but I do not think complexity alone is the problem.

I do think Season 3 is the hardest to love on first watch. The shift from park to real world feels like a different show.

[SPOILER] I actually enjoyed the Rehoboam plot, but it probably needed more build-up and quieter character moments to land better.

Curious:
Do you feel the show is overwritten, or do you like needing a wiki open on the second screen?', '2025-01-12 19:40:00',
        null, null, 0, 31);
INSERT INTO forum.posts (post_id, user_id, title, content, created_at, updated_at, deleted_at, is_deleted, folder_id)
VALUES (473, 29, 'Favorite Westworld episodes for a focused rewatch', 'If someone does not want to rewatch the whole show, which episodes would you recommend as a **focused rewatch playlist**?

Here is my list so far:

1. **S01E02 – Chestnut**
2. **S01E07 – Trompe L Oeil**
3. **S01E10 – The Bicameral Mind**
4. **S02E04 – The Riddle of the Sphinx**
5. **S02E08 – Kiksuya** (maybe the strongest emotional episode in the series)

I would add a couple of Season 3 and 4 episodes, but those seasons seem more serialized and harder to pull out on their own.

[SPOILER] Kiksuya still hits incredibly hard emotionally. It might work as a standalone short film.

What would you add or remove from this list?', '2025-03-03 22:05:00', null, null, 0, 31);
INSERT INTO forum.posts (post_id, user_id, title, content, created_at, updated_at, deleted_at, is_deleted, folder_id)
VALUES (474, 17, 'Where did you stop watching Westworld (if you did) and why?', '**Honest poll time:**

If you dropped **Westworld** at some point, when was it and what made you stop?

Some possible answers:

- Finished Season 1, never started Season 2
- Halfway through Season 2, lost track of timelines
- Season 3, did not like the real world direction
- Season 4, felt like it was too meta

If you want to reference a major plot moment, just mark it like this:

I am especially interested if anyone came back later and ended up enjoying the parts they initially bounced off.',
        '2025-05-18 18:30:00', null, null, 0, 31);

-- ============================================
-- Post likes
-- ============================================

INSERT INTO forum.likes (user_id, post_id)
VALUES (1, 457);
INSERT INTO forum.likes (user_id, post_id)
VALUES (6, 457);
INSERT INTO forum.likes (user_id, post_id)
VALUES (1, 458);
INSERT INTO forum.likes (user_id, post_id)
VALUES (6, 458);
INSERT INTO forum.likes (user_id, post_id)
VALUES (1, 471);
INSERT INTO forum.likes (user_id, post_id)
VALUES (2, 471);
INSERT INTO forum.likes (user_id, post_id)
VALUES (17, 471);
INSERT INTO forum.likes (user_id, post_id)
VALUES (25, 471);
INSERT INTO forum.likes (user_id, post_id)
VALUES (33, 471);
INSERT INTO forum.likes (user_id, post_id)
VALUES (19, 472);
INSERT INTO forum.likes (user_id, post_id)
VALUES (27, 472);
INSERT INTO forum.likes (user_id, post_id)
VALUES (36, 472);
INSERT INTO forum.likes (user_id, post_id)
VALUES (21, 473);
INSERT INTO forum.likes (user_id, post_id)
VALUES (30, 473);
INSERT INTO forum.likes (user_id, post_id)
VALUES (39, 473);
INSERT INTO forum.likes (user_id, post_id)
VALUES (18, 474);
INSERT INTO forum.likes (user_id, post_id)
VALUES (26, 474);
INSERT INTO forum.likes (user_id, post_id)
VALUES (34, 474);


-- ============================================
-- Added comments
-- ============================================

INSERT INTO forum.comments (comment_id, post_id, user_id, content, created_at, updated_at, deleted_at, is_deleted)
VALUES (2701, 471, 23, 'Rewatched Season 1 recently as well and I agree it feels almost like a **self-contained mini-series**.

What I like most is how the show trusts the viewer to catch patterns without over-explaining. The repetition of loops, small glitches in behavior, and the gradual shift in Dolores perspective still work really well.',
        '2025-10-07 20:15:00', '2025-12-08 16:55:53', null, 0);
INSERT INTO forum.comments (comment_id, post_id, user_id, content, created_at, updated_at, deleted_at, is_deleted)
VALUES (2702, 471, 31, 'For me the thing that really holds up is the *mood*.

Even knowing every twist, I still enjoy the slow, uneasy atmosphere of the park and the focus on small character beats. The later seasons may go bigger, but Season 1 feels tight and deliberate.',
        '2025-10-08 18:30:00', '2025-12-08 16:55:46', null, 0);
INSERT INTO forum.comments (comment_id, post_id, user_id, content, created_at, updated_at, deleted_at, is_deleted)
VALUES (2703, 471, 38, 'If it had ended after Season 1, I think people would talk about it the way they talk about a great anthology or limited series.

The downside is we would lose some amazing moments later on, but the upside is that it would avoid a lot of the “it got too complicated” narrative.',
        '2025-10-09 22:05:00', '2025-12-08 16:55:34', null, 0);
INSERT INTO forum.comments (comment_id, post_id, user_id, content, created_at, updated_at, deleted_at, is_deleted)
VALUES (2704, 472, 27, 'I think complexity is not the real issue, it is **clarity of motivation**.

    In Season 1, almost every character goal is easy to track, even when the timelines are tricky. In later seasons, some characters feel like they change goals off-screen, which makes the plot harder to connect to emotionally.',
        '2025-01-13 21:05:00', null, null, 0);
INSERT INTO forum.comments (comment_id, post_id, user_id, content, created_at, updated_at, deleted_at, is_deleted)
VALUES (2705, 472, 35, 'The show has always been dense, but early on it felt like there was a strong emotional core (Dolores, Maeve, Ford).

    When the focus shifts more to big ideas about society and control, I think some viewers check out because they were mainly there for the character work.',
        '2025-01-14 18:10:00', null, null, 0);
INSERT INTO forum.comments (comment_id, post_id, user_id, content, created_at, updated_at, deleted_at, is_deleted)
VALUES (2706, 472, 42, 'For me it is a rewatch-friendly show. On second watch, a lot of the “confusing” parts snap into place, especially in Season 2.

    The downside is that not everyone wants to rewatch a whole season just to make it feel coherent.',
        '2025-01-15 20:20:00', null, null, 0);
INSERT INTO forum.comments (comment_id, post_id, user_id, content, created_at, updated_at, deleted_at, is_deleted)
VALUES (2707, 473, 29, 'Love this idea of a **focused rewatch playlist**.

    I would definitely keep your choices and maybe add the Season 1 finale if someone wants a clean “beginning and end” structure. It gives a good sense of what the show is about without committing to every twist later.',
        '2025-03-04 21:30:00', null, null, 0);
INSERT INTO forum.comments (comment_id, post_id, user_id, content, created_at, updated_at, deleted_at, is_deleted)
VALUES (2708, 473, 33, 'Kiksuya is such a standout episode. It feels almost like a short story inside the larger series, with its own tone and rhythm.

    If someone is skeptical about Westworld being all puzzles and mystery boxes, I usually point them to that episode.',
        '2025-03-05 19:15:00', null, null, 0);
INSERT INTO forum.comments (comment_id, post_id, user_id, content, created_at, updated_at, deleted_at, is_deleted)
VALUES (2709, 473, 41, 'I would add at least one later-season episode that showcases the bigger themes about free will and systems of control, just so the playlist does not feel completely park-bound.

    That said, your list is a great way to show the shows range without requiring a full rewatch.',
        '2025-03-06 20:40:00', null, null, 0);
INSERT INTO forum.comments (comment_id, post_id, user_id, content, created_at, updated_at, deleted_at, is_deleted)
VALUES (2710, 474, 24, 'I stopped midway through Season 2 on first watch.

    There was a point where I felt like I was tracking timelines more than I was caring about characters. When I eventually came back and rewatched from the start of the season, it played a lot better.',
        '2025-05-19 19:10:00', null, null, 0);
INSERT INTO forum.comments (comment_id, post_id, user_id, content, created_at, updated_at, deleted_at, is_deleted)
VALUES (2711, 474, 32, 'Season 3 was my breaking point initially. The tone shift to the real world was interesting, but it felt like a different show.

    On rewatch, I appreciate some of the ideas more, but I still miss the park as a setting.', '2025-05-20 18:45:00',
        null, null, 0);
INSERT INTO forum.comments (comment_id, post_id, user_id, content, created_at, updated_at, deleted_at, is_deleted)
VALUES (2712, 474, 40, 'I actually stuck with it all the way through and do not regret it.

    It is not a perfect series, but I enjoy shows that swing big even when they miss sometimes. The conversations around Westworld are half the fun, which is why I am glad this thread exists.',
        '2025-05-21 21:00:00', null, null, 0);


-- ============================================
-- Added tags
-- ============================================

INSERT INTO forum.tags (tag_id, name)
VALUES (11, 'discussion');
INSERT INTO forum.tags (tag_id, name)
VALUES (10, 'in-depth');
INSERT INTO forum.tags (tag_id, name)
VALUES (12, 'opinion');
INSERT INTO forum.tags (tag_id, name)
VALUES (9, 'review');
INSERT INTO forum.tags (tag_id, name)
VALUES (13, 'rules');

INSERT INTO forum.tags_posts (tag_id, post_id)
VALUES (13, 457);
INSERT INTO forum.tags_posts (tag_id, post_id)
VALUES (13, 458);
INSERT INTO forum.tags_posts (tag_id, post_id)
VALUES (11, 471);
INSERT INTO forum.tags_posts (tag_id, post_id)
VALUES (12, 471);
INSERT INTO forum.tags_posts (tag_id, post_id)
VALUES (11, 472);
INSERT INTO forum.tags_posts (tag_id, post_id)
VALUES (9, 473);
INSERT INTO forum.tags_posts (tag_id, post_id)
VALUES (10, 473);
INSERT INTO forum.tags_posts (tag_id, post_id)
VALUES (9, 474);



-- ============================================
-- Added views and likes
-- ============================================

-- Resolve post IDs for the four extra Westworld posts
SET @ww_p1 := (SELECT post_id
               FROM forum.posts
               WHERE folder_id = 31
                 AND title = 'Westworld Season 1 – why it still works as a complete story'
               LIMIT 1);

SET @ww_p2 := (SELECT post_id
               FROM forum.posts
               WHERE folder_id = 31
                 AND title = 'Did Westworld get too complicated or was it always like this?'
               LIMIT 1);

SET @ww_p3 := (SELECT post_id
               FROM forum.posts
               WHERE folder_id = 31
                 AND title = 'Favorite Westworld episodes for a focused rewatch'
               LIMIT 1);

SET @ww_p4 := (SELECT post_id
               FROM forum.posts
               WHERE folder_id = 31
                 AND title = 'Where did you stop watching Westworld (if you did) and why?'
               LIMIT 1);

-- --------------------------------------------
-- VIEWS FOR THE FOUR POSTS
-- --------------------------------------------
INSERT IGNORE INTO forum.posts_users_views (user_id, post_id, view_date)
VALUES
-- p1 views
(15, @ww_p1, '2025-10-06'),
(21, @ww_p1, '2025-10-06'),
(29, @ww_p1, '2025-10-07'),
(34, @ww_p1, '2025-10-07'),
(42, @ww_p1, '2025-10-08'),

-- p2 views
(17, @ww_p2, '2025-01-13'),
(23, @ww_p2, '2025-01-13'),
(30, @ww_p2, '2025-01-14'),
(39, @ww_p2, '2025-01-14'),
(44, @ww_p2, '2025-01-15'),

-- p3 views
(19, @ww_p3, '2025-03-04'),
(25, @ww_p3, '2025-03-04'),
(31, @ww_p3, '2025-03-05'),
(36, @ww_p3, '2025-03-05'),
(41, @ww_p3, '2025-03-06'),

-- p4 views
(16, @ww_p4, '2025-05-19'),
(22, @ww_p4, '2025-05-19'),
(28, @ww_p4, '2025-05-20'),
(35, @ww_p4, '2025-05-20'),
(40, @ww_p4, '2025-05-21');
