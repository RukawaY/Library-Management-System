import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;

import queries.ApiResult;
import queries.CardList;

import entities.Card;
import entities.Card.CardType;

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
        String message = "{\"error\": \"Uninitialized message\"";
        Gson gson = new Gson();

        // 处理请求：registerCard or removeCard
        // format: "{\"action\":\"register\",\"card\":{\"cardId\":1,\"name\":\"Alice\",\"department\":\"Computer Science\",\"type\":\"S\"}}"
        String action = request.split("\"")[3];
        ApiResult res = null;
        switch (action) {
            case "register":
                int cardId = gson.fromJson(request, CardJson.class).getCardId();
                String name = gson.fromJson(request, CardJson.class).getName();
                String department = gson.fromJson(request, CardJson.class).getDepartment();
                String type = gson.fromJson(request, CardJson.class).getType();
                CardType cardType = CardType.values(type);
                Card card = new Card(cardId, name, department, cardType);

                res = libsys.registerCard(card);
                if (res.ok) {
                    message = "{\"success\":\"注册借书证成功\"}";
                } else {
                    message = "{\"error\":\"注册借书证失败: " + res.message + "\"}";
                }
                break;
            case "remove":
                int cardId3 = gson.fromJson(request, CardIDJson.class).getCardID();
                res = libsys.removeCard(cardId3);
                if (res.ok) {
                    message = "{\"success\":\"删除借书证成功\"}";
                } else {
                    message = "{\"error\":\"删除借书证失败: " + res.message + "\"}";
                }
                break;
            case "modify":
                int cardId2 = gson.fromJson(request, CardJson.class).getCardId();
                String name2 = gson.fromJson(request, CardJson.class).getName();
                String department2 = gson.fromJson(request, CardJson.class).getDepartment();
                String type2 = gson.fromJson(request, CardJson.class).getType();
                CardType cardType2 = CardType.values(type2);
                Card card2 = new Card(cardId2, name2, department2, cardType2);

                res = libsys.modifyCardInfo(card2);
                if (res.ok) {
                    message = "{\"success\":\"修改借书证信息成功\"}";
                } else {
                    message = "{\"error\":\"修改借书证信息失败: " + res.message + "\"}";
                }
                break;
            default:
                message = "{\"error\":\"无效操作\"}";
                break;
        }

        // 设置响应头
        exchange.getResponseHeaders().set("Content-Type", "application/json");
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
