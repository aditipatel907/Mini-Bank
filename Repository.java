
import Model.annotation.Id;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

class Repository<T> {
    private HashMap<String, T> data = new HashMap<>();

    public T save(T entity) {
        try {
            for (Field field : entity.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);

                    String id = String.valueOf(field.get(entity));
                    data.put(id, entity);

                    return entity;
                }
            }

            throw new RuntimeException("No @Id field found!");

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public T findById(String id) {
        return data.get(id);
    }

    public ArrayList<T> findAll() {
        return new ArrayList<>(data.values());
    }

    public boolean delete(String id) {
        return data.remove(id) != null;
    }

    @SafeVarargs
    public final void saveAll(T... items) {
        for (T item : items) {
            save(item);
        }
    }

    public static <N extends Number> double average(N[] values) {
        if (values.length == 0) {
            return 0;
        }

        double total = 0;

        for (N value : values) {
            total += value.doubleValue();
        }

        return total / values.length;
    }
}
