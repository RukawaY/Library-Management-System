package jsonHandlers;

import entities.Book;

public class BookJson {
    private String action;
    private Book book;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
