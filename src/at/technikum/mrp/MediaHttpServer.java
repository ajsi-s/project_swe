
package at.technikum.mrp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MediaHttpServer {

    private static final ObjectMapper JSON = new ObjectMapper();

    // Stores active login tokens and the corresponding usernames.
    private static final Map<String, String> activeTokens =
            new ConcurrentHashMap<>();

    public static void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/media", MediaHttpServer::handle);
        server.createContext("/ratings", MediaHttpServer::handleRatings);
        server.createContext("/users", MediaHttpServer::handleUsers);

        server.start();

        System.out.println("Media API started at http://localhost:8080/media");
        System.out.println("Ratings API started at http://localhost:8080/ratings");
        System.out.println("Users API started at http://localhost:8080/users");
    }

    private static void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            MediaRepository repository = new MediaRepository();

            if (path.equals("/media") || path.equals("/media/")) {
                if (method.equals("GET")) {
                    send(exchange, 200,
                            repository.findAll().stream()
                                    .map(MediaHttpServer::toJson).toList());

                } else if (method.equals("POST")) {
                    JsonNode body = JSON.readTree(exchange.getRequestBody());

                    int id = repository.create(
                            requiredText(body, "title"),
                            requiredText(body, "genre"),
                            requiredType(body),
                            optionalInt(body, "duration"),
                            optionalInt(body, "seasons")
                    );

                    send(exchange, 201, Map.of("id", id));

                } else {
                    methodNotAllowed(exchange, "GET, POST");
                }

                return;
            }

            if (!path.matches("/media/[1-9][0-9]*")) {
                send(exchange, 404, Map.of("error", "Route not found"));
                return;
            }

            int id = Integer.parseInt(path.substring("/media/".length()));

            switch (method) {
                case "GET" -> {
                    MediaEntry entry = repository.findById(id);

                    if (entry == null) {
                        send(exchange, 404,
                                Map.of("error", "Media entry not found"));
                    } else {
                        send(exchange, 200, toJson(entry));
                    }
                }

                case "PUT" -> {
                    JsonNode body = JSON.readTree(exchange.getRequestBody());

                    MediaEntry entry = repository.update(
                            id,
                            requiredText(body, "title"),
                            requiredText(body, "genre"),
                            requiredType(body),
                            optionalInt(body, "duration"),
                            optionalInt(body, "seasons")
                    );

                    if (entry == null) {
                        send(exchange, 404,
                                Map.of("error", "Media entry not found"));
                    } else {
                        send(exchange, 200, toJson(entry));
                    }
                }

                case "DELETE" -> {
                    if (repository.delete(id)) {
                        sendEmpty(exchange, 204);
                    } else {
                        send(exchange, 404,
                                Map.of("error", "Media entry not found"));
                    }
                }

                default -> methodNotAllowed(exchange, "GET, PUT, DELETE");
            }

        } catch (IllegalArgumentException e) {
            send(exchange, 400, Map.of("error", e.getMessage()));

        } catch (JsonProcessingException e) {
            send(exchange, 400, Map.of("error", "Invalid JSON body"));

        } catch (SQLException e) {
            e.printStackTrace();
            send(exchange, 500, Map.of("error", "Database request failed"));

        } finally {
            exchange.close();
        }
    }

    private static void handleRatings(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            RatingRepository repository = new RatingRepository();

            if (path.equals("/ratings") || path.equals("/ratings/")) {
                if (method.equals("GET")) {
                    send(exchange, 200, repository.findAll());

                } else if (method.equals("POST")) {

                    Integer userId = authenticatedUserId(exchange);

                    if (userId == null) {
                        send(exchange, 401,
                                Map.of("error", "Authentication required"));
                        return;
                    }

                    JsonNode body = JSON.readTree(exchange.getRequestBody());

                    int mediaId = requiredPositiveInt(body, "mediaId");
                    int stars = requiredPositiveInt(body, "stars");

                    if (stars > 5) {
                        throw new IllegalArgumentException(
                                "Stars must be between 1 and 5");
                    }

                    int id = repository.create(userId, mediaId, stars);

                    send(exchange, 201, Map.of("id", id));

                } else {
                    methodNotAllowed(exchange, "GET, POST");
                }

                return;
            }

            if (!path.matches("/ratings/[1-9][0-9]*")) {
                send(exchange, 404, Map.of("error", "Route not found"));
                return;
            }

            int id = Integer.parseInt(path.substring("/ratings/".length()));

            if (method.equals("DELETE")) {

                Integer userId = authenticatedUserId(exchange);

                if (userId == null) {
                    send(exchange, 401,
                            Map.of("error", "Authentication required"));
                    return;
                }

                if (repository.delete(id, userId)) {
                    sendEmpty(exchange, 204);
                } else {
                    send(exchange, 404,
                            Map.of("error", "Rating not found"));
                }

            } else {
                methodNotAllowed(exchange, "DELETE");
            }

        } catch (IllegalArgumentException e) {
            send(exchange, 400, Map.of("error", e.getMessage()));

        } catch (JsonProcessingException e) {
            send(exchange, 400, Map.of("error", "Invalid JSON body"));

        } catch (SQLException e) {
            e.printStackTrace();
            send(exchange, 500, Map.of("error", "Database request failed"));

        } finally {
            exchange.close();
        }
    }

    // Handles user registration and login.
    private static void handleUsers(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if (!method.equals("POST")) {
                methodNotAllowed(exchange, "POST");
                return;
            }

            UserRepository repository = new UserRepository();

            JsonNode body = JSON.readTree(exchange.getRequestBody());

            String username = requiredText(body, "username");
            String password = requiredText(body, "password");

            if (path.equals("/users/register")) {
                int id = repository.register(username, password);

                send(exchange, 201, Map.of("id", id));

            } else if (path.equals("/users/login")) {

                if (repository.login(username, password)) {
                    String token = UUID.randomUUID().toString();

                    activeTokens.put(token, username);

                    send(exchange, 200, Map.of("token", token));

                } else {
                    send(exchange, 401,
                            Map.of("error", "Invalid username or password"));
                }

            } else {
                send(exchange, 404, Map.of("error", "Route not found"));
            }

        } catch (IllegalArgumentException e) {
            send(exchange, 400, Map.of("error", e.getMessage()));

        } catch (JsonProcessingException e) {
            send(exchange, 400, Map.of("error", "Invalid JSON body"));

        } catch (SQLException e) {
            e.printStackTrace();
            send(exchange, 500, Map.of("error", "Database request failed"));

        } finally {
            exchange.close();
        }
    }

    // Gets the user ID from the login token.
    private static Integer authenticatedUserId(HttpExchange exchange)
            throws SQLException {

        String authorization =
                exchange.getRequestHeaders().getFirst("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }

        String token = authorization.substring(7).trim();

        String username = activeTokens.get(token);

        if (username == null) {
            return null;
        }

        int userId = new UserRepository().findIdByUsername(username);

        if (userId == -1) {
            return null;
        }

        return userId;
    }

    private static int requiredPositiveInt(JsonNode body, String field) {
        JsonNode value = body == null ? null : body.get(field);

        if (value == null || !value.canConvertToInt() || value.asInt() <= 0) {
            throw new IllegalArgumentException(
                    "Field '" + field + "' must be a positive integer");
        }

        return value.asInt();
    }

    private static Map<String, Object> toJson(MediaEntry entry) {
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("id", entry.getId());
        result.put("title", entry.getTitle());
        result.put("genre", entry.getGenre());
        result.put("mediaType", entry.getType());

        if (entry instanceof Movie movie) {
            result.put("duration", movie.getDuration());
        }

        if (entry instanceof Series series) {
            result.put("seasons", series.getSeasons());
        }

        return result;
    }

    private static String requiredText(JsonNode body, String field) {
        JsonNode value = body == null ? null : body.get(field);

        if (value == null || !value.isTextual() || value.asText().isBlank()) {
            throw new IllegalArgumentException(
                    "Field '" + field + "' is required");
        }

        return value.asText();
    }

    private static String requiredType(JsonNode body) {
        String type = requiredText(body, "mediaType");

        if (!type.equalsIgnoreCase("Movie")
                && !type.equalsIgnoreCase("Series")) {
            throw new IllegalArgumentException(
                    "mediaType must be Movie or Series");
        }

        return type;
    }

    private static Integer optionalInt(JsonNode body, String field) {
        JsonNode value = body == null ? null : body.get(field);

        if (value == null || value.isNull()) {
            return null;
        }

        if (!value.canConvertToInt() || value.asInt() < 0) {
            throw new IllegalArgumentException(
                    "Field '" + field + "' must be a non-negative integer");
        }

        return value.asInt();
    }

    private static void methodNotAllowed(
            HttpExchange exchange, String allowed) throws IOException {

        exchange.getResponseHeaders().set("Allow", allowed);
        send(exchange, 405, Map.of("error", "Method not allowed"));
    }

    private static void send(
            HttpExchange exchange, int status, Object value) throws IOException {

        byte[] bytes = JSON.writeValueAsBytes(value);

        exchange.getResponseHeaders().set(
                "Content-Type", "application/json; charset=utf-8");

        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
    }

    private static void sendEmpty(
            HttpExchange exchange, int status) throws IOException {

        exchange.sendResponseHeaders(status, -1);
    }
}
