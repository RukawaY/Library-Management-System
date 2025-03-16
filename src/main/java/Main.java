import utils.ConnectConfig;
import utils.DatabaseConnector;

import java.util.logging.Logger;
import com.sun.net.httpserver.*;
import java.io.IOException;
import java.net.InetSocketAddress;

import Handlers.CardHandler;
import Handlers.BookHandler;
import Handlers.BorrowHandler;

public class Main {

    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {
        try {
            // connect config
            ConnectConfig conf = new ConnectConfig();
            log.info("Success to parse connect config. " + conf.toString());

            // database connector
            DatabaseConnector connector = new DatabaseConnector(conf);
            boolean connStatus = connector.connect();
            if (!connStatus) {
                log.severe("Failed to connect database.");
                System.exit(1);
            }

            // http server
            HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);

            // register handlers
            server.createContext("/card", new CardHandler(connector));
            server.createContext("/book", new BookHandler(connector));
            server.createContext("/borrow", new BorrowHandler(connector));

            // start server
            server.start();
            log.info("Server started on port 8081");

            // add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (connector.release()) {
                    log.info("Success to release connection.");
                } else {
                    log.warning("Failed to release connection.");
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}