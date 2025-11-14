START TRANSACTION;

-- ========= OPTION A: allow underscores/dots/hyphens in username (run once) =========
-- Uncomment next two statements if you want to keep usernames with underscores (e.g., alice_admin)
-- ALTER TABLE users DROP CONSTRAINT chk_username_character_length_type;
-- ALTER TABLE users ADD CONSTRAINT chk_username_character_length_type
--   CHECK (username REGEXP '^[A-Za-z][A-Za-z0-9._-]{3,49}$');

-- ========= OPTION B: keep strict alnum usernames (no changes to CHECK) =========
-- If using strict rule, we'll insert alnum usernames below (see USERS section).

-- ========= FOLDERS (tree; idempotent upserts) =========
-- Roots
INSERT INTO folders (parent_id, name, slug)
VALUES (NULL, 'Uncategorized', 'uncategorized')
ON DUPLICATE KEY UPDATE name = VALUES(name);
INSERT INTO folders (parent_id, name, slug)
VALUES (NULL, 'Movies', 'movies')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Children under Movies (guarded)
INSERT INTO folders (parent_id, name, slug)
SELECT p.folder_id, 'Reviews', 'reviews'
FROM folders p
WHERE p.slug='movies'
  AND NOT EXISTS (
    SELECT 1 FROM folders c WHERE c.parent_id = p.folder_id AND c.slug = 'reviews'
);

INSERT INTO folders (parent_id, name, slug)
SELECT p.folder_id, 'News', 'news'
FROM folders p
WHERE p.slug='movies'
  AND NOT EXISTS (
    SELECT 1 FROM folders c WHERE c.parent_id = p.folder_id AND c.slug = 'news'
);

INSERT INTO folders (parent_id, name, slug)
SELECT p.folder_id, 'Sci-Fi', 'sci-fi'
FROM folders p
WHERE p.slug='movies'
  AND NOT EXISTS (
    SELECT 1 FROM folders c WHERE c.parent_id = p.folder_id AND c.slug = 'sci-fi'
);

INSERT INTO folders (parent_id, name, slug)
SELECT p.folder_id, 'Classics', 'classics'
FROM folders p
WHERE p.slug='movies'
  AND NOT EXISTS (
    SELECT 1 FROM folders c WHERE c.parent_id = p.folder_id AND c.slug = 'classics'
);

-- ========= USERS =========
-- Use ONE of these blocks:

-- A) If you ENABLED the relaxed username CHECK above (allows _ . -):
INSERT INTO users (first_name, last_name, username, email, password, is_admin, phone, avatar_url, is_blocked, is_deleted)
VALUES
    ('Alice', 'Wright',  'alice_admin', 'alice@cinehub.example', '$2a$10$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 1, '555-1001', NULL, 0, 0),
    ('Marco', 'Silva',   'marco01',     'marco@cinehub.example', '$2a$10$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, NULL,       NULL, 0, 0),
    ('Chloe', 'White',   'chloe_fox',   'chloe@cinehub.example', '$2a$10$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, NULL,       NULL, 0, 0),
    ('Ethan', 'Reed',    'ethanr',      'ethan@cinehub.example', '$2a$10$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, NULL,       NULL, 0, 0),
    ('Kevin', 'Black',   'kevin_black', 'kevin@cinehub.example', '$2a$10$CZE9.LfucJLVP/hlp6IVbuoFUFE1scio7eYPI4145i1wGU4JmukVi', 0, NULL,       NULL, 1, 0)
ON DUPLICATE KEY UPDATE
                     first_name=VALUES(first_name), last_name=VALUES(last_name);

-- B) If you KEEP the strict alnum rule (no _ . -), comment the block above and use this:
-- INSERT INTO users (first_name, last_name, username, email, password, is_admin, phone, avatar_url, is_blocked, is_deleted)
-- VALUES
--   ('Alice', 'Wright',  'aliceadmin', 'alice@cinehub.example', '$2y$10$hashAlice', 1, '555-1001', NULL, 0, 0),
--   ('Marco', 'Silva',   'marco01',    'marco@cinehub.example', '$2y$10$hashMarco', 0, NULL,       NULL, 0, 0),
--   ('Chloe', 'White',   'chloefox',   'chloe@cinehub.example', '$2y$10$hashChloe', 0, NULL,       NULL, 0, 0),
--   ('Ethan', 'Reed',    'ethanr',     'ethan@cinehub.example', '$2y$10$hashEthan', 0, NULL,       NULL, 0, 0),
--   ('Kevin', 'Black',   'kevinblack', 'kevin@cinehub.example', '$2y$10$hashKevin', 0, NULL,       NULL, 1, 0)
-- ON DUPLICATE KEY UPDATE
--   first_name=VALUES(first_name), last_name=VALUES(last_name);

-- ========= TAGS (idempotent) =========
INSERT INTO tags (name) VALUES
                            ('sci-fi'), ('christopher-nolan'), ('interstellar'), ('oscar-buzz'),
                            ('classic'), ('hitchcock'), ('rear-window'), ('trailer'), ('review')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- ========= POSTS (titles 16–64, content 32–8192) =========
-- Guard each insert with NOT EXISTS to avoid duplicates by title per author
-- 1) Reviews by Alice
INSERT INTO posts (user_id, title, content, folder_id)
SELECT u.user_id,
       'Interstellar Rewatch: What Still Hits Hard',
       'Revisiting Interstellar in 2025: why the docking set-piece and father–daughter thread still land, and what aged less gracefully.',
       f.folder_id
FROM users u, folders f
WHERE u.username IN ('alice_admin','aliceadmin')  -- supports both username schemes
  AND f.slug='reviews'
  AND NOT EXISTS (
    SELECT 1 FROM posts p WHERE p.title='Interstellar Rewatch: What Still Hits Hard' AND p.user_id=u.user_id
);

-- 2) Sci-Fi list by Marco
INSERT INTO posts (user_id, title, content, folder_id)
SELECT u.user_id,
       'Top 10 Sci-Fi Space Movies to Watch Next',
       'From hard science epics to pulpy adventures—ten space movies worth your time, with quick notes on tone and pacing.',
       f.folder_id
FROM users u, folders f
WHERE u.username='marco01' AND f.slug='sci-fi'
  AND NOT EXISTS (
    SELECT 1 FROM posts p WHERE p.title='Top 10 Sci-Fi Space Movies to Watch Next' AND p.user_id=u.user_id
);

-- 3) News by Chloe
INSERT INTO posts (user_id, title, content, folder_id)
SELECT u.user_id,
       'Oscar Buzz: Early Contenders for Best Picture',
       'A snapshot of the current field: festival darlings, studio crowd-pleasers, and dark horses poised to surge.',
       f.folder_id
FROM users u, folders f
WHERE u.username IN ('chloe_fox','chloefox') AND f.slug='news'
  AND NOT EXISTS (
    SELECT 1 FROM posts p WHERE p.title='Oscar Buzz: Early Contenders for Best Picture' AND p.user_id=u.user_id
);

-- 4) Classics by Ethan
INSERT INTO posts (user_id, title, content, folder_id)
SELECT u.user_id,
       'Rear Window: Why Hitchcock’s Thriller Still Works',
       'Voyeurism, visual storytelling, and the apartment-as-diagram: how Rear Window keeps tension tight without leaving the courtyard.',
       f.folder_id
FROM users u, folders f
WHERE u.username='ethanr' AND f.slug='classics'
  AND NOT EXISTS (
    SELECT 1 FROM posts p WHERE p.title='Rear Window: Why Hitchcock’s Thriller Still Works' AND p.user_id=u.user_id
);

-- 5) Movies root by Alice
INSERT INTO posts (user_id, title, content, folder_id)
SELECT u.user_id,
       'How to Start a Film Club in Your City',
       'Practical steps: finding a venue, curating themes, licensing considerations, and keeping discussions welcoming and lively.',
       f.folder_id
FROM users u, folders f
WHERE u.username IN ('alice_admin','aliceadmin') AND f.slug='movies'
  AND NOT EXISTS (
    SELECT 1 FROM posts p WHERE p.title='How to Start a Film Club in Your City' AND p.user_id=u.user_id
);

-- 6) Uncategorized by Alice
INSERT INTO posts (user_id, title, content, folder_id)
SELECT u.user_id,
       'Read This First: Forum Rules and Etiquette',
       'Stay on topic, credit sources, mark spoilers, and keep criticism about the work—not the person. Welcome aboard!',
       f.folder_id
FROM users u, folders f
WHERE u.username IN ('alice_admin','aliceadmin') AND f.slug='uncategorized'
  AND NOT EXISTS (
    SELECT 1 FROM posts p WHERE p.title='Read This First: Forum Rules and Etiquette' AND p.user_id=u.user_id
);

-- ========= TAGS ↔ POSTS (idempotent) =========
-- Use INSERT IGNORE to avoid duplicate (tag_id, post_id) PK errors on re-runs
INSERT IGNORE INTO tags_posts (tag_id, post_id)
SELECT t.tag_id, p.post_id
FROM tags t, posts p
WHERE t.name IN ('sci-fi','christopher-nolan','interstellar','review')
  AND p.title='Interstellar Rewatch: What Still Hits Hard';

INSERT IGNORE INTO tags_posts (tag_id, post_id)
SELECT t.tag_id, p.post_id
FROM tags t, posts p
WHERE t.name IN ('sci-fi','trailer')
  AND p.title='Top 10 Sci-Fi Space Movies to Watch Next';

INSERT IGNORE INTO tags_posts (tag_id, post_id)
SELECT t.tag_id, p.post_id
FROM tags t, posts p
WHERE t.name IN ('oscar-buzz')
  AND p.title='Oscar Buzz: Early Contenders for Best Picture';

INSERT IGNORE INTO tags_posts (tag_id, post_id)
SELECT t.tag_id, p.post_id
FROM tags t, posts p
WHERE t.name IN ('classic','hitchcock','rear-window','review')
  AND p.title='Rear Window: Why Hitchcock’s Thriller Still Works';

-- ========= COMMENTS (idempotent via NOT EXISTS) =========
INSERT INTO comments (post_id, user_id, content)
SELECT p.post_id, u.user_id,
       'The docking sequence in IMAX still gives me goosebumps. Great write-up!'
FROM posts p, users u
WHERE p.title='Interstellar Rewatch: What Still Hits Hard'
  AND u.username IN ('marco01')
  AND NOT EXISTS (
    SELECT 1 FROM comments c WHERE c.post_id=p.post_id AND c.user_id=u.user_id
                               AND c.content='The docking sequence in IMAX still gives me goosebumps. Great write-up!'
);

INSERT INTO comments (post_id, user_id, content)
SELECT p.post_id, u.user_id,
       'Love the point about time dilation as character drama, not just sci-fi dressing.'
FROM posts p, users u
WHERE p.title='Interstellar Rewatch: What Still Hits Hard'
  AND u.username IN ('chloe_fox','chloefox')
  AND NOT EXISTS (
    SELECT 1 FROM comments c WHERE c.post_id=p.post_id AND c.user_id=u.user_id
                               AND c.content='Love the point about time dilation as character drama, not just sci-fi dressing.'
);

INSERT INTO comments (post_id, user_id, content)
SELECT p.post_id, u.user_id,
       'Curious which indie picks survive the campaign season—marketing budgets matter.'
FROM posts p, users u
WHERE p.title='Oscar Buzz: Early Contenders for Best Picture'
  AND u.username IN ('alice_admin','aliceadmin')
  AND NOT EXISTS (
    SELECT 1 FROM comments c WHERE c.post_id=p.post_id AND c.user_id=u.user_id
                               AND c.content='Curious which indie picks survive the campaign season—marketing budgets matter.'
);

INSERT INTO comments (post_id, user_id, content)
SELECT p.post_id, u.user_id,
       'It’s wild how much suspense he gets from blocking and eyelines alone.'
FROM posts p, users u
WHERE p.title='Rear Window: Why Hitchcock’s Thriller Still Works'
  AND u.username='marco01'
  AND NOT EXISTS (
    SELECT 1 FROM comments c WHERE c.post_id=p.post_id AND c.user_id=u.user_id
                               AND c.content='It’s wild how much suspense he gets from blocking and eyelines alone.'
);

-- ========= LIKES (idempotent via IGNORE) =========
INSERT IGNORE INTO likes (user_id, post_id)
SELECT u.user_id, p.post_id
FROM users u, posts p
WHERE u.username IN ('alice_admin','aliceadmin','marco01','chloe_fox','chloefox')
  AND p.title='Interstellar Rewatch: What Still Hits Hard';

INSERT IGNORE INTO likes (user_id, post_id)
SELECT u.user_id, p.post_id
FROM users u, posts p
WHERE u.username IN ('alice_admin','aliceadmin','ethanr')
  AND p.title='Top 10 Sci-Fi Space Movies to Watch Next';

INSERT IGNORE INTO likes (user_id, post_id)
SELECT u.user_id, p.post_id
FROM users u, posts p
WHERE u.username IN ('chloe_fox','chloefox','alice_admin','aliceadmin','marco01')
  AND p.title='Oscar Buzz: Early Contenders for Best Picture';

INSERT IGNORE INTO likes (user_id, post_id)
SELECT u.user_id, p.post_id
FROM users u, posts p
WHERE u.username IN ('alice_admin','aliceadmin','ethanr')
  AND p.title='Rear Window: Why Hitchcock’s Thriller Still Works';

INSERT IGNORE INTO likes (user_id, post_id)
SELECT u.user_id, p.post_id
FROM users u, posts p
WHERE u.username IN ('chloe_fox','chloefox')
  AND p.title='How to Start a Film Club in Your City';

COMMIT;
