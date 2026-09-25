package mate.academy.onlinebookstore;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import mate.academy.onlinebookstore.dto.BookDto;
import mate.academy.onlinebookstore.dto.CreateBookRequestDto;
import mate.academy.onlinebookstore.exception.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.BookMapper;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.model.Category;
import mate.academy.onlinebookstore.repository.BookRepository;
import mate.academy.onlinebookstore.repository.CategoryRepository;
import mate.academy.onlinebookstore.service.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private CategoryRepository categoryRepository;


    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Verify createBook() saves a book and returns correct DTO")
    void createBook_ValidRequestDto_ReturnsBookDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "New Book",
                "Author Name",
                "ISBN-12345",
                new BigDecimal("29.99"),
                "Description",
                "image.jpg",
                List.of(1L)
        );
        Book book = new Book();
        book.setTitle("New Book");
        BookDto expectedDto = new BookDto();
        expectedDto.setId(1L);
        expectedDto.setTitle("New Book");
        Category category = new Category();
        category.setId(1L);

        when(categoryRepository.getReferenceById(1L)).thenReturn(category);
        when(bookMapper.toModel(any(CreateBookRequestDto.class))).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toDto(any(Book.class))).thenReturn(expectedDto);

        BookDto actualDto = bookService.createBook(requestDto);

        assertNotNull(actualDto);
        assertEquals(expectedDto.getTitle(), actualDto.getTitle());
    }

    @Test
    @DisplayName("Verify getBookById() returns correct DTO when book exists")
    void getBookById_ValidId_ReturnsBookDto() {
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Book Title");

        BookDto expectedDto = new BookDto();
        expectedDto.setId(bookId);
        expectedDto.setTitle("Book Title");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expectedDto);

        BookDto actualDto = bookService.getBookById(bookId);

        assertNotNull(actualDto);
        assertEquals(expectedDto.getTitle(), actualDto.getTitle());
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    @DisplayName("Verify getBookById() throws EntityNotFoundException when book does not exist")
    void getBookById_InvalidId_ThrowsException() {

        Long bookId = 100L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            bookService.getBookById(bookId);
        });

        verify(bookRepository, times(1)).findById(bookId);
        verifyNoInteractions(bookMapper);
    }

    @Test
    @DisplayName("Verify getAll() return page of books")
    void getAll_ValidPageable_ReturnsPageOfBookDto() {
        Pageable pageable = PageRequest.of(0,10);

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book Title");

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("Book Title");

        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        Page<BookDto> actualPage = bookService.getAll(pageable);

        assertNotNull(actualPage);
        assertEquals(1, actualPage.getContent().size());
        assertEquals("Book Title", actualPage.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("Verify update() method modifies and returns valid Dto")
    void update_ValidIdAndRequestDto_ReturnsUpdatedBookDto() {
        Long bookId = 1L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "Updated Title",
                "Author",
                "ISBN-1234567890",
                new BigDecimal("29.99"),
                "Desc",
                "123.jpg",
                List.of(1L)
        );
        Book existingBook = new Book();
        existingBook.setId(bookId);
        existingBook.setTitle("Title");
        Category category = new Category();
        category.setId(1L);

        BookDto expectedDto = new BookDto();
        expectedDto.setId(bookId);
        expectedDto.setTitle("Updated Title");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(categoryRepository.getReferenceById(1L)).thenReturn(category);
        doNothing().when(bookMapper).updateBookFromDto(requestDto, existingBook);
        when(bookRepository.save(existingBook)).thenReturn(existingBook);
        when(bookMapper.toDto(existingBook)).thenReturn(expectedDto);

        BookDto actualDto = bookService.update(bookId, requestDto);

        assertNotNull(actualDto);
        assertEquals("Updated Title", actualDto.getTitle());
    }

    @Test
    @DisplayName("Verify deleteById() calls repository method")
    void deleteById_ValidId_ExecutesSuccessfully() {
        Long bookId = 1L;

        bookService.deleteById(bookId);

        verify(bookRepository, times(1)).deleteById(bookId);
    }

}
