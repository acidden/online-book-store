package mate.academy.onlinebookstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Object firstObj = new BeanWrapperImpl(value).getPropertyValue(firstFieldName);
            Object secondObj = new BeanWrapperImpl(value).getPropertyValue(secondFieldName);

            return Objects.equals(firstObj, secondObj);
        } catch (final Exception ignore) {
            return false;
        }
    }
}
