package mate.academy.onlinebookstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import mate.academy.onlinebookstore.controller.BookController;
import mate.academy.onlinebookstore.dto.BookDto;
import mate.academy.onlinebookstore.dto.CreateBookRequestDto;
import mate.academy.onlinebookstore.security.JwtUtil;
import mate.academy.onlinebookstore.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Verify createBook() endpoint returns 201 Created and correct JSON")
    void createBook_ValidRequestDto_ReturnsCreated() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "Controller Book",
                "Author Name",
                "ISBN-99999",
                BigDecimal.valueOf(49.99),
                "Description",
                "image.jpg",
                java.util.List.of(1L)
        );
        BookDto responseDto =  new BookDto();
        responseDto.setId(1L);
        responseDto.setTitle("Controller Book");

        when(bookService.createBook(any())).thenReturn(responseDto);

        mockMvc.perform(post("/books").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Controller Book")
                );
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Verify getAll() endpoint returns list of books")
    void getAll_ValidRequest_ReturnsAllBooks() throws Exception {
        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("First Book");
        BookDto bookDto2 = new BookDto();
        bookDto2.setId(2L);
        bookDto2.setTitle("Second Book");
        List<BookDto> bookDtoList = List.of(bookDto, bookDto2);
        Page<BookDto> expectedPage = new PageImpl<>(bookDtoList);

        when(bookService.getAll(any())).thenReturn(expectedPage);

        mockMvc.perform(get("/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].title").value("First Book"))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[1].title").value("Second Book"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Verify getBookById() endpoint returns book when id exists")
    void getBookById_ValidRequest_ReturnsBookDto() throws Exception {
        Long bookId = 1L;
        BookDto bookDto = new BookDto();
        bookDto.setId(bookId);
        bookDto.setTitle("Book");

        when(bookService.getBookById(bookId)).thenReturn(bookDto);

        mockMvc.perform(get("/books/" + bookId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookId))
                .andExpect(jsonPath("$.title").value("Book"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Verify update() endpoint updates book and returns updated BookDto")
    void update_ValidRequest_ReturnsUpdatedBookDto() throws Exception {
        Long bookId = 1L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "Updated Title",
                "Author",
                "ISBN-99999",
                BigDecimal.valueOf(49.99),
                "Description",
                "image.jpg",
                java.util.List.of(1L)
        );

        BookDto responseDto = new BookDto();
        responseDto.setId(bookId);
        responseDto.setTitle("Updated Title");

        when(bookService.update(bookId, requestDto)).thenReturn(responseDto);

        mockMvc.perform(put("/books/" + bookId).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookId))
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Verify deleteById() endpoint returns No content status")
    void deleteById_ValidId_returnsNoContentStatus() throws Exception {
        Long bookId = 1L;

        doNothing().when(bookService).deleteById(bookId);

        mockMvc.perform(delete("/books/" + bookId).with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
