package mate.academy.onlinebookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.BookDto;
import mate.academy.onlinebookstore.dto.BookSearchParameters;
import mate.academy.onlinebookstore.dto.CreateBookRequestDto;
import mate.academy.onlinebookstore.service.BookService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Books management", description = "Endpoints for managing books")
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Get all books", description = "Retrieve a paginated list of all books")
    public Page<BookDto> getAll(@ParameterObject @PageableDefault(size = 20, sort = "title")
                                    Pageable pageable) {
        return bookService.getAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by ID",
            description = "Retrieve specific book details by its ID")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    @Operation(summary = "Create a new book", description = "Add a new book to the store catalog")
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto bookDto) {
        return bookService.createBook(bookDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book", description = "Soft-delete an existing book by its ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book", description = "Update details of an existing book")
    public BookDto update(@PathVariable Long id,
                          @RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.update(id, requestDto);
    }

    @GetMapping("/search")
    @Operation(summary = "Search books",
            description = "Search for books using specified parameters")
    public List<BookDto> searchBook(BookSearchParameters searchParameters) {
        return bookService.search(searchParameters);
    }
}
