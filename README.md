# Projekt_SWE

Media Ratings Platform in plain Java. The project keeps the OOP models and now includes a PostgreSQL-backed HTTP API for media entries.

## Project structure

- `src/at/technikum/mrp`: models, database repository, and HTTP API
- `db/schema.sql`: creates the users, media_entries, and ratings tables and inserts sample media
- `pom.xml`: Java 17 build and Jackson/PostgreSQL dependencies

The API follows the example architecture from `ServerWithDB`: the HTTP handler routes requests, `MediaRepository` executes parameterized SQL, and the existing `Movie` and `Series` classes represent media.

## Run

1. Create a PostgreSQL database named `media_ratings_db`.
2. Run `db/schema.sql` against that database (for example, `psql -U postgres -d media_ratings_db -f db/schema.sql`).
3. Set connection variables if needed: `DB_URL` (default `jdbc:postgresql://localhost:5432/media_ratings_db`), `DB_USER` (default `postgres`), and `DB_PASSWORD` (default empty).
4. Import the Maven project in IntelliJ and run `at.technikum.mrp.Main`.

The server listens on `http://localhost:8080`.

## Media endpoints

| Method | Endpoint | Result |
| --- | --- | --- |
| GET | `/media` | List media entries |
| GET | `/media/{id}` | Read one entry |
| POST | `/media` | Create a movie or series |
| PUT | `/media/{id}` | Replace an entry |
| DELETE | `/media/{id}` | Delete an entry |

POST and PUT accept JSON such as `{"title":"Arrival","genre":"Science Fiction","mediaType":"Movie","duration":116}`. For a series, use `"mediaType":"Series"` and `"seasons":3`.

```sh
curl http://localhost:8080/media
curl -X POST http://localhost:8080/media -H 'Content-Type: application/json' \
  -d '{"title":"Arrival","genre":"Science Fiction","mediaType":"Movie","duration":116}'
```

The `users` and `ratings` tables are created from the supplied SQL, but API endpoints for them have not been added yet.
# project_swe
