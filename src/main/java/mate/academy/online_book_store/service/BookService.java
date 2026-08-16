package mate.academy.online_book_store.service;

import mate.academy.online_book_store.model.Book;
import java.util.List;

public interface BookService {
    Book save(Book book);
    List<Book> findAll();
}
