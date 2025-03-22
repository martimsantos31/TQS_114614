package pt.ua.deti.tqs;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Library {
    private final List<Book> store;

    public Library() {
        this.store = new ArrayList<>();
    }

    public void addBook(Book book) {
        store.add(book);
    }

    public List<Book> findBooksByAuthor(String author) {
        for (Book book : store) {
            if (book.getAuthor().equals(author)) {
                return List.of(book);
            }
        }

        return null;
    }

    public List<Book> findBooks(LocalDateTime from, LocalDateTime to) {
        List<Book> booksInRange = new ArrayList<>();

        for (Book book : store) {
            if (book.getPublished().isAfter(from) && book.getPublished().isBefore(to)) {
                booksInRange.add(book);
            }
        }

        return booksInRange;
    }


}
