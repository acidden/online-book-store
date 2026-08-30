package mate.academy.onlinebookstore.service;

import java.util.List;
import mate.academy.onlinebookstore.dto.BookDto;
import mate.academy.onlinebookstore.dto.BookSearchParameters;
import mate.academy.onlinebookstore.dto.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto createBook(CreateBookRequestDto requestDto);

    BookDto getBookById(Long id);

    List<BookDto> getAll(Pageable pageable);

    BookDto update(Long id, CreateBookRequestDto requestDto);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParameters params);
}
