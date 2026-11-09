package at.technikum.mrp;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class MovieHttpServer {

    public static void start(Movie movie) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/movies", exchange -> {
            String json = "[{\"title\":\"" + movie.getTitle() + "\",\"genre\":\"" + movie.getGenre() + "\",\"duration\":" + movie.getDuration() + "}]";
            byte[] response = json.getBytes();
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, 0);
            exchange.getResponseBody().write(response);
            exchange.close();
        });

        server.start();
    }
}



// umwandlung in bytes
//
