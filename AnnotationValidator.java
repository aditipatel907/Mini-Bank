import Model.annotation.MaxLength;
import Model.annotation.Positive;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class AnnotationValidator {

    public static String[] validate(Object obj) {
        ArrayList<String> errors = new ArrayList<>();

        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            try {
                Object value = field.get(obj);

                if (field.isAnnotationPresent(Positive.class)) {
                    Positive positive =
                            field.getAnnotation(Positive.class);

                    if (value instanceof Number) {
                        if (((Number) value).doubleValue() <= 0) {
                            errors.add(field.getName() + " "
                                    + positive.message());
                        }
                    }
                }

                if (field.isAnnotationPresent(MaxLength.class)) {
                    MaxLength maxLength =
                            field.getAnnotation(MaxLength.class);

                    if (value instanceof String) {
                        if (((String) value).length()
                                > maxLength.value()) {
                            errors.add(field.getName()
                                    + " length must not exceed "
                                    + maxLength.value());
                        }
                    }
                }

            } catch (IllegalAccessException e) {
                errors.add("Unable to validate "
                        + field.getName());
            }
        }

        return errors.toArray(new String[0]);
    }
}
