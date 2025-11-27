USE forum;

-- =========================
-- FOLDERS (Home: Movies, Series)
-- =========================
INSERT INTO forum.folders (folder_id, parent_id, name, slug, created_at)
VALUES (1, NULL, 'Movies', 'movies', '2025-11-20 10:00:00'),
       (2, NULL, 'Series', 'series', '2025-11-20 10:05:00'),
       (3, 1, 'Action Movies', 'action-movies', '2025-11-20 10:10:00'),
       (4, 1, 'Drama Movies', 'drama-movies', '2025-11-20 10:15:00'),
       (5, 1, 'Comedy Movies', 'comedy-movies', '2025-11-20 10:20:00'),
       (6, 1, 'Sci-Fi Movies', 'sci-fi-movies', '2025-11-20 10:25:00'),
       (7, 2, 'Drama Series', 'drama-series', '2025-11-20 10:30:00'),
       (8, 2, 'Comedy Series', 'comedy-series', '2025-11-20 10:35:00'),
       (9, 2, 'Sci-Fi Series', 'sci-fi-series', '2025-11-20 10:40:00'),
       (10, 2, 'Animated Series', 'animated-series', '2025-11-20 10:45:00');

-- =========================
-- TAGS (movie/series related, all lowercase)
-- =========================
INSERT INTO forum.tags (tag_id, name)
VALUES (1, 'action'),
       (2, 'drama'),
       (3, 'comedy'),
       (4, 'sci-fi'),
       (5, 'thriller'),
       (6, 'romance'),
       (7, 'animation'),
       (8, 'superhero'),
       (9, 'mystery'),
       (10, 'crime');

-- =========================
-- USERS (22 total, first 3 admins)
-- =========================
INSERT INTO forum.users
(user_id, first_name, last_name, username, email, password, is_admin, created_at, phone, avatar_url, is_blocked,
 is_deleted)
VALUES (1, 'Alice', 'Adminson', 'alice.admin', 'alice.admin@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 1, '2025-11-01 09:00:00', NULL, NULL, 0, 0),
       (2, 'Robert', 'Adminson', 'robert.admin', 'robert.admin@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 1, '2025-11-01 09:10:00', NULL, NULL, 0, 0),
       (3, 'Caroline', 'Adminson', 'caroline.admin', 'caroline.admin@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 1, '2025-11-01 09:20:00', NULL, NULL, 0, 0),
       (4, 'Daniel', 'Userton', 'daniel.user', 'daniel.user@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, '2025-11-02 10:00:00', NULL, NULL, 0, 0),
       (5, 'Emilia', 'Userton', 'emilia.user', 'emilia.user@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, '2025-11-02 10:10:00', NULL, NULL, 0, 0),
       (6, 'George', 'Userton', 'george.user', 'george.user@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, '2025-11-02 10:20:00', NULL, NULL, 0, 0),
       (7, 'Helena', 'Userton', 'helena.user', 'helena.user@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, '2025-11-02 10:30:00', NULL, NULL, 0, 0),
       (8, 'Isaac', 'Userton', 'isaac.user', 'isaac.user@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, '2025-11-02 10:40:00', NULL, NULL, 0, 0),
       (9, 'Julia', 'Userton', 'julia.user', 'julia.user@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, '2025-11-02 10:50:00', NULL, NULL, 0, 0),
       (10, 'Kevin', 'Userton', 'kevin.user', 'kevin.user@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, '2025-11-03 11:00:00', NULL, NULL, 0, 0),
       (11, 'Laura', 'Userton', 'laura.user', 'laura.user@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, '2025-11-03 11:10:00', NULL, NULL, 0, 0),
       (12, 'Michael', 'Userton', 'michael.user', 'michael.user@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, '2025-11-03 11:20:00', NULL, NULL, 0, 0),
       (13, 'Natalia', 'Userton', 'natalia.user', 'natalia.user@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, '2025-11-03 11:30:00', NULL, NULL, 0, 0),
       (14, 'Oliver', 'Userton', 'oliver.user', 'oliver.user@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, '2025-11-03 11:40:00', NULL, NULL, 0, 0),
       (15, 'Patricia', 'Userton', 'patricia.user', 'patricia.user@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, '2025-11-03 11:50:00', NULL, NULL, 0, 0),
       (16, 'Quentin', 'Userton', 'quentin.user', 'quentin.user@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, '2025-11-04 12:00:00', NULL, NULL, 0, 0),
       (17, 'Rebecca', 'Userton', 'rebecca.user', 'rebecca.user@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, '2025-11-04 12:10:00', NULL, NULL, 0, 0),
       (18, 'Samuel', 'Userton', 'samuel.user', 'samuel.user@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, '2025-11-04 12:20:00', NULL, NULL, 0, 0),
       (19, 'Tatiana', 'Userton', 'tatiana.user', 'tatiana.user@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, '2025-11-04 12:30:00', NULL, NULL, 0, 0),
       (20, 'Victor', 'Userton', 'victor.user', 'victor.user@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, '2025-11-04 12:40:00', NULL, NULL, 0, 0),
       (21, 'William', 'Userton', 'william.user', 'william.user@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, '2025-11-04 12:50:00', NULL, NULL, 0, 0),
       (22, 'Yvonne', 'Userton', 'yvonne.user', 'yvonne.user@example.com',
        'a$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, '2025-11-04 13:00:00', NULL, NULL, 0, 0);

-- =========================
-- POSTS (50 total, 5 per folder, all movies/series themed)
-- =========================
INSERT INTO forum.posts
(post_id, user_id, title, content, created_at, updated_at, deleted_at, is_deleted, folder_id)
VALUES
-- Folder 1: Movies (general) posts 1–5
(1, 4, 'Movies general discussion 01',
 'Seeded discussion content about movies and series for testing the forum application. Topic focuses on general movie talk.',
 '2025-11-18 09:00:00', NULL, NULL, 0, 1),
(2, 5, 'Movies general discussion 02',
 'Seeded discussion content about movies and series for testing the forum application. Topic focuses on favorite directors.',
 '2025-11-19 09:10:00', NULL, NULL, 0, 1),
(3, 6, 'Movies general discussion 03',
 'Seeded discussion content about movies and series for testing the forum application. Topic focuses on classic cinema.',
 '2025-11-20 09:20:00', NULL, NULL, 0, 1),
(4, 7, 'Movies general discussion 04',
 'Seeded discussion content about movies and series for testing the forum application. Topic focuses on soundtracks.',
 '2025-11-21 09:30:00', NULL, NULL, 0, 1),
(5, 8, 'Movies general discussion 05',
 'Seeded discussion content about movies and series for testing the forum application. Topic focuses on rewatchable films.',
 '2025-11-22 09:40:00', NULL, NULL, 0, 1),

-- Folder 3: Action Movies posts 6–10
(6, 9, 'Action movie talk 01: big set pieces',
 'Seeded discussion content about action movies, stunts and set pieces, for testing the forum application.',
 '2025-11-18 10:00:00', NULL, NULL, 0, 3),
(7, 10, 'Action movie talk 02: 90s favorites',
 'Seeded discussion content about classic 90s action movies for testing the forum application.',
 '2025-11-19 10:10:00', NULL, NULL, 0, 3),
(8, 11, 'Action movie talk 03: modern blockbusters',
 'Seeded discussion content about modern blockbuster action films for testing the forum application.',
 '2025-11-20 10:20:00', NULL, NULL, 0, 3),
(9, 12, 'Action movie talk 04: martial arts focus',
 'Seeded discussion content about martial arts action movies for testing the forum application.',
 '2025-11-21 10:30:00', NULL, NULL, 0, 3),
(10, 13, 'Action movie talk 05: underrated gems',
 'Seeded discussion content about underrated action movies for testing the forum application.',
 '2025-11-22 10:40:00', NULL, NULL, 0, 3),

-- Folder 4: Drama Movies posts 11–15
(11, 14, 'Drama movie discussion 01: character studies',
 'Seeded discussion content about drama movies and character studies for testing the forum application.',
 '2025-11-18 11:00:00', NULL, NULL, 0, 4),
(12, 15, 'Drama movie discussion 02: award winners',
 'Seeded discussion content about award-winning drama films for testing the forum application.',
 '2025-11-19 11:10:00', NULL, NULL, 0, 4),
(13, 16, 'Drama movie discussion 03: slow burns',
 'Seeded discussion content about slow-burn drama movies for testing the forum application.',
 '2025-11-20 11:20:00', NULL, NULL, 0, 4),
(14, 17, 'Drama movie discussion 04: based on true stories',
 'Seeded discussion content about dramas based on true stories for testing the forum application.',
 '2025-11-21 11:30:00', NULL, NULL, 0, 4),
(15, 18, 'Drama movie discussion 05: emotional endings',
 'Seeded discussion content about emotional drama movie endings for testing the forum application.',
 '2025-11-22 11:40:00', NULL, NULL, 0, 4),

-- Folder 5: Comedy Movies posts 16–20
(16, 19, 'Comedy movie chat 01: laugh out loud',
 'Seeded discussion content about comedy movies and funniest scenes for testing the forum application.',
 '2025-11-18 12:00:00', NULL, NULL, 0, 5),
(17, 20, 'Comedy movie chat 02: classic comedies',
 'Seeded discussion content about classic comedy movies for testing the forum application.',
 '2025-11-19 12:10:00', NULL, NULL, 0, 5),
(18, 4, 'Comedy movie chat 03: modern hits',
 'Seeded discussion content about modern popular comedy films for testing the forum application.',
 '2025-11-20 12:20:00', NULL, NULL, 0, 5),
(19, 5, 'Comedy movie chat 04: ensemble casts',
 'Seeded discussion content about ensemble comedy movies for testing the forum application.',
 '2025-11-21 12:30:00', NULL, NULL, 0, 5),
(20, 6, 'Comedy movie chat 05: dark comedies',
 'Seeded discussion content about dark comedy films for testing the forum application.',
 '2025-11-22 12:40:00', NULL, NULL, 0, 5),

-- Folder 6: Sci-Fi Movies posts 21–25
(21, 7, 'Sci-fi movie talk 01: space epics',
 'Seeded discussion content about sci-fi space epics and classics for testing the forum application.',
 '2025-11-18 13:00:00', NULL, NULL, 0, 6),
(22, 8, 'Sci-fi movie talk 02: time travel',
 'Seeded discussion content about time travel science fiction movies for testing the forum application.',
 '2025-11-19 13:10:00', NULL, NULL, 0, 6),
(23, 9, 'Sci-fi movie talk 03: cerebral sci-fi',
 'Seeded discussion content about thoughtful and cerebral sci-fi films for testing the forum application.',
 '2025-11-20 13:20:00', NULL, NULL, 0, 6),
(24, 10, 'Sci-fi movie talk 04: dystopian worlds',
 'Seeded discussion content about dystopian sci-fi movies for testing the forum application.',
 '2025-11-21 13:30:00', NULL, NULL, 0, 6),
(25, 11, 'Sci-fi movie talk 05: robots and ai',
 'Seeded discussion content about robot and AI themed sci-fi films for testing the forum application.',
 '2025-11-22 13:40:00', NULL, NULL, 0, 6),

-- Folder 2: Series (general) posts 26–30
(26, 12, 'Series general discussion 01',
 'Seeded discussion content about TV series and streaming shows for testing the forum application.',
 '2025-11-18 14:00:00', NULL, NULL, 0, 2),
(27, 13, 'Series general discussion 02',
 'Seeded discussion content about current binge-worthy series for testing the forum application.',
 '2025-11-19 14:10:00', NULL, NULL, 0, 2),
(28, 14, 'Series general discussion 03',
 'Seeded discussion content about cliffhangers and finales in series for testing the forum application.',
 '2025-11-20 14:20:00', NULL, NULL, 0, 2),
(29, 15, 'Series general discussion 04',
 'Seeded discussion content about showrunners and writers in series for testing the forum application.',
 '2025-11-21 14:30:00', NULL, NULL, 0, 2),
(30, 16, 'Series general discussion 05',
 'Seeded discussion content about long-running TV series for testing the forum application.',
 '2025-11-22 14:40:00', NULL, NULL, 0, 2),

-- Folder 7: Drama Series posts 31–35
(31, 17, 'Drama series talk 01: prestige TV',
 'Seeded discussion content about prestige drama series for testing the forum application.',
 '2025-11-18 15:00:00', NULL, NULL, 0, 7),
(32, 18, 'Drama series talk 02: crime sagas',
 'Seeded discussion content about crime drama series for testing the forum application.',
 '2025-11-19 15:10:00', NULL, NULL, 0, 7),
(33, 19, 'Drama series talk 03: character-driven',
 'Seeded discussion content about character-driven drama series for testing the forum application.',
 '2025-11-20 15:20:00', NULL, NULL, 0, 7),
(34, 20, 'Drama series talk 04: slow burn shows',
 'Seeded discussion content about slow burn drama series for testing the forum application.',
 '2025-11-21 15:30:00', NULL, NULL, 0, 7),
(35, 4, 'Drama series talk 05: emotional finales',
 'Seeded discussion content about emotional drama series finales for testing the forum application.',
 '2025-11-22 15:40:00', NULL, NULL, 0, 7),

-- Folder 8: Comedy Series posts 36–40
(36, 5, 'Comedy series chat 01: sitcom classics',
 'Seeded discussion content about classic sitcom series for testing the forum application.',
 '2025-11-18 16:00:00', NULL, NULL, 0, 8),
(37, 6, 'Comedy series chat 02: modern sitcoms',
 'Seeded discussion content about modern sitcoms and streaming comedies for testing the forum application.',
 '2025-11-19 16:10:00', NULL, NULL, 0, 8),
(38, 7, 'Comedy series chat 03: mockumentaries',
 'Seeded discussion content about mockumentary style comedy series for testing the forum application.',
 '2025-11-20 16:20:00', NULL, NULL, 0, 8),
(39, 8, 'Comedy series chat 04: workplace comedies',
 'Seeded discussion content about workplace comedy shows for testing the forum application.',
 '2025-11-21 16:30:00', NULL, NULL, 0, 8),
(40, 9, 'Comedy series chat 05: comfort shows',
 'Seeded discussion content about comfort comedy series for testing the forum application.',
 '2025-11-22 16:40:00', NULL, NULL, 0, 8),

-- Folder 9: Sci-Fi Series posts 41–45
(41, 10, 'Sci-fi series talk 01: space operas',
 'Seeded discussion content about sci-fi space opera series for testing the forum application.',
 '2025-11-18 17:00:00', NULL, NULL, 0, 9),
(42, 11, 'Sci-fi series talk 02: mind-bending plots',
 'Seeded discussion content about mind-bending sci-fi series for testing the forum application.',
 '2025-11-19 17:10:00', NULL, NULL, 0, 9),
(43, 12, 'Sci-fi series talk 03: time travel shows',
 'Seeded discussion content about time travel sci-fi series for testing the forum application.',
 '2025-11-20 17:20:00', NULL, NULL, 0, 9),
(44, 13, 'Sci-fi series talk 04: dystopian futures',
 'Seeded discussion content about dystopian science fiction series for testing the forum application.',
 '2025-11-21 17:30:00', NULL, NULL, 0, 9),
(45, 14, 'Sci-fi series talk 05: anthology shows',
 'Seeded discussion content about sci-fi anthology series for testing the forum application.',
 '2025-11-22 17:40:00', NULL, NULL, 0, 9),

-- Folder 10: Animated Series posts 46–50
(46, 15, 'Animated series talk 01: adult animation',
 'Seeded discussion content about adult animated series for testing the forum application.',
 '2025-11-18 18:00:00', NULL, NULL, 0, 10),
(47, 16, 'Animated series talk 02: anime favourites',
 'Seeded discussion content about anime series and favorites for testing the forum application.',
 '2025-11-19 18:10:00', NULL, NULL, 0, 10),
(48, 17, 'Animated series talk 03: kids shows',
 'Seeded discussion content about animated kids series for testing the forum application.',
 '2025-11-20 18:20:00', NULL, NULL, 0, 10),
(49, 18, 'Animated series talk 04: iconic episodes',
 'Seeded discussion content about iconic episodes of animated series for testing the forum application.',
 '2025-11-21 18:30:00', NULL, NULL, 0, 10),
(50, 19, 'Animated series talk 05: art styles',
 'Seeded discussion content about visual art styles in animated series for testing the forum application.',
 '2025-11-22 18:40:00', NULL, NULL, 0, 10);

-- =========================
-- COMMENTS (on first 15 posts, all movie/series related)
-- =========================
INSERT INTO forum.comments
(comment_id, is_deleted, post_id, user_id, created_at, deleted_at, updated_at, content)
VALUES (1, b'0', 1, 5, '2025-11-23 09:00:00', NULL, NULL, 'Really like this general movie discussion thread.'),
       (2, b'0', 1, 6, '2025-11-23 09:10:00', NULL, NULL, 'Great starting list of favorite films.'),
       (3, b'0', 2, 7, '2025-11-23 09:20:00', NULL, NULL, 'I would add a few more directors to this.'),
       (4, b'0', 3, 8, '2025-11-23 09:30:00', NULL, NULL, 'Classic cinema deserves more love, nice post.'),
       (5, b'0', 4, 9, '2025-11-23 09:40:00', NULL, NULL, 'Movie soundtracks are such an underrated topic.'),
       (6, b'0', 5, 10, '2025-11-23 09:50:00', NULL, NULL, 'These rewatchable movies are my comfort picks.'),
       (7, b'0', 6, 11, '2025-11-23 10:00:00', NULL, NULL, 'Action set pieces you mentioned are incredible.'),
       (8, b'0', 7, 12, '2025-11-23 10:10:00', NULL, NULL, '90s action movies really hit different.'),
       (9, b'0', 8, 13, '2025-11-23 10:20:00', NULL, NULL, 'Modern action films have amazing visuals.'),
       (10, b'0', 9, 14, '2025-11-23 10:30:00', NULL, NULL, 'Martial arts movies are my favourite genre.'),
       (11, b'0', 10, 15, '2025-11-23 10:40:00', NULL, NULL, 'Some of these underrated action films are great.'),
       (12, b'0', 11, 16, '2025-11-23 10:50:00', NULL, NULL, 'Love a good character-driven drama movie.'),
       (13, b'0', 12, 17, '2025-11-23 11:00:00', NULL, NULL, 'Award winners list is spot on.'),
       (14, b'0', 13, 18, '2025-11-23 11:10:00', NULL, NULL, 'Slow burn dramas are absolutely my thing.'),
       (15, b'0', 14, 19, '2025-11-23 11:20:00', NULL, NULL, 'True story dramas always hit harder.'),
       (16, b'0', 15, 20, '2025-11-23 11:30:00', NULL, NULL, 'That emotional ending ruined me in a good way.'),
       (17, b'0', 26, 4, '2025-11-23 11:40:00', NULL, NULL, 'Great overview of current TV series trends.'),
       (18, b'0', 27, 5, '2025-11-23 11:50:00', NULL, NULL, 'Binge recommendations are really useful.'),
       (19, b'0', 31, 6, '2025-11-23 12:00:00', NULL, NULL, 'Prestige drama series are at an all-time high.'),
       (20, b'0', 36, 7, '2025-11-23 12:10:00', NULL, NULL, 'Sitcom classics are still hilarious today.');

-- =========================
-- LIKES (spread across various posts)
-- =========================
INSERT INTO forum.likes (post_id, user_id)
VALUES (1, 4),
       (1, 5),
       (1, 6),
       (2, 7),
       (2, 8),
       (3, 9),
       (3, 10),
       (3, 11),
       (4, 12),
       (4, 13),
       (5, 14),
       (5, 15),
       (5, 16),

       (6, 17),
       (6, 18),
       (7, 19),
       (7, 20),
       (8, 21),
       (8, 22),
       (9, 4),
       (9, 5),
       (10, 6),
       (10, 7),

       (11, 8),
       (11, 9),
       (12, 10),
       (12, 11),
       (13, 12),
       (13, 13),
       (14, 14),
       (14, 15),
       (15, 16),
       (15, 17),

       (26, 18),
       (26, 19),
       (27, 20),
       (27, 21),
       (28, 22),
       (29, 4),
       (30, 5),
       (31, 6),
       (36, 7),
       (41, 8),
       (46, 9),
       (50, 10);

-- =========================
-- POST VIEWS (past week and earlier)
-- =========================
INSERT INTO forum.posts_users_views
    (posts_users_views_id, user_id, post_id, view_date)
VALUES (1, 4, 1, '2025-11-19'),
       (2, 5, 1, '2025-11-20'),
       (3, 6, 2, '2025-11-21'),
       (4, 7, 3, '2025-11-21'),
       (5, 8, 3, '2025-11-22'),
       (6, 9, 4, '2025-11-22'),
       (7, 10, 5, '2025-11-23'),
       (8, 11, 6, '2025-11-23'),
       (9, 12, 7, '2025-11-24'),
       (10, 13, 8, '2025-11-24'),
       (11, 14, 9, '2025-11-25'),
       (12, 15, 10, '2025-11-25'),
       (13, 16, 11, '2025-11-26'),
       (14, 17, 12, '2025-11-26'),
       (15, 18, 13, '2025-11-27'),
       (16, 19, 26, '2025-11-20'),
       (17, 20, 27, '2025-11-21'),
       (18, 21, 31, '2025-11-22'),
       (19, 22, 36, '2025-11-23'),
       (20, 4, 41, '2025-11-24'),
       (21, 5, 46, '2025-11-25'),
       (22, 6, 50, '2025-11-26'),
       (23, 7, 28, '2025-11-18'),
       (24, 8, 15, '2025-11-18'),
       (25, 9, 22, '2025-11-19');

-- =========================
-- TAGS ↔ POSTS (movie/series themed)
-- =========================
INSERT INTO forum.tags_posts (post_id, tag_id)
VALUES
-- Movies general: drama / comedy / action mix
(1, 2),
(1, 3),
(2, 2),
(2, 5),
(3, 2),
(4, 5),
(5, 3),

-- Action Movies
(6, 1),
(6, 5),
(7, 1),
(8, 1),
(8, 8),
(9, 1),
(10, 1),

-- Drama Movies
(11, 2),
(12, 2),
(12, 10),
(13, 2),
(14, 2),
(14, 9),
(15, 2),

-- Comedy Movies
(16, 3),
(17, 3),
(18, 3),
(19, 3),
(19, 6),
(20, 3),

-- Sci-Fi Movies
(21, 4),
(22, 4),
(22, 9),
(23, 4),
(24, 4),
(24, 5),
(25, 4),

-- Series general: mix
(26, 2),
(26, 3),
(27, 2),
(28, 5),
(29, 9),
(30, 3),

-- Drama Series
(31, 2),
(31, 10),
(32, 2),
(32, 9),
(33, 2),
(34, 2),
(35, 2),

-- Comedy Series
(36, 3),
(37, 3),
(38, 3),
(39, 3),
(40, 3),

-- Sci-Fi Series
(41, 4),
(42, 4),
(42, 9),
(43, 4),
(44, 4),
(44, 5),
(45, 4),

-- Animated Series
(46, 7),
(47, 7),
(47, 4),
(48, 7),
(49, 7),
(50, 7);
