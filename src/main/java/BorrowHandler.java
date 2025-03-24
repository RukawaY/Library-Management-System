import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import queries.ApiResult;
import queries.BorrowHistories;
import queries.BorrowHistories.Item;

import java.io.IOException;
import java.io.OutputStream;

import java.util.List;

public class BorrowHandler implements HttpHandler {

    private final LibraryManagementSystem libsys;

    public BorrowHandler(LibraryManagementSystem libsys) {
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

        // query example: ?type=records&cardId=123456

        int cardId = -1;
        if (query != null) {
            for (String param : query.split("&")) {
                String[] KeyValue = param.split("=");
                if (KeyValue[0].equals("type")) {
                    type = KeyValue[1];
                }
                if (KeyValue[0].equals("cardId")) {
                    cardId = Integer.parseInt(KeyValue[1]);
                }
            }
        }

        String response = "{\"error\":\"Not initialized\"}";
        switch (type) {
            case "records":
                // 获取借阅记录
                if (cardId == -1) {
                    response = "{\"error\":\"Invalid cardId\"}";
                } else {
                    ApiResult res = libsys.showBorrowHistory(cardId);
                    if (res.ok) {
                        BorrowHistories borrowHistories = (BorrowHistories) res.payload;                       
                        List<Item> borrowRecords = borrowHistories.getItems();
                        String records = "[";
                        for (Item record : borrowRecords) {
                            records += record.toString() + ", ";
                        }
                        records = records.substring(0, records.length() - 2);
                        records += "]";

                        response = "{\"records\":" + records + "}";
                    } else {
                        response = "{\"error\":\"" + res.message + "\"}";
                    }
                }
                break;
            default:
                response = "{\"error\":\"Invalid type\"}";
                break;
        }
        
        // 设置响应头
        exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, 0);

        OutputStream outputStream = exchange.getResponseBody();
        // 发送响应
        outputStream.write(response.getBytes());
        outputStream.close();
    }

    private void handleOptionsRequest(HttpExchange exchange) throws IOException {
        // 处理 OPTIONS 请求（用于跨域预检）
        exchange.sendResponseHeaders(204, -1); // 204 No Content
    }
}

