import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;

import queries.ApiResult;
import queries.CardList;

import entities.Card;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.List;

import jsonHandlers.CardJson;
import jsonHandlers.CardIDJson;

public class CardHandler implements HttpHandler {

    private final LibraryManagementSystem libsys;

    public CardHandler(LibraryManagementSystem libsys) {
        this.libsys = libsys;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // 设置跨域头
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        String requestMethod = exchange.getRequestMethod();
        switch (requestMethod) {
            case "GET":
                handleGetRequest(exchange);
                break;
            case "POST":
                handlePostRequest(exchange);
                break;
            case "OPTIONS":
                handleOptionsRequest(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String type = null;

        // query example: ?type=records
        if (query != null) {
            for (String param : query.split("&")) {
                String[] KeyValue = param.split("=");
                if (KeyValue[0].equals("type")) {
                    type = KeyValue[1];
                }
            }
        }

        String response = "{\"error\":\"Not initialized\"}";
        switch (type) {
            case "records":
                ApiResult res = libsys.showCards();
                CardList cardList = (CardList) res.payload;
                List<Card> cards = cardList.getCards();

                String records = "[";
                for (Card card : cards) {
                    records += card.toString() + ", ";
                }
                records = records.substring(0, records.length() - 2);
                records += "]";

                response = "{\"records\":" + records + "}";
                break;
            default:
                response = "{\"error\":\"Invalid type\"}";
                break;
        }


        // 设置响应头
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, 0);

        OutputStream outputStream = exchange.getResponseBody();
        // 发送响应
        outputStream.write(response.getBytes());
        outputStream.close();
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        // 读取请求体
        InputStream requestBody = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
        StringBuilder requestBodyBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            requestBodyBuilder.append(line);
        }

        String request = requestBodyBuilder.toString();
        String message = "Uninitialized message";
        Gson gson = new Gson();

        // 处理请求：registerCard or removeCard
        // format: "{\"action\":\"register\",\"card\":{\"cardId\":1,\"name\":\"Alice\",\"department\":\"Computer Science\",\"type\":\"S\"}}"
        String action = request.split("\"")[3];
        ApiResult res = null;
        switch (action) {
            case "register":
                Card card = gson.fromJson(request, CardJson.class).getCard();
                res = libsys.registerCard(card);
                if (res.ok) {
                    message = "Register card success";
                } else {
                    message = "Register card failed: " + res.message;
                }
                break;
            case "remove":
                int cardId = gson.fromJson(request, CardIDJson.class).getCardID();
                res = libsys.removeCard(cardId);
                if (res.ok) {
                    message = "Remove card success";
                } else {
                    message = "Remove card failed: " + res.message;
                }
                break;
            default:
                message = "Invalid action";
                break;
        }

        // 设置响应头
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(200, 0);

        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(message.getBytes());
        outputStream.close();
    }

    private void handleOptionsRequest(HttpExchange exchange) throws IOException {
        // 处理 OPTIONS 请求（用于跨域预检）
        exchange.sendResponseHeaders(204, -1); // 204 No Content
    }
}
