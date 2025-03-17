import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import entities.Card;
import entities.Book;
import entities.Borrow;
import entities.Book.SortColumn;
import jsonHandlers.*;
import queries.SortOrder;
import queries.ApiResult;
import queries.CardList;
import queries.BookQueryConditions;
import queries.BookQueryResults;

import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;
import java.util.List;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class BookHandler implements HttpHandler {

    private final LibraryManagementSystem libsys;

    public BookHandler(LibraryManagementSystem libsys) {
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

        // query example: ?type=records&category=fiction&title=The+Great+Gatsby
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
                // construct query conditions
                BookQueryConditions conditions = new BookQueryConditions();

                for (String param : query.split("&")) {
                    String[] KeyValue = param.split("=");
                    switch (KeyValue[0]) {
                        case "category":
                            conditions.setCategory(KeyValue[1]);
                            break;
                        case "title":
                            conditions.setTitle(KeyValue[1]);
                            break;
                        case "press":
                            conditions.setPress(KeyValue[1]);
                            break;
                        case "min-publish-year":
                            conditions.setMinPublishYear(Integer.parseInt(KeyValue[1]));
                            break;
                        case "max-publish-year":
                            conditions.setMaxPublishYear(Integer.parseInt(KeyValue[1]));
                            break;
                        case "author":
                            conditions.setAuthor(KeyValue[1]);
                            break;
                        case "minprice":
                            conditions.setMinPrice(Double.parseDouble(KeyValue[1]));
                            break;
                        case "maxprice":
                            conditions.setMaxPrice(Double.parseDouble(KeyValue[1]));
                            break;
                        case "sortby":
                            conditions.setSortBy(SortColumn.valueOf(KeyValue[1]));
                            break;
                        case "sortorder":
                            conditions.setSortOrder(SortOrder.valueOf(KeyValue[1]));
                            break;
                        }
                }

                ApiResult res = libsys.queryBook(conditions);
                if (res.ok) {
                    BookQueryResults results = (BookQueryResults) res.payload;
                    List<Book> books = results.getResults();

                    String tempResult = "[";
                    for (Book book : books) {
                        tempResult += book.toString() + ", ";
                    }
                    tempResult = tempResult.substring(0, tempResult.length() - 2);
                    tempResult += "]";

                    response = "{\"count\":" + results.getCount() + ", \"results\":" + tempResult + "}";
                } else {
                    response = "{\"error\":\"" + res.message + "\"}";
                }

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

        // 处理请求
        String action = request.split("\"")[3];
        ApiResult res = null;
        switch (action) {
            case "store":
                Book book = gson.fromJson(request, BookJson.class).getBook();
                res = libsys.storeBook(book);
                if (res.ok) {
                    message = "Successfully stored book";
                } else {
                    message = "Failed to store book: " + res.message;
                }
                break;
            case "incstock":
                int bookId = gson.fromJson(request, BookIDAmtJson.class).getBookID();
                int amount = gson.fromJson(request, BookIDAmtJson.class).getAmount();
                res = libsys.incBookStock(bookId, amount);
                if (res.ok) {
                    message = "Successfully increased stock";
                } else {
                    message = "Failed to increase stock: " + res.message;
                }
                break;
            case "storemulti":
                List<Book> books = gson.fromJson(request, MultiBookJson.class).getBooks();
                res = libsys.storeBook(books);
                if (res.ok) {
                    message = "Successfully stored books";
                } else {
                    message = "Failed to store books: " + res.message;
                }
                break;
            case "remove":
                int bookid = gson.fromJson(request, BookIDJson.class).getBookID();
                res = libsys.removeBook(bookid);
                if (res.ok) {
                    message = "Successfully removed book";
                } else {
                    message = "Failed to remove book: " + res.message;
                }
                break;
            case "modify":
                Book book2 = gson.fromJson(request, BookJson.class).getBook();
                res = libsys.modifyBookInfo(book2);
                if (res.ok) {
                    message = "Successfully modified book";
                } else {
                    message = "Failed to modify book: " + res.message;
                }
                break;
            case "borrow":
                Borrow borrow1 = gson.fromJson(request, BorrowJson.class).getBorrow();
                res = libsys.borrowBook(borrow1);
                if (res.ok) {
                    message = "Successfully borrowed book";
                } else {
                    message = "Failed to borrow book: " + res.message;
                }
                break;
            case "return":
                Borrow borrow2 = gson.fromJson(request, BorrowJson.class).getBorrow();
                res = libsys.returnBook(borrow2);
                if (res.ok) {
                    message = "Successfully returned book";
                } else {
                    message = "Failed to return book: " + res.message;
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

