-- Run this in the media_ratings_db database. It creates the tables from console.sql.
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS media_entries (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    media_type VARCHAR(20) NOT NULL CHECK (media_type IN ('Movie', 'Series')),
    duration INT,
    seasons INT
);

CREATE TABLE IF NOT EXISTS ratings (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id),
    media_id INT NOT NULL REFERENCES media_entries(id),
    stars INT NOT NULL CHECK (stars BETWEEN 1 AND 5)
);

INSERT INTO media_entries (title, genre, media_type, duration, seasons)
SELECT seed.title, seed.genre, seed.media_type, seed.duration, seed.seasons
FROM (VALUES
    ('Interstellar', 'Science Fiction', 'Movie', 169, NULL),
    ('Inception', 'Science Fiction', 'Movie', 148, NULL),
    ('Wednesday', 'Mystery', 'Series', NULL, 2)
) AS seed(title, genre, media_type, duration, seasons)
WHERE NOT EXISTS (SELECT 1 FROM media_entries);
