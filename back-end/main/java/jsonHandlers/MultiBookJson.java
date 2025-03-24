package jsonHandlers;

import entities.Book;
import java.util.List;

public class MultiBookJson {
    private String action;
    private List<Book> books;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
