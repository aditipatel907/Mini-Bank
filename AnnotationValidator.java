import Model.annotation.MaxLength;
import Model.annotation.Positive;
import java.lang.reflect.Field;

class AnnotationValidator {

    public static String[] validate(Object obj) {
        java.util.ArrayList<String> errors = new java.util.ArrayList<>();

        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            try {
                if (field.isAnnotationPresent(Positive.class)) {
                    long value = field.getLong(obj);
                    Positive annotation = field.getAnnotation(Positive.class);

                    if (value <= 0) {
                        errors.add(field.getName() + " " + annotation.message());
                    }
                }

                if (field.isAnnotationPresent(MaxLength.class)) {
                    Object value = field.get(obj);
                    MaxLength annotation = field.getAnnotation(MaxLength.class);

                    if (value instanceof String) {
                        String str = (String) value;

                        if (str.length() > annotation.value()) {
                            errors.add(field.getName() + " exceeds maximum length");
                        }
                    }
                }

            } catch (Exception e) {
                errors.add("Error validating " + field.getName());
            }
        }

        return errors.toArray(new String[0]);
    }
}