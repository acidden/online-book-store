package mate.academy.onlinebookstore;

import java.math.BigDecimal;
import mate.academy.onlinebookstore.model.Book;
import mate.academy.onlinebookstore.service.BookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OnlineBookStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineBookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(BookService bookService) {
        return args -> {
            Book book = new Book();
            book.setTitle("Title");
            book.setAuthor("Author");
            book.setIsbn("123-0-12-345678-9");
            book.setPrice(BigDecimal.valueOf(99.00));
            book.setDescription("Some description.");
            book.setCoverImage("image.jpg");

            bookService.save(book);

            System.out.println("--- All Books in Database ---");
            bookService.findAll().forEach(System.out::println);
            System.out.println("-----------------------------");
        };
    }
}
