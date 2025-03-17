package jsonHandlers;

import entities.Borrow;

public class BorrowJson {
    private String action;
    private Borrow borrow;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Borrow getBorrow() {
        return borrow;
    }

    public void setBorrow(Borrow borrow) {
        this.borrow = borrow;
    }
}