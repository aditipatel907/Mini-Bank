import java.io.*;
import java.nio.file.Path;

class StatePersister {

    public static void save(Account[] accounts, Path file)
            throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(file.toFile()))) {
            out.writeObject(accounts);
        }
    }

    public static Account[] load(Path file)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(file.toFile()))) {
            return (Account[]) in.readObject();
        }
    }
}
