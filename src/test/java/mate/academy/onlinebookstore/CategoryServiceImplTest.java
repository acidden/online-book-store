package mate.academy.onlinebookstore;

import java.util.List;
import java.util.Optional;
import mate.academy.onlinebookstore.dto.CategoryRequestDto;
import mate.academy.onlinebookstore.dto.CategoryResponseDto;
import mate.academy.onlinebookstore.mapper.CategoryMapper;
import mate.academy.onlinebookstore.model.Category;
import mate.academy.onlinebookstore.repository.CategoryRepository;
import mate.academy.onlinebookstore.service.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Verify save() saves category and returns correct DTO")
    void save_ValidRequestDto_ReturnsCategoryResponseDto() {
        CategoryRequestDto requestDto = new CategoryRequestDto(
                "Fantasy",
                "Fantasy Fiction"
        );
        Category category = new Category();
        category.setName("Fantasy");
        category.setDescription("Fantasy Fiction");
        CategoryResponseDto expectedDto =  new CategoryResponseDto(
                1L,
                "Fantasy",
                "Fantasy Fiction"
        );

        when(categoryMapper.toModel(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expectedDto);

        CategoryResponseDto actualDto = categoryService.save(requestDto);

        assertNotNull(actualDto);
        assertEquals(expectedDto.id(), actualDto.id());
        assertEquals(expectedDto.name(), actualDto.name());
    }

    @Test
    @DisplayName("Verify findAll() returns list of all categories")
    void findAll_ValidRequest_ReturnsListOfCategoryResponseDto() {
        Category category =  new Category();
        category.setId(1L);
        category.setName("Comedy");

        CategoryResponseDto responseDto = new CategoryResponseDto(
                1L,
                "Comedy",
                "Comedy Books"
        );
        List<Category> categoryList = List.of(category);

        when(categoryRepository.findAll()).thenReturn(categoryList);
        when(categoryMapper.toDto(category)).thenReturn(responseDto);

        List<CategoryResponseDto> actualList = categoryService.findAll();

        assertNotNull(actualList);
        assertEquals(1, actualList.size());
        assertEquals("Comedy", actualList.get(0).name());
    }

    @Test
    @DisplayName("Verify findById() returns correct DTO when category exists")
    void findById_ValidId_ReturnsCategoryResponseDto() {
        Long categoryId = 1L;

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Drama");

        CategoryResponseDto expectedDto = new CategoryResponseDto(
                1L,
                "Drama",
                "Drama books"
        );

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expectedDto);

        CategoryResponseDto actualDto = categoryService.findById(categoryId);

        assertNotNull(actualDto);
        assertEquals(expectedDto.name(), actualDto.name());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    @DisplayName("Verify deleteById() calls repository method")
    void deleteById_ValidId_ExecutesSuccessfully() {
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);

        when(categoryRepository.existsById(any())).thenReturn(true);
        categoryService.deleteById(categoryId);

        verify(categoryRepository, times(1)).deleteById(categoryId);
    }
}
