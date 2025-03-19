import entities.Book;
import entities.Borrow;
import entities.Card;
import entities.Book.SortColumn;
import entities.Card.CardType;
import queries.*;
import queries.BorrowHistories.Item;
import scorex.util.ArrayList;
import utils.DBInitializer;
import utils.DatabaseConnector;

import java.sql.*;
import java.util.List;

public class LibraryManagementSystemImpl implements LibraryManagementSystem {

    private final DatabaseConnector connector;

    public LibraryManagementSystemImpl(DatabaseConnector connector) {
        this.connector = connector;
    }

    @Override
    public ApiResult storeBook(Book book) {
        Connection conn = connector.getConn();

        // check if book already exists
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM book WHERE category =? AND title =? AND press =? AND publish_year =? AND author =?");
            stmt.setString(1, book.getCategory());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getPress());
            stmt.setInt(4, book.getPublishYear());
            stmt.setString(5, book.getAuthor());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ApiResult(false, "Book already exists!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }

        // insert book into database
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO book (category, title, press, publish_year, author, price, stock) VALUES (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, book.getCategory());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getPress());
            stmt.setInt(4, book.getPublishYear());
            stmt.setString(5, book.getAuthor());
            stmt.setDouble(6, book.getPrice());
            stmt.setInt(7, book.getStock());
            stmt.executeUpdate();

            // update book.id using auto-generated id in the database
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                book.setBookId(generatedId);
            }

            commit(conn);
            return new ApiResult(true, "Successfully stored book!");
        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }
    }

    @Override
    public ApiResult incBookStock(int bookId, int deltaStock) {
        Connection conn = connector.getConn();

        // check whether the book exists
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM book WHERE book_id =?");
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return new ApiResult(false, "Book does not exist!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }

        // update book stock
        try {
            // negative stock check
            PreparedStatement stmt = conn.prepareStatement("SELECT stock FROM book WHERE book_id =?");
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int stock = rs.getInt(1);
                if (stock + deltaStock < 0) {
                    return new ApiResult(false, "Book stock cannot be negative!");
                }
            }

            stmt = conn.prepareStatement("UPDATE book SET stock = stock +? WHERE book_id =?");
            stmt.setInt(1, deltaStock);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
            commit(conn);
            return new ApiResult(true, "Successfully updated book stock!");
        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }
    }

    @Override
    public ApiResult storeBook(List<Book> books) {
        Connection conn = connector.getConn();

        // check if any book already exists
        try {
            for (Book book : books) {
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM book WHERE category =? AND title =? AND press =? AND publish_year =? AND author =?");
                stmt.setString(1, book.getCategory());
                stmt.setString(2, book.getTitle());
                stmt.setString(3, book.getPress());
                stmt.setInt(4, book.getPublishYear());
                stmt.setString(5, book.getAuthor());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new ApiResult(false, "Book already exists!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }

        // use addBatch and executeBatch to insert multiple books
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO book (category, title, press, publish_year, author, price, stock) VALUES (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            for (Book book : books) {
                stmt.setString(1, book.getCategory());
                stmt.setString(2, book.getTitle());
                stmt.setString(3, book.getPress());
                stmt.setInt(4, book.getPublishYear());
                stmt.setString(5, book.getAuthor());
                stmt.setDouble(6, book.getPrice());
                stmt.setInt(7, book.getStock());
                stmt.addBatch();
            }
            stmt.executeBatch();

            // update book.id using auto-generated id in the database
            ResultSet rs = stmt.getGeneratedKeys();
            int index = 0;
            while (rs.next()) {
                int generatedId = rs.getInt(1);
                books.get(index).setBookId(generatedId);
                index++;
            }

            commit(conn);
            return new ApiResult(true, "Successfully stored books!");
        } catch (Exception e) {
            rollback(conn);
            e.printStackTrace();
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }
    }

    @Override
    public ApiResult removeBook(int bookId) {
        Connection conn = connector.getConn();

        // check if book exists
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM book WHERE book_id =?");
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return new ApiResult(false, "Book does not exist!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }

        // if someone didn't return the book, then fail to remove
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM borrow WHERE book_id =? AND return_time = 0");
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ApiResult(false, "Cannot remove book because it is borrowed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }

        // Conduct the removal
        try {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM book WHERE book_id =?");
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
            commit(conn);
            return new ApiResult(true, "Successfully removed book!");
        } catch (Exception e) {
            rollback(conn);
            e.printStackTrace();
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }
    }

    @Override
    public ApiResult modifyBookInfo(Book book) {
        Connection conn = connector.getConn();

        // check if book exists
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM book WHERE book_id =?");
            stmt.setInt(1, book.getBookId());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return new ApiResult(false, "Book does not exist!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }

        // Modify
        try {
            PreparedStatement stmt = conn.prepareStatement("UPDATE book SET category =?, title =?, press =?, publish_year =?, author =?, price =? WHERE book_id =?");
            stmt.setString(1, book.getCategory());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getPress());
            stmt.setInt(4, book.getPublishYear());
            stmt.setString(5, book.getAuthor());
            stmt.setDouble(6, book.getPrice());
            stmt.setInt(7, book.getBookId());
            stmt.executeUpdate();
            commit(conn);
            return new ApiResult(true, "Successfully modified book info!");
        } catch (Exception e) {
            rollback(conn);
            e.printStackTrace();
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }
    }

    @Override
    public ApiResult queryBook(BookQueryConditions conditions) {
        Connection conn = connector.getConn();

        try {
            StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM book WHERE 1 = 1");
            List<Object> params = new ArrayList<>();

            if (conditions.getCategory() != null) {
                sqlBuilder.append(" AND category = ?");
                params.add(conditions.getCategory());
            }
            if (conditions.getTitle() != null) {
                sqlBuilder.append(" AND title LIKE ?");
                params.add("%" + conditions.getTitle() + "%");
            }
            if (conditions.getPress() != null) {
                sqlBuilder.append(" AND press LIKE ?");
                params.add("%" + conditions.getPress() + "%");
            }
            if (conditions.getMinPublishYear() != null) {
                sqlBuilder.append(" AND publish_year >= ?");
                params.add(conditions.getMinPublishYear());
            }
            if (conditions.getMaxPublishYear() != null) {
                sqlBuilder.append(" AND publish_year <= ?");
                params.add(conditions.getMaxPublishYear());
            }
            if (conditions.getAuthor()  != null) {
                sqlBuilder.append(" AND author LIKE ?");
                params.add("%" + conditions.getAuthor() + "%");
            }
            if (conditions.getMinPrice() != null) {
                sqlBuilder.append(" AND price >= ?");
                params.add(conditions.getMinPrice());
            }
            if (conditions.getMaxPrice() != null) {
                sqlBuilder.append(" AND price <= ?");
                params.add(conditions.getMaxPrice());
            }

            sqlBuilder.append(" ORDER BY ");

            // sprt according to sortBy and sortOrder
            if (conditions.getSortBy() != null) {
                SortColumn cond = conditions.getSortBy();
                SortOrder ord = conditions.getSortOrder();
                if (cond == Book.SortColumn.BOOK_ID) {
                    sqlBuilder.append("book_id ");
                } else if (cond == Book.SortColumn.CATEGORY) {
                    sqlBuilder.append("category ");
                } else if (cond == Book.SortColumn.TITLE) {
                    sqlBuilder.append("title ");
                } else if (cond == Book.SortColumn.PRESS) {
                    sqlBuilder.append("press ");
                } else if (cond == Book.SortColumn.PUBLISH_YEAR) {
                    sqlBuilder.append("publish_year ");
                } else if (cond == Book.SortColumn.AUTHOR) {
                    sqlBuilder.append("author ");
                } else if (cond == Book.SortColumn.PRICE) {
                    sqlBuilder.append("price ");
                } else if (cond == Book.SortColumn.STOCK) {
                    sqlBuilder.append("stock ");
                }

                if (ord == SortOrder.ASC) {
                    sqlBuilder.append("ASC");
                } else {
                    sqlBuilder.append("DESC");
                }
                
                if (cond != Book.SortColumn.BOOK_ID) {
                    sqlBuilder.append(", book_id ASC");
                }
            } else {
                sqlBuilder.append("book_id ASC");
            }

            PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString());
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            List<Book> books = new ArrayList<>();
            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt(1));
                book.setCategory(rs.getString(2));
                book.setTitle(rs.getString(3));
                book.setPress(rs.getString(4));
                book.setPublishYear(rs.getInt(5));
                book.setAuthor(rs.getString(6));
                book.setPrice(rs.getDouble(7));
                book.setStock(rs.getInt(8));
                books.add(book);
            }

            BookQueryResults results = new BookQueryResults(books);
            commit(conn);
            return new ApiResult(true, "Successfully queried books!", results);
        } catch (Exception e) {
            rollback(conn);
            e.printStackTrace();
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }
    }

    private static final Object lock = new Object();
    
    @Override
    public ApiResult borrowBook(Borrow borrow) {
        synchronized (lock) {
            Connection conn = connector.getConn();

            // check the validity of book id
            try {
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM book WHERE book_id =?");
                stmt.setInt(1, borrow.getBookId());
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    return new ApiResult(false, "Book does not exist!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new ApiResult(false, "Database Error:" + e.getMessage());
            }

            // check the validity of card id
            try {
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM card WHERE card_id =?");
                stmt.setInt(1, borrow.getCardId());
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    return new ApiResult(false, "Card does not exist!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new ApiResult(false, "Database Error:" + e.getMessage());
            }

            // check if there is enough stock
            try {
                PreparedStatement stmt = conn.prepareStatement("SELECT stock FROM book WHERE book_id =?");
                stmt.setInt(1, borrow.getBookId());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int stock = rs.getInt(1);
                    if (stock <= 0) {
                        return new ApiResult(false, "Book stock is empty!");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new ApiResult(false, "Database Error:" + e.getMessage());
            }

            // check whether the user has borrowed the book before
            try {
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM borrow WHERE book_id =? AND card_id =? AND return_time = 0");
                stmt.setInt(1, borrow.getBookId());
                stmt.setInt(2, borrow.getCardId());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new ApiResult(false, "You have already borrowed this book!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new ApiResult(false, "Database Error:" + e.getMessage());
            }

            // Conduct the borrow and update the stock
            try {
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO borrow (card_id, book_id, borrow_time) VALUES (?,?,?)");
                stmt.setInt(1, borrow.getCardId());
                stmt.setInt(2, borrow.getBookId());
                stmt.setLong(3, borrow.getBorrowTime());
                stmt.executeUpdate();

                stmt = conn.prepareStatement("UPDATE book SET stock = stock - 1 WHERE book_id =?");
                stmt.setInt(1, borrow.getBookId());
                stmt.executeUpdate();

                commit(conn);
                return new ApiResult(true, "Successfully borrowed book!");
            } catch (Exception e) {
                rollback(conn);
                e.printStackTrace();
                return new ApiResult(false, "Database Error:" + e.getMessage());
            }
        }
    }

    @Override
    public ApiResult returnBook(Borrow borrow) {
        Connection conn = connector.getConn();

        // check if the book is borrowed by the user and not yet returned
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM borrow WHERE book_id =? AND card_id =? AND return_time = 0");
            stmt.setInt(1, borrow.getBookId());
            stmt.setInt(2, borrow.getCardId());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return new ApiResult(false, "You have not borrowed this book!");
            }

            long borrowTime = rs.getLong(3);

            // if return_time <= borrow_time, then fail to return
            if (borrow.getReturnTime() <= borrowTime) {
                return new ApiResult(false, "Return time must be later than borrow time!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }

        // return the book and update the stock
        try {
            PreparedStatement stmt = conn.prepareStatement("UPDATE borrow SET return_time =? WHERE book_id =? AND card_id =? AND return_time = 0");
            stmt.setLong(1, borrow.getReturnTime());
            stmt.setInt(2, borrow.getBookId());
            stmt.setInt(3, borrow.getCardId());
            stmt.executeUpdate();

            stmt = conn.prepareStatement("UPDATE book SET stock = stock + 1 WHERE book_id =?");
            stmt.setInt(1, borrow.getBookId());
            stmt.executeUpdate();

            commit(conn);
            return new ApiResult(true, "Successfully returned book!");
        } catch (Exception e) {
            rollback(conn);
            e.printStackTrace();
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }
    }

    @Override
    public ApiResult showBorrowHistory(int cardId) {
        Connection conn = connector.getConn();

        // query the borrow history
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT card_id, book_id, category, title, press, publish_year, author, price, borrow_time, return_time " +
                "FROM card NATURAL JOIN borrow NATURAL JOIN book " +
                "WHERE card_id =? " +
                "ORDER BY borrow_time DESC, book_id ASC"
            );
            stmt.setInt(1, cardId);
            ResultSet rs = stmt.executeQuery();

            List<Item> borrows = new ArrayList<>();
            while (rs.next()) {
                Item item = new Item();
                item.setCardId(rs.getInt(1));
                item.setBookId(rs.getInt(2));
                item.setCategory(rs.getString(3));
                item.setTitle(rs.getString(4));
                item.setPress(rs.getString(5));
                item.setPublishYear(rs.getInt(6));
                item.setAuthor(rs.getString(7));
                item.setPrice(rs.getDouble(8));
                item.setBorrowTime(rs.getLong(9));
                item.setReturnTime(rs.getLong(10));
                borrows.add(item);
            }
                    
            BorrowHistories borrow_histories = new BorrowHistories(borrows);

            commit(conn);
            return new ApiResult(true, "Successfully queried borrow history!", borrow_histories);
        } catch (Exception e) {
            rollback(conn);
            e.printStackTrace();
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }
    }

    @Override
    public ApiResult registerCard(Card card) {
        Connection conn = connector.getConn();

        // check if the card already exists
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM card WHERE name =? AND department =? AND type =?");
            stmt.setString(1, card.getName());
            stmt.setString(2, card.getDepartment());
            stmt.setString(3, card.getType().getStr());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ApiResult(false, "Card already exists!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }

        // insert the card
        try { 
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO card (name, department, type) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, card.getName());
            stmt.setString(2, card.getDepartment());
            stmt.setString(3, card.getType().getStr());
            stmt.executeUpdate();

            // update card.id using auto-generated id in the database
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                card.setCardId(generatedId);
            }

            commit(conn);
            return new ApiResult(true, "Successfully registered card!");
        } catch (Exception e) {
            rollback(conn);
            e.printStackTrace();
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }
    }

    @Override
    public ApiResult removeCard(int cardId) {
        Connection conn = connector.getConn();

        // check if the card exists
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM card WHERE card_id =?");
            stmt.setInt(1, cardId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return new ApiResult(false, "Card does not exist!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }

        // if current card is borrowing any book, then fail to remove
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM borrow WHERE card_id =? AND return_time = 0");
            stmt.setInt(1, cardId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ApiResult(false, "Cannot remove card because it is borrowing a book!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }

        // Conduct the removal
        try {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM card WHERE card_id =?");
            stmt.setInt(1, cardId);
            stmt.executeUpdate();

            commit(conn);
            return new ApiResult(true, "Successfully removed card!");
        } catch (Exception e) {
            rollback(conn);
            e.printStackTrace();
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }
    }

    @Override
    public ApiResult showCards() {
        Connection conn = connector.getConn();

        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM card ORDER BY card_id ASC");
            ResultSet rs = stmt.executeQuery();

            List<Card> cards = new ArrayList<>();
            while (rs.next()) {
                Card card = new Card();
                card.setCardId(rs.getInt(1));
                card.setName(rs.getString(2));
                card.setDepartment(rs.getString(3));
                card.setType(CardType.values(rs.getString(4)));
                cards.add(card);
            }

            CardList card_list = new CardList(cards);
            commit(conn);
            return new ApiResult(true, "Successfully queried cards!", card_list);
        } catch (Exception e) {
            rollback(conn);
            e.printStackTrace();
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }
    }

    @Override
    public ApiResult modifyCardInfo(Card card) {
        Connection conn = connector.getConn();

        // check if card exists
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM card WHERE card_id =?");
            stmt.setInt(1, card.getCardId());
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return new ApiResult(false, "Card does not exist!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }

        // update card info
        try {
            PreparedStatement stmt = conn.prepareStatement("UPDATE card SET name =?, department =?, type =? WHERE card_id =?");
            stmt.setString(1, card.getName());
            stmt.setString(2, card.getDepartment());
            stmt.setString(3, card.getType().getStr());
            stmt.setInt(4, card.getCardId());
            stmt.executeUpdate();

            commit(conn);
            return new ApiResult(true, "Successfully modified card info!");
        } catch (Exception e) {
            rollback(conn);
            e.printStackTrace();
            return new ApiResult(false, "Database Error:" + e.getMessage());
        }
    }

    @Override
    public ApiResult resetDatabase() {
        Connection conn = connector.getConn();
        try {
            Statement stmt = conn.createStatement();
            DBInitializer initializer = connector.getConf().getType().getDbInitializer();
            stmt.addBatch(initializer.sqlDropBorrow());
            stmt.addBatch(initializer.sqlDropBook());
            stmt.addBatch(initializer.sqlDropCard());
            stmt.addBatch(initializer.sqlCreateCard());
            stmt.addBatch(initializer.sqlCreateBook());
            stmt.addBatch(initializer.sqlCreateBorrow());
            stmt.executeBatch();
            commit(conn);
        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
        return new ApiResult(true, null);
    }

    private void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void commit(Connection conn) {
        try {
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
