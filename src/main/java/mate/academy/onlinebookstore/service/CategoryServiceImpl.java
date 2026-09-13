package mate.academy.onlinebookstore.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.onlinebookstore.dto.CategoryRequestDto;
import mate.academy.onlinebookstore.dto.CategoryResponseDto;
import mate.academy.onlinebookstore.exception.EntityNotFoundException;
import mate.academy.onlinebookstore.mapper.CategoryMapper;
import mate.academy.onlinebookstore.model.Category;
import mate.academy.onlinebookstore.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> findAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDto findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category not found with id: " + id));
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public CategoryResponseDto save(CategoryRequestDto requestDto) {
        Category category = categoryMapper.toModel(requestDto);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponseDto update(Long id, CategoryRequestDto requestDto) {
        Category existingCategory = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Category not found with id: " + id)
        );
        categoryMapper.updateCategoryFromDto(requestDto, existingCategory);
        Category updatedCategory = categoryRepository.save(existingCategory);
        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Can`t delete. Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
